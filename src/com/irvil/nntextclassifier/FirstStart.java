package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.AlreadyExistsException;
import com.irvil.nntextclassifier.dao.EmptyRecordException;
import com.irvil.nntextclassifier.dao.NotExistsException;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import com.irvil.nntextclassifier.recognizer.Recognizer;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class FirstStart {
  private DAOFactory daoFactory;
  private NGramStrategy nGram;

  public FirstStart(DAOFactory daoFactory, NGramStrategy nGram) {
    if (daoFactory == null) {
      throw new IllegalArgumentException();
    }

    this.daoFactory = daoFactory;
    this.nGram = nGram;
  }

  public static void main(String[] args) throws Exception {
    DAOFactory daoFactory = new MainWindow().getDaoFactory();
    Config config = Config.getInstance();

    if (daoFactory == null) {
      System.out.println("Oops, it seems there is an error in config file");
      return;
    }

    //

    FirstStart fs = new FirstStart(daoFactory, new FilteredUnigram());
    List<IncomingCall> incomingCalls = fs.convertXLSXtoIncomingCalls("./etc/1.xlsx");

    fs.createDbFolder(config.getDbPath());
    fs.createStorage(daoFactory);
    fs.fillStorage(incomingCalls);
    fs.trainRecognizers(daoFactory, config.getDbPath());
  }

  private void trainRecognizers(DAOFactory daoFactory, String pathToSave) {
    List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
    List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();
    List<IncomingCall> incomingCallsForTrain = daoFactory.incomingCallDAO().getAll();

    // train Recognizer for each Characteristic from DB
    //

    for (Characteristic characteristic : characteristics) {
      Recognizer recognizer = new Recognizer(characteristic, vocabulary, nGram);
      recognizer.train(incomingCallsForTrain);
      recognizer.saveTrainedRecognizer(new File(pathToSave + "/" + recognizer.toString()));
    }

    Recognizer.shutdown();
  }

  private void createStorage(DAOFactory daoFactory) {
    StorageCreator storageCreator = daoFactory.storageCreator();
    storageCreator.createStorage();
    storageCreator.clearStorage();
  }

  private List<IncomingCall> convertXLSXtoIncomingCalls(String xlsxFile) throws IOException {
    List<Characteristic> characteristics = new ArrayList<>();
    List<IncomingCall> incomingCalls = new ArrayList<>();

    XSSFWorkbook excelFile = new XSSFWorkbook(new FileInputStream(xlsxFile));
    XSSFSheet sheet = excelFile.getSheetAt(1);

    // create Characteristics
    // first row contains Characteristics names from second to last columns
    //

    for (int i = 1; i < sheet.getRow(0).getLastCellNum(); i++) {
      characteristics.add(new Characteristic(sheet.getRow(0).getCell(i).getStringCellValue()));
    }

    // fill IncomingCalls
    // start from second row
    //

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      Map<Characteristic, CharacteristicValue> characteristicsValues = new HashMap<>();

      for (int j = 1; j < sheet.getRow(i).getLastCellNum(); j++) {
        characteristicsValues.put(characteristics.get(j - 1), new CharacteristicValue(sheet.getRow(i).getCell(j).getStringCellValue()));
      }

      incomingCalls.add(new IncomingCall(sheet.getRow(i).getCell(0).getStringCellValue(), characteristicsValues));
    }

    excelFile.close();
    return incomingCalls;
  }

  private void createDbFolder(String path) {
    new File(path).mkdir();
  }

  private void fillStorage(List<IncomingCall> incomingCalls) {
    fillVocabulary(incomingCalls);
    fillCharacteristics(incomingCalls);
    fillIncomingCalls(incomingCalls);
  }

  private void fillIncomingCalls(List<IncomingCall> incomingCalls) {
    for (IncomingCall incomingCall : incomingCalls) {
      try {
        daoFactory.incomingCallDAO().add(incomingCall);
      } catch (EmptyRecordException | NotExistsException ignored) {
      }
    }
  }

  private void fillCharacteristics(List<IncomingCall> incomingCalls) {
    Set<Characteristic> characteristics = getCharacteristicsCatalog(incomingCalls);

    // save characteristics in Storage
    //

    for (Characteristic characteristic : characteristics) {
      try {
        daoFactory.characteristicDAO().addCharacteristic(characteristic);
      } catch (EmptyRecordException | AlreadyExistsException ignored) {
      }
    }
  }

  private Set<Characteristic> getCharacteristicsCatalog(List<IncomingCall> incomingCalls) {
    Map<Characteristic, Characteristic> characteristics = new HashMap<>();

    for (IncomingCall incomingCall : incomingCalls) {
      // for all incoming calls characteristics
      //

      for (Map.Entry<Characteristic, CharacteristicValue> entry : incomingCall.getCharacteristics().entrySet()) {
        // add characteristic to Map
        characteristics.put(entry.getKey(), entry.getKey());

        // add characteristic value to possible values
        characteristics.get(entry.getKey()).addPossibleValue(entry.getValue());
      }
    }

    return characteristics.keySet();
  }

  private void fillVocabulary(List<IncomingCall> incomingCalls) {
    // create vocabulary from all IncomingCalls
    Set<String> vocabulary = getVocabularyFromIncommingCallsTexts(incomingCalls);

    // save vocabulary words in Storage
    //

    for (String word : vocabulary) {
      try {
        daoFactory.vocabularyWordDAO().add(new VocabularyWord(word));
      } catch (EmptyRecordException | AlreadyExistsException ignored) {
      }
    }
  }

  private Set<String> getVocabularyFromIncommingCallsTexts(List<IncomingCall> incomingCalls) {
    Set<String> vocabulary = new LinkedHashSet<>();

    // add words (converted to n-gram) from all IncomingCalls to vocabulary
    //

    for (IncomingCall incomingCall : incomingCalls) {
      vocabulary.addAll(nGram.getNGram(incomingCall.getText()));
    }

    return vocabulary;
  }
}
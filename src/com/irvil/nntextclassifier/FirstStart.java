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
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import com.irvil.nntextclassifier.observer.Observable;
import com.irvil.nntextclassifier.observer.Observer;
import com.irvil.nntextclassifier.recognizer.Recognizer;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

// todo: create tests
class FirstStart implements Observable {
  private DAOFactory daoFactory;
  private NGramStrategy nGram;
  private List<Observer> observers = new ArrayList<>();

  FirstStart(DAOFactory daoFactory, NGramStrategy nGram) {
    if (daoFactory == null || nGram == null) {
      throw new IllegalArgumentException();
    }

    this.daoFactory = daoFactory;
    this.nGram = nGram;
  }

  static boolean createDbFolder(String path) {
    return new File(path).mkdir();
  }

  void trainAndSaveRecognizers(String pathToSave) {
    List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
    List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();
    List<IncomingCall> incomingCallsForTrain = daoFactory.incomingCallDAO().getAll();

    // train Recognizer for each Characteristic from DB
    //

    for (Characteristic characteristic : characteristics) {
      Recognizer recognizer = new Recognizer(characteristic, vocabulary, nGram);

      // add all FirstStart observers to Recognizer observers
      //

      for (Observer o : observers) {
        recognizer.addObserver(o);
      }

      // train and save recognizer
      //

      recognizer.train(incomingCallsForTrain);
      recognizer.saveTrainedRecognizer(new File(pathToSave + "/" + recognizer.toString()));
    }

    notifyObservers("\nPlease restart the program.");
    Recognizer.shutdown();
  }

  void createStorage() {
    StorageCreator storageCreator = daoFactory.storageCreator();
    storageCreator.createStorage();
    storageCreator.clearStorage();
    notifyObservers("Storage created. Wait for storage filling...");
  }

  List<IncomingCall> readXlsxFile(File xlsxFile) {
    List<IncomingCall> incomingCalls = new ArrayList<>();

    try (XSSFWorkbook excelFile = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
      XSSFSheet sheet = excelFile.getSheetAt(1);

      // create Characteristics catalog
      // first row contains Characteristics names from second to last columns
      //

      List<Characteristic> characteristics = new ArrayList<>();

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
    } catch (IOException ignored) {

    }

    return incomingCalls;
  }

  void fillStorage(List<IncomingCall> incomingCalls) {
    fillVocabulary(incomingCalls);
    fillCharacteristics(incomingCalls);
    fillIncomingCalls(incomingCalls);
  }

  private void fillIncomingCalls(List<IncomingCall> incomingCalls) {
    // save incoming calls to Storage
    //

    try {
      daoFactory.incomingCallDAO().addAll(incomingCalls);
      notifyObservers("Incoming calls filled. Wait for recognizer training...");
    } catch (EmptyRecordException | NotExistsException e) {
      notifyObservers(e.getMessage());
    }
  }

  private void fillCharacteristics(List<IncomingCall> incomingCalls) {
    Set<Characteristic> characteristics = getCharacteristicsCatalog(incomingCalls);

    // save characteristics to Storage
    //

    for (Characteristic characteristic : characteristics) {
      try {
        daoFactory.characteristicDAO().addCharacteristic(characteristic);
      } catch (EmptyRecordException | AlreadyExistsException e) {
        notifyObservers(e.getMessage());
      }
    }

    notifyObservers("Characteristics filled. Wait for Incoming calls filling...");
  }

  private Set<Characteristic> getCharacteristicsCatalog(List<IncomingCall> incomingCalls) {
    Map<Characteristic, Characteristic> characteristics = new HashMap<>();

    for (IncomingCall incomingCall : incomingCalls) {
      // for all incoming calls characteristic values
      //

      for (Map.Entry<Characteristic, CharacteristicValue> entry : incomingCall.getCharacteristics().entrySet()) {
        // add characteristic to catalog
        characteristics.put(entry.getKey(), entry.getKey());

        // add characteristic value to possible values
        characteristics.get(entry.getKey()).addPossibleValue(entry.getValue());
      }
    }

    return characteristics.keySet();
  }

  private void fillVocabulary(List<IncomingCall> incomingCalls) {
    // save vocabulary to Storage
    //

    try {
      daoFactory.vocabularyWordDAO().addAll(getVocabularyFromIncomingCallsTexts(incomingCalls));
      notifyObservers("Vocabulary filled. Wait for Characteristics filling...");
    } catch (EmptyRecordException | AlreadyExistsException e) {
      notifyObservers(e.getMessage());
    }
  }

  private List<VocabularyWord> getVocabularyFromIncomingCallsTexts(List<IncomingCall> incomingCalls) {
    Set<String> uniqueValues = new LinkedHashSet<>();
    List<VocabularyWord> vocabulary = new ArrayList<>();

    // add words (converted to n-gram) from all Incoming Calls Texts to vocabulary
    //

    for (IncomingCall incomingCall : incomingCalls) {
      uniqueValues.addAll(nGram.getNGram(incomingCall.getText()));
    }

    // convert uniqueValues to Vocabulary
    //

    for (String uniqueValue : uniqueValues) {
      vocabulary.add(new VocabularyWord(uniqueValue));
    }

    return vocabulary;
  }

  @Override
  public void addObserver(Observer o) {
    observers.add(o);
  }

  @Override
  public void removeObserver(Observer o) {
    observers.remove(o);
  }

  @Override
  public void notifyObservers(String text) {
    for (Observer o : observers) {
      o.update(text);
    }
  }
}
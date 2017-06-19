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

    Recognizer.shutdown();
  }

  void createStorage() {
    StorageCreator storageCreator = daoFactory.storageCreator();
    storageCreator.createStorage();
    storageCreator.clearStorage();
    notifyObservers("Storage created. Wait...");
  }

  List<IncomingCall> readXlsxFile(File xlsxFile) {
    List<IncomingCall> incomingCalls = new ArrayList<>();

    try (XSSFWorkbook excelFile = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
      XSSFSheet sheet = excelFile.getSheetAt(0);

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

        // exclude empty rows
        if (!sheet.getRow(i).getCell(0).getStringCellValue().equals("")) {
          incomingCalls.add(new IncomingCall(sheet.getRow(i).getCell(0).getStringCellValue(), characteristicsValues));
        }
      }

      return incomingCalls;
    } catch (IOException e) {
      notifyObservers(e.getMessage());
    }

    return null;
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
      notifyObservers("Incoming calls saved. Wait...");
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
        notifyObservers("Characteristics '" + characteristic.getName() + "' saved. Wait...");
      } catch (EmptyRecordException | AlreadyExistsException e) {
        notifyObservers(e.getMessage());
      }
    }
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
      notifyObservers("Vocabulary saved. Wait...");
    } catch (EmptyRecordException | AlreadyExistsException e) {
      notifyObservers(e.getMessage());
    }
  }

  // todo: move to VocabularyBuilder class
  private List<VocabularyWord> getVocabularyFromIncomingCallsTexts(List<IncomingCall> incomingCalls) {
    Map<String, Integer> uniqueValues = new HashMap<>();
    List<VocabularyWord> vocabulary = new ArrayList<>();

    // count frequency of use each word (converted to n-gram) from all Incoming Calls Texts
    //

    for (IncomingCall incomingCall : incomingCalls) {
      for (String word : nGram.getNGram(incomingCall.getText())) {
        if (uniqueValues.containsKey(word)) {
          // increase counter
          uniqueValues.put(word, uniqueValues.get(word) + 1);
        } else {
          // add new word
          uniqueValues.put(word, 1);
        }
      }
    }

    // convert uniqueValues to Vocabulary, excluding rare (frequency of use less then 4)
    //

    for (Map.Entry<String, Integer> entry : uniqueValues.entrySet()) {
      if (entry.getValue() > 3) {
        vocabulary.add(new VocabularyWord(entry.getKey()));
      }
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
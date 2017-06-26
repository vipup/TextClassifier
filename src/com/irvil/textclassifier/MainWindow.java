package com.irvil.textclassifier;

import com.irvil.textclassifier.dao.*;
import com.irvil.textclassifier.dao.factories.DAOFactory;
import com.irvil.textclassifier.model.Characteristic;
import com.irvil.textclassifier.model.CharacteristicValue;
import com.irvil.textclassifier.model.ClassifiableText;
import com.irvil.textclassifier.model.VocabularyWord;
import com.irvil.textclassifier.ngram.NGramStrategy;
import com.irvil.textclassifier.classifier.Classifier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainWindow extends Application {
  private final Config config = Config.getInstance();
  private final List<Classifier> classifiers = new ArrayList<>();
  private LogWindow logWindow;
  private DAOFactory daoFactory;
  private NGramStrategy nGramStrategy;
  private FlowPane root;
  private TextArea textAreaClassifiableText;
  private Button btnClassify;
  private Label lblCharacteristics;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    // check config file
    //

    if (!config.isLoaded()) {
      errorMsg("Config file is not found or it is empty.");
      return;
    }

    // create DAO factory and NGramStrategy using settings from config file
    //

    daoFactory = DAOFactory.getDaoFactory(config);
    nGramStrategy = NGramStrategy.getStrategy(config.getNGramStrategy());

    if (daoFactory == null || nGramStrategy == null) {
      errorMsg("Oops, it seems there is an error in config file.");
      return;
    }

    List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
    List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();

    // check if it is first start
    //

    if (!loadTrainedClassifiers(characteristics, vocabulary)) {
      infoMsg("You start program first time. Please, choose XLSX file with data for classifier training.");

      File file = openFileDialogBox();

      if (file != null) {
        logWindow = new LogWindow();
        logWindow.show();

        // handle file and update log window in separate thread
        // log window do not update if we run this code in main thread
        //

        Thread t = new Thread() {
          @Override
          public void run() {
            createStorage();

            // read first sheet from a file
            List<ClassifiableText> classifiableTexts = getClassifiableTexts(file, 1);

            // save data to storage
            //

            List<VocabularyWord> vocabulary = saveVocabularyToStorage(classifiableTexts);
            List<Characteristic> characteristics = saveCharacteristicsToStorage(classifiableTexts);
            List<ClassifiableText> classifiableTextForTrain = saveClassifiableTextsToStorage(classifiableTexts);

            // create and train classifiers
            //

            createClassifiers(characteristics, vocabulary);
            trainAndSaveClassifiers(classifiableTextForTrain);
            checkClassifiersAccuracy(file);

            logWindow.update("\nPlease restart the program.");
          }
        };

        t.setUncaughtExceptionHandler((th, ex) -> logWindow.update(ex.toString()));
        t.start();
      }

      return;
    }

    // everything is ok (Storage is filled, classifier is trained) -> start program
    buildForm(primaryStage);
  }

  private void checkClassifiersAccuracy(File file) {
    logWindow.update("\n");

    // read second sheet from a file
    List<ClassifiableText> classifiableTexts = getClassifiableTexts(file, 2);

    for (Classifier classifier : classifiers) {
      Characteristic characteristic = classifier.getCharacteristic();
      int correctlyClassified = 0;

      for (ClassifiableText classifiableText : classifiableTexts) {
        CharacteristicValue idealValue = classifiableText.getCharacteristicValue(characteristic);
        CharacteristicValue classifiedValue = classifier.classify(classifiableText);

        if (classifiedValue.getValue().equals(idealValue.getValue())) {
          correctlyClassified++;
        }
      }

      double accuracy = ((double) correctlyClassified / classifiableTexts.size()) * 100;
      logWindow.update(String.format("Accuracy of Classifier for '" + characteristic.getName() + "' characteristic: %.2f%%", accuracy));
    }
  }

  private File openFileDialogBox() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"));
    return fileChooser.showOpenDialog(null);
  }

  private void errorMsg(String text) {
    msg(text, Alert.AlertType.ERROR);
    Platform.exit();
  }

  private void infoMsg(String text) {
    msg(text, Alert.AlertType.INFORMATION);
  }

  private void msg(String text, Alert.AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setHeaderText("");
    alert.setContentText(text);
    alert.showAndWait();
  }

  private List<ClassifiableText> getClassifiableTexts(File file, int sheetNumber) {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();

    try {
      classifiableTexts = new ExcelFileReader().xlsxToClassifiableTexts(file, sheetNumber);
    } catch (IOException | EmptySheetException e) {
      logWindow.update(e.getMessage());
    }

    return classifiableTexts;
  }

  private void createClassifiers(List<Characteristic> characteristics, List<VocabularyWord> vocabulary) {
    for (Characteristic characteristic : characteristics) {
      Classifier classifier = new Classifier(characteristic, vocabulary, nGramStrategy);
      classifier.addObserver(logWindow);
      classifiers.add(classifier);
    }
  }

  private void createStorage() {
    StorageCreator storageCreator = daoFactory.storageCreator();
    storageCreator.createStorageFolder(config.getDbPath());
    storageCreator.createStorage();
    storageCreator.clearStorage();
    logWindow.update("Storage created. Wait...");
  }

  private boolean loadTrainedClassifiers(List<Characteristic> characteristics, List<VocabularyWord> vocabulary) {
    if (characteristics.size() == 0 || vocabulary.size() == 0) {
      return false;
    }

    // load trained classifiers for each Characteristics
    //

    try {
      for (Characteristic characteristic : characteristics) {
        File trainedClassifier = new File(config.getDbPath() + "/" + characteristic.getName() + "NeuralNetworkClassifier");
        classifiers.add(new Classifier(trainedClassifier, characteristic, vocabulary, nGramStrategy));
      }
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  private void trainAndSaveClassifiers(List<ClassifiableText> classifiableTextForTrain) {
    for (Classifier classifier : classifiers) {
      classifier.train(classifiableTextForTrain);
      classifier.saveTrainedClassifier(new File(config.getDbPath() + "/" + classifier.toString()));
    }

    Classifier.shutdown();
  }

  private List<ClassifiableText> saveClassifiableTextsToStorage(List<ClassifiableText> classifiableTexts) {
    ClassifiableTextDAO classifiableTextDAO = daoFactory.classifiableTextDAO();

    try {
      classifiableTextDAO.addAll(classifiableTexts);
      logWindow.update("Classifiable texts saved. Wait...");
    } catch (EmptyRecordException | NotExistsException e) {
      logWindow.update(e.getMessage());
    }

    // return classifiable texts from DB
    return classifiableTextDAO.getAll();
  }

  private List<Characteristic> saveCharacteristicsToStorage(List<ClassifiableText> classifiableTexts) {
    Set<Characteristic> characteristics = getCharacteristicsCatalog(classifiableTexts);

    CharacteristicDAO characteristicDAO = daoFactory.characteristicDAO();

    for (Characteristic characteristic : characteristics) {
      try {
        characteristicDAO.addCharacteristic(characteristic);
        logWindow.update("'" + characteristic.getName() + "' characteristic saved. Wait...");
      } catch (EmptyRecordException | AlreadyExistsException e) {
        logWindow.update(e.getMessage());
      }
    }

    // return Characteristics with IDs
    return characteristicDAO.getAllCharacteristics();
  }

  private Set<Characteristic> getCharacteristicsCatalog(List<ClassifiableText> classifiableTexts) {
    Map<Characteristic, Characteristic> characteristics = new HashMap<>();

    for (ClassifiableText classifiableText : classifiableTexts) {
      // for all classifiable texts characteristic values
      //

      for (Map.Entry<Characteristic, CharacteristicValue> entry : classifiableText.getCharacteristics().entrySet()) {
        // add characteristic to catalog
        characteristics.put(entry.getKey(), entry.getKey());

        // add characteristic value to possible values
        characteristics.get(entry.getKey()).addPossibleValue(entry.getValue());
      }
    }

    return characteristics.keySet();
  }

  private List<VocabularyWord> saveVocabularyToStorage(List<ClassifiableText> classifiableTexts) {
    VocabularyWordDAO vocabularyWordDAO = daoFactory.vocabularyWordDAO();

    try {
      vocabularyWordDAO.addAll(new VocabularyBuilder(nGramStrategy).getVocabulary(classifiableTexts));
      logWindow.update("Vocabulary saved. Wait...");
    } catch (EmptyRecordException | AlreadyExistsException e) {
      logWindow.update(e.getMessage());
    }

    // return vocabulary with IDs
    return vocabularyWordDAO.getAll();
  }

  private void buildForm(Stage primaryStage) {
    textAreaClassifiableText = new TextArea();
    textAreaClassifiableText.setWrapText(true);

    btnClassify = new Button("Classify");
    btnClassify.setOnAction(new ClassifyBtnPressEvent());

    lblCharacteristics = new Label("");

    root = new FlowPane(Orientation.VERTICAL, 10, 10);
    root.setAlignment(Pos.BASELINE_CENTER);
    root.getChildren().addAll(textAreaClassifiableText, btnClassify, lblCharacteristics);

    primaryStage.setScene(new Scene(root, 500, 300));
    primaryStage.show();
  }

  // Event handlers
  //

  private class ClassifyBtnPressEvent implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      ClassifiableText classifiableText = new ClassifiableText(textAreaClassifiableText.getText());
      StringBuilder classifiedCharacteristics = new StringBuilder();

      // start Classifier for each Characteristic from DB
      //

      try {
        for (Classifier classifier : classifiers) {
          CharacteristicValue classifiedValue = classifier.classify(classifiableText);
          classifiedCharacteristics.append(classifier.getCharacteristic().getName()).append(": ").append(classifiedValue.getValue()).append("\n");
        }
      } catch (Exception e) {
        // it is possible if DB was edited manually
        errorMsg("It seems that trained classifier does not match Characteristics and Vocabulary. " +
            "You need to retrain classifier.");
      }

      lblCharacteristics.setText(classifiedCharacteristics.toString());
    }
  }
}
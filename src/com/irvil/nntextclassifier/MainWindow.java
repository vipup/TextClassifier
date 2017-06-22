package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.*;
import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.dao.factories.JDBCDAOFactory;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import com.irvil.nntextclassifier.ngram.NGramStrategySimpleFactory;
import com.irvil.nntextclassifier.recognizer.Recognizer;
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
  private LogWindow logWindow;
  private Config config = Config.getInstance();
  private DAOFactory daoFactory;
  private NGramStrategy nGramStrategy;

  private FlowPane root;
  private TextArea textAreaIncomingCall;
  private Button btnRecognize;
  private Label lblCharacteristics;

  private List<Recognizer> recognizers = new ArrayList<>();

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

    // create Storage folder
    //

    if (!isDBFolderExists()) {
      if (!createDbFolder(config.getDbPath())) {
        errorMsg("Can't create folder.");
        return;
      }
    }

    // create DAO factory and NGramStrategy using settings from config file
    //

    daoFactory = getDaoFactory();
    nGramStrategy = NGramStrategySimpleFactory.getStrategy(config.getNGramStrategy());

    if (daoFactory == null || nGramStrategy == null) {
      errorMsg("Oops, it seems there is an error in config file.");
      return;
    }

    List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
    List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();

    // check if it is first start
    //

    if (!loadLearnedRecognizers(characteristics, vocabulary)) {
      infoMsg("You start program first time. Please, choose XLSX file with data for recognizer training.");

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

            // read incoming calls from file
            //

            List<IncomingCall> incomingCalls = null;

            try {
              incomingCalls = new FileToIncomingCallsConverter().readXlsxFile(file);
            } catch (IOException e) {
              logWindow.update(e.getMessage());
            }

            // save data to storage
            //

            List<VocabularyWord> vocabulary = saveVocabularyToStorage(incomingCalls);
            List<Characteristic> characteristics = saveCharacteristicsToStorage(incomingCalls);
            List<IncomingCall> incomingCallsForTrain = saveIncomingCallsToStorage(incomingCalls);

            // create and train recognizers
            //

            createRecognizers(characteristics, vocabulary);
            trainAndSaveRecognizers(incomingCallsForTrain);

            //

            logWindow.update("\nPlease restart the program.");
          }
        };

        t.setUncaughtExceptionHandler((th, ex) -> logWindow.update(ex.toString()));
        t.start();
      }

      return;
    }

    // everything is ok (Storage is filled, recognizer is trained) -> start program
    buildForm(primaryStage);
  }

  private void createRecognizers(List<Characteristic> characteristics, List<VocabularyWord> vocabulary) {
    for (Characteristic characteristic : characteristics) {
      Recognizer recognizer = new Recognizer(characteristic, vocabulary, nGramStrategy);
      recognizer.addObserver(logWindow);
      recognizers.add(recognizer);
    }
  }

  private void createStorage() {
    StorageCreator storageCreator = daoFactory.storageCreator();
    storageCreator.createStorage();
    storageCreator.clearStorage();
    logWindow.update("Storage created. Wait...");
  }

  private File openFileDialogBox() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"));
    return fileChooser.showOpenDialog(null);
  }

  private boolean isDBFolderExists() {
    return new File(config.getDbPath()).exists();
  }

  private DAOFactory getDaoFactory() {
    DAOFactory daoFactory = null;

    // create DAO factory depends on config values
    //

    try {
      if (config.getDaoType().equals("jdbc")) {
        // create connector depends on config value
        //

        JDBCConnector jdbcConnector = null;

        if (config.getDBMSType().equals("sqlite")) {
          jdbcConnector = new JDBCSQLiteConnector(config.getDbPath() + "/" + config.getSQLiteDbFileName());
        }

        // create factory
        daoFactory = new JDBCDAOFactory(jdbcConnector);
      }
    } catch (IllegalArgumentException e) {
      return null;
    }

    return daoFactory;
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

  private boolean loadLearnedRecognizers(List<Characteristic> characteristics, List<VocabularyWord> vocabulary) {
    if (characteristics.size() == 0 || vocabulary.size() == 0) {
      return false;
    }

    // load trained recognizers for each Characteristic from DB
    //

    try {
      for (Characteristic characteristic : characteristics) {
        File trainedRecognizer = new File(config.getDbPath() + "/" + characteristic.getName() + "RecognizerNeuralNetwork");
        recognizers.add(new Recognizer(trainedRecognizer, characteristic, vocabulary, nGramStrategy));
      }
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  private boolean createDbFolder(String path) {
    return new File(path).mkdir();
  }

  private void trainAndSaveRecognizers(List<IncomingCall> incomingCallsForTrain) {
    for (Recognizer recognizer : recognizers) {
      recognizer.train(incomingCallsForTrain);
      recognizer.saveTrainedRecognizer(new File(config.getDbPath() + "/" + recognizer.toString()));
    }

    Recognizer.shutdown();
  }

  private List<IncomingCall> saveIncomingCallsToStorage(List<IncomingCall> incomingCalls) {
    IncomingCallDAO incomingCallDAO = daoFactory.incomingCallDAO();

    try {
      incomingCallDAO.addAll(incomingCalls);
      logWindow.update("Incoming calls saved. Wait...");
    } catch (EmptyRecordException | NotExistsException e) {
      logWindow.update(e.getMessage());
    }

    // return incoming calls from DB
    return incomingCallDAO.getAll();
  }

  private List<Characteristic> saveCharacteristicsToStorage(List<IncomingCall> incomingCalls) {
    CharacteristicDAO characteristicDAO = daoFactory.characteristicDAO();
    Set<Characteristic> characteristics = getCharacteristicsCatalog(incomingCalls);

    for (Characteristic characteristic : characteristics) {
      try {
        characteristicDAO.addCharacteristic(characteristic);
        logWindow.update("Characteristics '" + characteristic.getName() + "' saved. Wait...");
      } catch (EmptyRecordException | AlreadyExistsException e) {
        logWindow.update(e.getMessage());
      }
    }

    // return Characteristics with IDs
    return characteristicDAO.getAllCharacteristics();
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

  private List<VocabularyWord> saveVocabularyToStorage(List<IncomingCall> incomingCalls) {
    VocabularyWordDAO vocabularyWordDAO = daoFactory.vocabularyWordDAO();

    try {
      vocabularyWordDAO.addAll(new VocabularyBuilder(nGramStrategy).getVocabulary(incomingCalls));
      logWindow.update("Vocabulary saved. Wait...");
    } catch (EmptyRecordException | AlreadyExistsException e) {
      logWindow.update(e.getMessage());
    }

    // return vocabulary with IDs
    return vocabularyWordDAO.getAll();
  }

  private void buildForm(Stage primaryStage) {
    textAreaIncomingCall = new TextArea();
    textAreaIncomingCall.setWrapText(true);

    btnRecognize = new Button("Recognize");
    btnRecognize.setOnAction(new RecognizeBtnPressEvent());

    lblCharacteristics = new Label("");

    root = new FlowPane(Orientation.VERTICAL, 10, 10);
    root.setAlignment(Pos.BASELINE_CENTER);
    root.getChildren().addAll(textAreaIncomingCall, btnRecognize, lblCharacteristics);

    primaryStage.setScene(new Scene(root, 500, 300));
    primaryStage.show();
  }

// Event handlers
//

  private class RecognizeBtnPressEvent implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      IncomingCall incomingCall = new IncomingCall(textAreaIncomingCall.getText());
      StringBuilder recognizedCharacteristics = new StringBuilder();

      // start Recognizer for each Characteristic from DB
      //

      try {
        for (Recognizer recognizer : recognizers) {
          CharacteristicValue recognizedValue = recognizer.recognize(incomingCall);
          recognizedCharacteristics.append(recognizer.getCharacteristicName()).append(": ").append(recognizedValue.getValue()).append("\n");
        }
      } catch (Exception e) {
        // it is possible if DB was edited manually
        errorMsg("It seems that trained recognizer does not match Characteristics and Vocabulary. " +
            "You need to retrain recognizer.");
      }

      lblCharacteristics.setText(recognizedCharacteristics.toString());
    }
  }
}
package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.dao.factories.JDBCDAOFactory;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import com.irvil.nntextclassifier.recognizer.Recognizer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends Application {
  private Config config = Config.getInstance();

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
      if (!FirstStart.createDbFolder(config.getDbPath())) {
        errorMsg("Can't create folder.");
        return;
      }
    }

    // create DAO factory using settings from config file
    //

    DAOFactory daoFactory = getDaoFactory();

    if (daoFactory == null) {
      errorMsg("Oops, it seems there is an error in config file.");
      return;
    }

    // check if it is first start
    //

    if (!isDBFilled(daoFactory) || !loadLearnedRecognizers(daoFactory)) {
      infoMsg("You start program first time. Please, choose XLSX file with data for recognizer learning.");

      File file = openFileDialogBox();

      if (file != null) {
        LogWindow logWindow = new LogWindow();
        logWindow.show();

        // handle file and update log window in separate thread
        //

        Task<Void> task = new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            FirstStart firstStart = new FirstStart(daoFactory, new FilteredUnigram());

            firstStart.createStorage();
            logWindow.addText("Storage created. Wait for storage filling...");

            firstStart.fillStorage(firstStart.readXlsxFile(file));
            logWindow.addText("Storage filled. Wait for recognizer training...");

            firstStart.trainAndSaveRecognizers(config.getDbPath());
            logWindow.addText("Recognizer trained. All tasks finished.");
            logWindow.addText("\nPlease restart the program.");

            return null;
          }
        };
        new Thread(task).start();
      }

      return;
    }
    // todo: change learn to train
    // everything is ok (Storage is filled, recognizer is trained) -> start program
    buildForm(primaryStage);
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

  private boolean isDBFilled(DAOFactory daoFactory) {
    List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
    List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();

    return (characteristics.size() != 0 && vocabulary.size() != 0);
  }

  private boolean loadLearnedRecognizers(DAOFactory daoFactory) {
    try {
      List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
      List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();

      // load trained recognizers for each Characteristic from DB
      //

      for (Characteristic characteristic : characteristics) {
        File trainedRecognizer = new File(config.getDbPath() + "/" + characteristic.getName() + "RecognizerNeuralNetwork");
        recognizers.add(new Recognizer(trainedRecognizer, characteristic, vocabulary, new FilteredUnigram()));
      }
    } catch (Exception e) {
      return false;
    }

    return true;
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
        errorMsg("It seems that learned recognizer does not match Characteristics and Vocabulary. " +
            "You need to relearn recognizer.");
      }

      lblCharacteristics.setText(recognizedCharacteristics.toString());
    }
  }
}
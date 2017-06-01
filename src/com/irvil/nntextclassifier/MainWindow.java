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
    if (!config.isLoaded()) {
      alertMsg("Config file is not found or it is empty");
      return;
    }

    DAOFactory daoFactory = getDaoFactory();

    if (daoFactory == null) {
      alertMsg("Oops, it seems there is an error in config file");
      return;
    }

    // todo: start FirstStart automatically
    if (!isDBFilled(daoFactory)) {
      alertMsg("Database is empty. Please start FirstStart class.");
      return;
    }

    if (!loadLearnedRecognizers(daoFactory, config.getDbPath())) {
      alertMsg("Learned recognizers is not found. Please start FirstStart class.");
      return;
    }

    buildForm(primaryStage);
  }

  public DAOFactory getDaoFactory() {
    DAOFactory daoFactory = null;

    // create DAO factory depends on config value
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

  private void alertMsg(String text) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setContentText(text);
    alert.showAndWait();
  }

  private boolean isDBFilled(DAOFactory daoFactory) {
    List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
    List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();

    return (characteristics.size() != 0 && vocabulary.size() != 0);
  }

  private boolean loadLearnedRecognizers(DAOFactory daoFactory, String path) {
    try {
      List<Characteristic> characteristics = daoFactory.characteristicDAO().getAllCharacteristics();
      List<VocabularyWord> vocabulary = daoFactory.vocabularyWordDAO().getAll();

      // load trained recognizers for each Characteristic from DB
      //

      for (Characteristic characteristic : characteristics) {
        File trainedRecognizer = new File(path + "/" + characteristic.getName() + "RecognizerNeuralNetwork");
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
        alertMsg("It seems that learned recognizer does not match Characteristics and Vocabulary. " +
            "You need to relearn recognizer.");
      }

      lblCharacteristics.setText(recognizedCharacteristics.toString());
    }
  }
}
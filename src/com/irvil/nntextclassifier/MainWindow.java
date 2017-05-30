package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.dao.factories.JDBCDAOFactory;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
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
import java.util.List;

public class MainWindow extends Application {
  private boolean error;

  private FlowPane root;
  private TextArea textAreaIncomingCall;
  private Button btnRecognize;
  private Label lblModule;
  private Label lblHandler;

  private Recognizer moduleRecognizer;
  private Recognizer handlerRecognizer;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {
    super.init();

    Config config = Config.getInstance();
    DAOFactory daoFactory = null;

    // create DAO factory depends on config value
    //

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

    //

    error = (!config.isLoaded() ||
        !isDBFolderExists(config.getDbPath()) ||
        !isDBFilled() ||
        !loadLearnedRecognizers(daoFactory, config.getDbPath()));
  }

  @Override
  public void start(Stage primaryStage) {
    if (error) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText("Database or learned recognizers not found. Please start FirstStart module.");
      alert.showAndWait();
    } else {
      buildForm(primaryStage);
    }
  }

  private void buildForm(Stage primaryStage) {
    textAreaIncomingCall = new TextArea();
    textAreaIncomingCall.setWrapText(true);

    btnRecognize = new Button("Recognize");
    btnRecognize.setOnAction(new RecognizeBtnPressEvent());

    lblModule = new Label("");
    lblHandler = new Label("");

    root = new FlowPane(Orientation.VERTICAL, 10, 10);
    root.setAlignment(Pos.BASELINE_CENTER);
    root.getChildren().addAll(textAreaIncomingCall, btnRecognize, lblModule, lblHandler);

    primaryStage.setScene(new Scene(root, 500, 300));
    primaryStage.show();
  }

  private boolean isDBFolderExists(String path) {
    return new File(path).exists();
  }

  private boolean isDBFilled() {
    return false;
  }

  private boolean loadLearnedRecognizers(DAOFactory daoFactory, String path) {
    try {
      List<VocabularyWord> vacabulary = daoFactory.vocabularyWordDAO().getAll();

      moduleRecognizer = new Recognizer(new File(path + "/ModuleRecognizerTrainedNetwork"), "Module", daoFactory.moduleDAO().getAll(), vacabulary, new FilteredUnigram());
      handlerRecognizer = new Recognizer(new File(path + "/HandlerRecognizerTrainedNetwork"), "Handler", daoFactory.handlerDAO().getAll(), vacabulary, new FilteredUnigram());
    } catch (RuntimeException e) {
      return false;
    }

    return true;
  }

  // Event handlers
  //

  private class RecognizeBtnPressEvent implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      IncomingCall ic = new IncomingCall(textAreaIncomingCall.getText());

      // recognize characteristics
      lblModule.setText(moduleRecognizer.recognize(ic).getValue());
      lblHandler.setText(handlerRecognizer.recognize(ic).getValue());
    }
  }
}
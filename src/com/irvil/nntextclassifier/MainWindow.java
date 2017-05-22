package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.recognizer.CategoryRecognizer;
import com.irvil.nntextclassifier.recognizer.HandlerRecognizer;
import com.irvil.nntextclassifier.recognizer.ModuleRecognizer;
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

public class MainWindow extends Application {
  private Config config = Config.getInstance();
  private boolean error;

  private FlowPane root;
  private TextArea textAreaIncomingCall;
  private Button btnRecognize;
  private Label lblModule;
  private Label lblCategory;
  private Label lblHandler;

  private Recognizer moduleRecognizer;
  private Recognizer categoryRecognizer;
  private Recognizer handlerRecognizer;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {
    super.init();
    error = (!config.isLoaded() || !isDBFolderExists() || !isDBFilled() || !loadLearnedRecognizers());
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
    lblCategory = new Label("");
    lblHandler = new Label("");

    root = new FlowPane(Orientation.VERTICAL, 10, 10);
    root.setAlignment(Pos.BASELINE_CENTER);
    root.getChildren().addAll(textAreaIncomingCall, btnRecognize, lblModule, lblCategory, lblHandler);

    primaryStage.setScene(new Scene(root, 500, 300));
    primaryStage.show();
  }

  private boolean isDBFolderExists() {
    return new File(config.getDbPath()).exists();
  }

  private boolean isDBFilled() {
    return (DAOFactory.vocabularyWordDAO(config.getDaoType(), config.getDBMSType()).getCount() != 0 &&
        DAOFactory.moduleDAO(config.getDaoType(), config.getDBMSType()).getCount() != 0 &&
        DAOFactory.categoryDAO(config.getDaoType(), config.getDBMSType()).getCount() != 0 &&
        DAOFactory.handlerDAO(config.getDaoType(), config.getDBMSType()).getCount() != 0);
  }

  private boolean loadLearnedRecognizers() {
    try {
      moduleRecognizer = new ModuleRecognizer(new File(config.getDbPath() + "/ModuleRecognizerTrainedNetwork"));
      categoryRecognizer = new CategoryRecognizer(new File(config.getDbPath() + "/CategoryRecognizerTrainedNetwork"));
      handlerRecognizer = new HandlerRecognizer(new File(config.getDbPath() + "/HandlerRecognizerTrainedNetwork"));
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
      lblCategory.setText(categoryRecognizer.recognize(ic).getValue());
      lblHandler.setText(handlerRecognizer.recognize(ic).getValue());
    }
  }
}
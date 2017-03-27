package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.jdbc.JDBCCategoryDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCHandlerDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCModuleDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCVocabularyWordDAO;
import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;
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
  private boolean error;

  private FlowPane root;
  private TextArea textArea;
  private Button btn;
  private Label moduleLbl;
  private Label categoryLbl;
  private Label handlerLbl;

  private Recognizer<Module> moduleRecognizer;
  private Recognizer<Category> categoryRecognizer;
  private Recognizer<Handler> handlerRecognizer;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {
    super.init();
    error = (!isDBFolderExists() || !isDBFilled() || !loadLearnedRecognizers());
  }

  private boolean isDBFolderExists() {
    return new File("./db").exists();
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
    textArea = new TextArea();
    textArea.setWrapText(true);

    btn = new Button("Recognize");
    btn.setOnAction(new RecognizeBtnPressEvent());

    moduleLbl = new Label("");
    categoryLbl = new Label("");
    handlerLbl = new Label("");

    root = new FlowPane(Orientation.VERTICAL, 10, 10);
    root.setAlignment(Pos.BASELINE_CENTER);
    root.getChildren().addAll(textArea, btn, moduleLbl, categoryLbl, handlerLbl);

    primaryStage.setScene(new Scene(root, 500, 300));
    primaryStage.show();
  }


  private boolean isDBFilled() {
    return !(new JDBCVocabularyWordDAO().getCount() == 0 ||
        new JDBCModuleDAO().getCount() == 0 ||
        new JDBCCategoryDAO().getCount() == 0 ||
        new JDBCHandlerDAO().getCount() == 0);
  }

  private boolean loadLearnedRecognizers() {
    try {
      moduleRecognizer = new ModuleRecognizer(new File("./db/ModuleRecognizerTrainedNetwork"));
      categoryRecognizer = new CategoryRecognizer(new File("./db/CategoryRecognizerTrainedNetwork"));
      handlerRecognizer = new HandlerRecognizer(new File("./db/HandlerRecognizerTrainedNetwork"));
    } catch (RuntimeException e) {
      return false;
    }

    return true;
  }

  private class RecognizeBtnPressEvent implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      IncomingCall ic = new IncomingCall(textArea.getText());

      moduleLbl.setText(moduleRecognizer.recognize(ic).toString());
      categoryLbl.setText(categoryRecognizer.recognize(ic).toString());
      handlerLbl.setText(handlerRecognizer.recognize(ic).toString());
    }
  }
}
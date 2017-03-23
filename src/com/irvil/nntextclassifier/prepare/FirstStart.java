package com.irvil.nntextclassifier.prepare;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.model.*;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import com.irvil.nntextclassifier.ngram.Unigram;
import com.irvil.nntextclassifier.recognizer.ModuleRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FirstStart {
  private NGramStrategy nGram;

  public FirstStart(NGramStrategy nGram) {
    this.nGram = nGram;
  }

  public void createStorage() {
    StorageCreator sc = new JDBCDBCreator();
    sc.createStorage();

    System.out.println("Storage created");
  }

  public void fillVocabulary() {
    List<IncomingCall> incomingCalls = new JDBCIncomingCallDAO().getAll();
    Set<String> vocabulary = getVocabulary(incomingCalls);

    for (String word : vocabulary) {
      new JDBCVocabularyWordDAO().add(new VocabularyWord(0, word));
    }

    System.out.println("Vocabulary filled");
  }

  private Set<String> getVocabulary(List<IncomingCall> incomingCalls) {
    Set<String> vocabulary = new LinkedHashSet<>();

    for (IncomingCall ic : incomingCalls) {
      vocabulary.addAll(nGram.getNGram(ic.getText()));
    }

    return vocabulary;
  }

  public void fillReferenceData() {
    IncomingCallDAO icDAO = new JDBCIncomingCallDAO();

    List<Module> modules = icDAO.getAllModules();
    List<Category> categories = icDAO.getAllCategories();
    List<Handler> handlers = icDAO.getAllHandlers();

    modules.forEach((module) -> new JDBCModuleDAO().add(module));
    categories.forEach((category) -> new JDBCCategoryDAO().add(category));
    handlers.forEach((handler) -> new JDBCHandlerDAO().add(handler));

    System.out.println("Modules, Categories, Handlers filled");
  }

  public void trainNeuralNetwork() {
    ModuleRecognizer moduleRecognizer = new ModuleRecognizer();

    moduleRecognizer.train();
    moduleRecognizer.saveTrainedNetwork(new File("./db/TrainedNetwork"));
  }

  public static void main(String[] args) throws IOException {
    FirstStart fs = new FirstStart(new Unigram());
//    fs.createStorage();
//
//    System.out.println("Fill IncomingCalls and press Enter");
//    System.in.read();
//
//    fs.fillVocabulary();
//    fs.fillReferenceData();
    fs.trainNeuralNetwork();

    //ModuleRecognizer m = new ModuleRecognizer(new File("./db/TrainedNetwork"));
  }
}
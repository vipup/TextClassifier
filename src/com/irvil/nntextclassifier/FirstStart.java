package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import com.irvil.nntextclassifier.ngram.Unigram;
import com.irvil.nntextclassifier.recognizer.CategoryRecognizer;
import com.irvil.nntextclassifier.recognizer.HandlerRecognizer;
import com.irvil.nntextclassifier.recognizer.ModuleRecognizer;
import com.irvil.nntextclassifier.recognizer.Recognizer;
import org.encog.Encog;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FirstStart {
  private boolean createStorage() {
    StorageCreator sc = DAOFactory.storageCreator("jdbc", "SQLite");
    sc.createStorage();

    return true;
  }

  private boolean fillVocabulary(NGramStrategy nGram) {
    // build vocabulary from all IncomingCalls
    Set<String> vocabulary = getVocabulary(nGram, DAOFactory.incomingCallDAO("jdbc", "SQLite").getAll());

    // save vocabulary words in Storage
    for (String word : vocabulary) {
      DAOFactory.vocabularyWordDAO("jdbc", "SQLite").add(new VocabularyWord(0, word));
    }

    return true;
  }

  private Set<String> getVocabulary(NGramStrategy nGram, List<IncomingCall> incomingCalls) {
    Set<String> vocabulary = new LinkedHashSet<>();

    // add words (converted to n-gram) from all IncomingCalls to vocabulary
    for (IncomingCall ic : incomingCalls) {
      vocabulary.addAll(nGram.getNGram(ic.getText()));
    }

    return vocabulary;
  }

  private boolean fillReferenceData() {
    IncomingCallDAO icDAO = DAOFactory.incomingCallDAO("jdbc", "SQLite");

    // save characteristics in Storage
    icDAO.getUniqueModules().forEach((module) -> DAOFactory.moduleDAO("jdbc", "SQLite").add(module));
    icDAO.getUniqueCategories().forEach((category) -> DAOFactory.categoryDAO("jdbc", "SQLite").add(category));
    icDAO.getUniqueHandlers().forEach((handler) -> DAOFactory.handlerDAO("jdbc", "SQLite").add(handler));

    return true;
  }

  private void trainRecognizer(Recognizer recognizer) {
    recognizer.train();
    recognizer.saveTrainedRecognizer(new File("./db/" + recognizer.toString() + "TrainedNetwork"));
  }

  private boolean createDbFolder(String path) {
    return new File(path).mkdir();
  }

  public static void main(String[] args) throws IOException {
    FirstStart fs = new FirstStart();

    // create Storage
    //

    if (fs.createDbFolder("./db")) {
      System.out.println("Folder created");
    }

    if (fs.createStorage()) {
      System.out.println("Storage created");
    }

    // fill data
    //

    System.out.println("Fill IncomingCalls and press Enter");
    System.in.read();

    if (fs.fillVocabulary(new Unigram())) {
      System.out.println("Vocabulary filled");
    }

    if (fs.fillReferenceData()) {
      System.out.println("Modules, Categories, Handlers filled");
    }

    // train recognizers
    //

    fs.trainRecognizer(new ModuleRecognizer());
    fs.trainRecognizer(new CategoryRecognizer());
    fs.trainRecognizer(new HandlerRecognizer());
    Encog.getInstance().shutdown();
  }
}
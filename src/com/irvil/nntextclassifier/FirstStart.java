package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.dao.factories.JDBCDAOFactory;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
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
  private DAOFactory daoFactory;

  public FirstStart(DAOFactory daoFactory) {
    if (daoFactory == null) {
      throw new IllegalArgumentException();
    }

    this.daoFactory = daoFactory;
  }

  public boolean createStorage() {
    StorageCreator sc = daoFactory.storageCreator();
    sc.createStorage();

    return true;
  }

  public boolean fillVocabulary(NGramStrategy nGram) {
    // build vocabulary from all IncomingCalls
    Set<String> vocabulary = getVocabulary(nGram, daoFactory.incomingCallDAO().getAll());

    // save vocabulary words in Storage
    for (String word : vocabulary) {
      daoFactory.vocabularyWordDAO().add(new VocabularyWord(0, word));
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

  public boolean fillReferenceData() {
    IncomingCallDAO icDAO = daoFactory.incomingCallDAO();

    // save characteristics in Storage
    icDAO.getUniqueModules().forEach((module) -> daoFactory.moduleDAO().add(module));
    icDAO.getUniqueHandlers().forEach((handler) -> daoFactory.handlerDAO().add(handler));

    return true;
  }

  public void trainRecognizer(String path, Recognizer recognizer) {
    recognizer.train();
    recognizer.saveTrainedRecognizer(new File(path + "/" + recognizer.toString() + "TrainedNetwork"));
  }

  public boolean createDbFolder(String path) {
    return new File(path).mkdir();
  }

  public static void main(String[] args) throws IOException {
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

    FirstStart fs = new FirstStart(daoFactory);

    // create Storage
    //

    if (fs.createDbFolder(config.getDbPath())) {
      System.out.println("Folder created");
    }

    if (fs.createStorage()) {
      System.out.println("Storage created");
    }

    // fill data
    //

    System.out.println("Fill IncomingCalls and press Enter");
    System.in.read();

    if (fs.fillVocabulary(new FilteredUnigram())) {
      System.out.println("Vocabulary filled");
    }

    if (fs.fillReferenceData()) {
      System.out.println("Modules, Categories, Handlers filled");
    }

    // train recognizers
    //

    fs.trainRecognizer(config.getDbPath(), new ModuleRecognizer(daoFactory));
    fs.trainRecognizer(config.getDbPath(), new HandlerRecognizer(daoFactory));
    Encog.getInstance().shutdown();
  }
}
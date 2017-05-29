package com.irvil.nntextclassifier.dao.factories;

import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.VocabularyWord;

public class JDBCDAOFactory implements DAOFactory {
  private JDBCConnector connector;

  public JDBCDAOFactory(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public IncomingCallDAO incomingCallDAO() {
    return new JDBCIncomingCallDAO(connector);
  }

  @Override
  public CharacteristicDAO<Characteristic> moduleDAO() {
    return new JDBCModuleDAO(connector);
  }

  @Override
  public CharacteristicDAO<Characteristic> handlerDAO() {
    return new JDBCHandlerDAO(connector);
  }

  @Override
  public CharacteristicDAO<VocabularyWord> vocabularyWordDAO() {
    return new JDBCVocabularyWordDAO(connector);
  }

  @Override
  public StorageCreator storageCreator() {
    return new JDBCDBCreator(connector);
  }
}
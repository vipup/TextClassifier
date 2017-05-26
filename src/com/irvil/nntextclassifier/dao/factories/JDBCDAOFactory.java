package com.irvil.nntextclassifier.dao.factories;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Catalog;
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
  public GenericDAO<Catalog> moduleDAO() {
    return new JDBCModuleDAO(connector);
  }

  @Override
  public GenericDAO<Catalog> handlerDAO() {
    return new JDBCHandlerDAO(connector);
  }

  @Override
  public GenericDAO<VocabularyWord> vocabularyWordDAO() {
    return new JDBCVocabularyWordDAO(connector);
  }

  @Override
  public StorageCreator storageCreator() {
    return new JDBCDBCreator(connector);
  }
}
package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnectorFactory;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.VocabularyWord;

public class DAOFactory {
  public static IncomingCallDAO incomingCallDAO(String daoType, String dbmsType) {
    switch (daoType) {
      case "jdbc":
        return new JDBCIncomingCallDAO(JDBCConnectorFactory.getJDBCConnector(dbmsType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericDAO<Catalog> moduleDAO(String daoType, String dbmsType) {
    switch (daoType) {
      case "jdbc":
        return new JDBCModuleDAO(JDBCConnectorFactory.getJDBCConnector(dbmsType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericDAO<Catalog> handlerDAO(String daoType, String dbmsType) {
    switch (daoType) {
      case "jdbc":
        return new JDBCHandlerDAO(JDBCConnectorFactory.getJDBCConnector(dbmsType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static GenericDAO<VocabularyWord> vocabularyWordDAO(String daoType, String dbmsType) {
    switch (daoType) {
      case "jdbc":
        return new JDBCVocabularyWordDAO(JDBCConnectorFactory.getJDBCConnector(dbmsType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static StorageCreator storageCreator(String daoType, String dbmsType) {
    switch (daoType) {
      case "jdbc":
        return new JDBCDBCreator(JDBCConnectorFactory.getJDBCConnector(dbmsType));
      default:
        throw new IllegalArgumentException();
    }
  }
}
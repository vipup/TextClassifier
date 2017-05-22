package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnectorFactory;
import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.Module;

public class DAOFactory {
  public static IncomingCallDAO incomingCallDAO(String DAOType, String DBType) {
    switch (DAOType) {
      case "jdbc":
        return new JDBCIncomingCallDAO(JDBCConnectorFactory.getJDBCConnector(DBType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static CatalogDAO<Category> categoryDAO(String DAOType, String DBType) {
    switch (DAOType) {
      case "jdbc":
        return new JDBCCategoryDAO(JDBCConnectorFactory.getJDBCConnector(DBType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static CatalogDAO<Module> moduleDAO(String DAOType, String DBType) {
    switch (DAOType) {
      case "jdbc":
        return new JDBCModuleDAO(JDBCConnectorFactory.getJDBCConnector(DBType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static CatalogDAO<Handler> handlerDAO(String DAOType, String DBType) {
    switch (DAOType) {
      case "jdbc":
        return new JDBCHandlerDAO(JDBCConnectorFactory.getJDBCConnector(DBType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static VocabularyWordDAO vocabularyWordDAO(String DAOType, String DBType) {
    switch (DAOType) {
      case "jdbc":
        return new JDBCVocabularyWordDAO(JDBCConnectorFactory.getJDBCConnector(DBType));
      default:
        throw new IllegalArgumentException();
    }
  }

  public static StorageCreator storageCreator(String DAOType, String DBType) {
    switch (DAOType) {
      case "jdbc":
        return new JDBCDBCreator(JDBCConnectorFactory.getJDBCConnector(DBType));
      default:
        throw new IllegalArgumentException();
    }
  }
}
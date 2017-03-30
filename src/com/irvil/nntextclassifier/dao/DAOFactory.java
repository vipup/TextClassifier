package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.Module;

public class DAOFactory {
  public static IncomingCallDAO incomingCallDAO(String type) {
    switch (type) {
      case "jdbc":
        return new JDBCIncomingCallDAO();
      default:
        return null;
    }
  }

  public static CatalogDAO<Category> categoryDAO(String type) {
    switch (type) {
      case "jdbc":
        return new JDBCCategoryDAO();
      default:
        return null;
    }
  }

  public static CatalogDAO<Module> moduleDAO(String type) {
    switch (type) {
      case "jdbc":
        return new JDBCModuleDAO();
      default:
        return null;
    }
  }

  public static CatalogDAO<Handler> handlerDAO(String type) {
    switch (type) {
      case "jdbc":
        return new JDBCHandlerDAO();
      default:
        return null;
    }
  }

  public static VocabularyWordDAO vocabularyWordDAO(String type) {
    switch (type) {
      case "jdbc":
        return new JDBCVocabularyWordDAO();
      default:
        return null;
    }
  }

  public static StorageCreator storageCreator(String type) {
    switch (type) {
      case "jdbc":
        return new JDBCDBCreator();
      default:
        return null;
    }
  }
}
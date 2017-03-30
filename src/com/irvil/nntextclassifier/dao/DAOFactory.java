package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.dao.jdbc.*;
import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.Module;

public class DAOFactory {
  public static IncomingCallDAO incomingCallDAO(String type) {
    // switch (type) ...
    return new JDBCIncomingCallDAO();
  }

  public static CatalogDAO<Category> categoryDAO(String type) {
    // switch (type) ...
    return new JDBCCategoryDAO();
  }

  public static CatalogDAO<Module> moduleDAO(String type) {
    // switch (type) ...
    return new JDBCModuleDAO();
  }

  public static CatalogDAO<Handler> handlerDAO(String type) {
    // switch (type) ...
    return new JDBCHandlerDAO();
  }

  public static VocabularyWordDAO vocabularyWordDAO(String type) {
    // switch (type) ...
    return new JDBCVocabularyWordDAO();
  }

  public static StorageCreator storageCreator(String type) {
    // switch (type) ...
    return new JDBCDBCreator();
  }
}
package com.irvil.nntextclassifier.dao.factories;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.VocabularyWord;

public interface DAOFactory {
  IncomingCallDAO incomingCallDAO();

  GenericDAO<Catalog> moduleDAO();

  GenericDAO<Catalog> handlerDAO();

  GenericDAO<VocabularyWord> vocabularyWordDAO();

  StorageCreator storageCreator();
}
package com.irvil.nntextclassifier.dao.factories;

import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;

public interface DAOFactory {
  IncomingCallDAO incomingCallDAO();

  CharacteristicDAO characteristicDAO();

  VocabularyWordDAO vocabularyWordDAO();

  StorageCreator storageCreator();
}
package com.irvil.nntextclassifier.dao.factories;

import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.VocabularyWord;

public interface DAOFactory {
  IncomingCallDAO incomingCallDAO();

  CharacteristicDAO<Characteristic> moduleDAO();

  CharacteristicDAO<Characteristic> handlerDAO();

  CharacteristicDAO<VocabularyWord> vocabularyWordDAO();

  StorageCreator storageCreator();
}
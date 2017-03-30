package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.DAOFactory;

public class VocabularyWord extends Catalog {
  public VocabularyWord(int id, String value) {
    super(id, value, DAOFactory.vocabularyWordDAO("jdbc"));
  }
}
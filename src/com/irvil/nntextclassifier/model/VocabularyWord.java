package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.jdbc.JDBCVocabularyWordDAO;

public class VocabularyWord extends Catalog {
  public VocabularyWord(int id, String value) {
    super(id, value, new JDBCVocabularyWordDAO());
  }
}
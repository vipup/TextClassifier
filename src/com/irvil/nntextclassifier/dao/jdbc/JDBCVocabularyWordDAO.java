package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.VocabularyWord;

public class JDBCVocabularyWordDAO extends JDBCGenericDAO<VocabularyWord> {
  public JDBCVocabularyWordDAO(JDBCConnector connector) {
    super(connector);
  }

  @Override
  protected String getTableName() {
    return "Vocabulary";
  }

  @Override
  protected VocabularyWord createObject(int id, String value) {
    return new VocabularyWord(id, value);
  }
}
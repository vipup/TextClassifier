package com.irvil.textclassifier.dao.jdbc;

import com.irvil.textclassifier.dao.VocabularyWordDAOTest;
import com.irvil.textclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.textclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;

public class JDBCVocabularyWordDAOTest extends VocabularyWordDAOTest {
  @Override
  public void initializeDAO() {
    JDBCConnector jdbcConnector = new JDBCSQLiteConnector("./test_db/test.db");
    storageCreator = new JDBCDBCreator(jdbcConnector);
    characteristicDAO = new JDBCCharacteristicDAO(jdbcConnector);
    classifiableTextDAO = new JDBCClassifiableTextDAO(jdbcConnector);
    vocabularyWordDAO = new JDBCVocabularyWordDAO(jdbcConnector);
  }
}
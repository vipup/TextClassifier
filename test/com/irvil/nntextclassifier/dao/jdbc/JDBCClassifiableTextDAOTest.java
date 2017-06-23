package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.ClassifiableTextDAOTest;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;

public class JDBCClassifiableTextDAOTest extends ClassifiableTextDAOTest {
  @Override
  public void initializeDAO() {
    JDBCConnector jdbcConnector = new JDBCSQLiteConnector("./test_db/test.db");
    storageCreator = new JDBCDBCreator(jdbcConnector);
    characteristicDAO = new JDBCCharacteristicDAO(jdbcConnector);
    classifiableTextDAO = new JDBCClassifiableTextDAO(jdbcConnector);
    vocabularyWordDAO = new JDBCVocabularyWordDAO(jdbcConnector);
  }
}
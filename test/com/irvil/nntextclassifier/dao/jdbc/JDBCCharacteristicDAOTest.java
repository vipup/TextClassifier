package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.CharacteristicDAOTest;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;

public class JDBCCharacteristicDAOTest extends CharacteristicDAOTest {
  @Override
  public void initializeDAO() {
    JDBCConnector jdbcConnector = new JDBCSQLiteConnector(Config.getInstance().getDbPath() + "/test.db");
    storageCreator = new JDBCDBCreator(jdbcConnector);
    characteristicDAO = new JDBCCharacteristicDAO(jdbcConnector);
    incomingCallDAO = new JDBCIncomingCallDAO(jdbcConnector);
    vocabularyWordDAO = new JDBCVocabularyWordDAO(jdbcConnector);
  }
}
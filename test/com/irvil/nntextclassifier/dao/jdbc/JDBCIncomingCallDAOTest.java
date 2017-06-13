package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.IncomingCallDAOTest;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;

public class JDBCIncomingCallDAOTest extends IncomingCallDAOTest {
  @Override
  public void initializeDAO() {
    JDBCConnector jdbcConnector = new JDBCSQLiteConnector(".db/test.db");
    storageCreator = new JDBCDBCreator(jdbcConnector);
    characteristicDAO = new JDBCCharacteristicDAO(jdbcConnector);
    incomingCallDAO = new JDBCIncomingCallDAO(jdbcConnector);
    vocabularyWordDAO = new JDBCVocabularyWordDAO(jdbcConnector);
  }
}
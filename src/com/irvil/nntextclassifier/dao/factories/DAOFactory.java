package com.irvil.nntextclassifier.dao.factories;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.ClassifiableTextDAO;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;

public interface DAOFactory {
  ClassifiableTextDAO classifiableTextDAO();

  CharacteristicDAO characteristicDAO();

  VocabularyWordDAO vocabularyWordDAO();

  StorageCreator storageCreator();

  static DAOFactory getDaoFactory(Config config) {
    DAOFactory daoFactory = null;

    // create DAO factory depends on config values
    //

    try {
      if (config.getDaoType().equals("jdbc")) {
        // create connector depends on config value
        //

        JDBCConnector jdbcConnector = null;

        if (config.getDBMSType().equals("sqlite")) {
          jdbcConnector = new JDBCSQLiteConnector(config.getDbPath() + "/" + config.getSQLiteDbFileName());
        }

        // create factory
        daoFactory = new JDBCDAOFactory(jdbcConnector);
      }
    } catch (IllegalArgumentException e) {
      return null;
    }

    return daoFactory;
  }
}
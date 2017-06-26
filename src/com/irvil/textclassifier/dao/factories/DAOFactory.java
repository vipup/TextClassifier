package com.irvil.textclassifier.dao.factories;

import com.irvil.textclassifier.Config;
import com.irvil.textclassifier.dao.CharacteristicDAO;
import com.irvil.textclassifier.dao.ClassifiableTextDAO;
import com.irvil.textclassifier.dao.StorageCreator;
import com.irvil.textclassifier.dao.VocabularyWordDAO;
import com.irvil.textclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.textclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;

public interface DAOFactory {
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

  ClassifiableTextDAO classifiableTextDAO();

  CharacteristicDAO characteristicDAO();

  VocabularyWordDAO vocabularyWordDAO();

  StorageCreator storageCreator();
}
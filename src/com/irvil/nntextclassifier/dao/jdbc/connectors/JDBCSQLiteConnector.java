package com.irvil.nntextclassifier.dao.jdbc.connectors;

import com.irvil.nntextclassifier.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class JDBCSQLiteConnector implements JDBCConnector {
  private Config config = Config.getInstance();

  public Connection getDBConnection() {
    Connection dbConnection = null;

    try {
      Class.forName("org.sqlite.JDBC");
      dbConnection = DriverManager.getConnection("jdbc:sqlite:" + config.getDbPath() + "/" + config.getSQLiteDbFileName());
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }

    return dbConnection;
  }
}
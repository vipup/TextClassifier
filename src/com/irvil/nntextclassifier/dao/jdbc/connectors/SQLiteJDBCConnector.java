package com.irvil.nntextclassifier.dao.jdbc.connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class SQLiteJDBCConnector implements JDBCConnector {
  public Connection getDBConnection() {
    Connection dbConnection = null;

    try {
      Class.forName("org.sqlite.JDBC");
      dbConnection = DriverManager.getConnection("jdbc:sqlite:./db/NNTextClassifier.db");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }

    return dbConnection;
  }
}
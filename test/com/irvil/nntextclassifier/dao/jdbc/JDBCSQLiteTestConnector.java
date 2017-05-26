package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class JDBCSQLiteTestConnector implements JDBCConnector {
  public Connection getConnection() {
    Connection dbConnection = null;

    try {
      Class.forName("org.sqlite.JDBC");
      dbConnection = DriverManager.getConnection("jdbc:sqlite:./db/test.db");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }

    return dbConnection;
  }
}
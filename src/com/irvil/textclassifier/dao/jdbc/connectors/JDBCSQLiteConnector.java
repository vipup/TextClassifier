package com.irvil.textclassifier.dao.jdbc.connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCSQLiteConnector implements JDBCConnector {
  private final String dbName;

  public JDBCSQLiteConnector(String dbName) {
    if (dbName == null || dbName.equals("")) {
      throw new IllegalArgumentException();
    }

    this.dbName = dbName;
  }

  public Connection getConnection() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException ignored) {
    }

    return DriverManager.getConnection("jdbc:sqlite:" + dbName);
  }
}
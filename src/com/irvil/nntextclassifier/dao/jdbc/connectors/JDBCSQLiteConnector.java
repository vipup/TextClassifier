package com.irvil.nntextclassifier.dao.jdbc.connectors;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCSQLiteConnector implements JDBCConnector {
  private final String dbName;

  public JDBCSQLiteConnector(String dbName) {
    if (!isDbExists(dbName)) {
      throw new IllegalArgumentException("DB file not found");
    }

    this.dbName = dbName;
  }

  public Connection getConnection() {
    Connection dbConnection = null;

    try {
      Class.forName("org.sqlite.JDBC");
      dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    } catch (ClassNotFoundException | SQLException ignored) {
    }

    return dbConnection;
  }

  private boolean isDbExists(String dbName) {
    return new File(dbName).exists();
  }
}
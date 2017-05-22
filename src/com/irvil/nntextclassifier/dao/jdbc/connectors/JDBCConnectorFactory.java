package com.irvil.nntextclassifier.dao.jdbc.connectors;

public class JDBCConnectorFactory {
  public static JDBCConnector getJDBCConnector(String DBType) {
    switch (DBType) {
      case "SQLite":
        return new SQLiteJDBCConnector();
      default:
        throw new IllegalArgumentException();
    }
  }
}
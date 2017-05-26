package com.irvil.nntextclassifier.dao.jdbc.connectors;

public class JDBCConnectorFactory {
  public static JDBCConnector getJDBCConnector(String dbmsType) {
    switch (dbmsType) {
      case "sqlite":
        return new JDBCSQLiteConnector();
      default:
        throw new IllegalArgumentException();
    }
  }
}
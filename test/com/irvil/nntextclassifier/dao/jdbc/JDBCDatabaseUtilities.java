package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;

import java.sql.Connection;
import java.sql.Statement;

class JDBCDatabaseUtilities {
  static void clearTable(JDBCConnector jdbcConnector, String tableName) throws Exception {
    try (Connection con = jdbcConnector.getConnection()) {
      Statement statement = con.createStatement();
      statement.executeUpdate("DELETE FROM " + tableName);
      statement.executeUpdate("DELETE FROM sqlite_sequence WHERE name = '" + tableName + "'");
    }
  }
}
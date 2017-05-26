package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

class JDBCDatabaseUtilities {
  static void cleanTable(String tableName) throws Exception {
    try (Connection con = new JDBCSQLiteTestConnector().getConnection()) {
      Statement statement = con.createStatement();
      statement.executeUpdate("DELETE FROM " + tableName);
    }
  }

  static void insertToCatalog(String tableName, Catalog catalog) throws Exception {
    try (Connection con = new JDBCSQLiteTestConnector().getConnection()) {
      PreparedStatement statement = con.prepareStatement("INSERT INTO " + tableName + " (id, value) VALUES (?, ?)");
      statement.setInt(1, catalog.getId());
      statement.setString(2, catalog.getValue());
      statement.executeUpdate();
    }
  }

  static void insertIncomingCall(IncomingCall incomingCall) throws Exception {
    String sql = "INSERT INTO IncomingCalls (Text, Module, Handler) VALUES (?, ?, ?)";
    try (Connection con = new JDBCSQLiteTestConnector().getConnection()) {
      PreparedStatement statement = con.prepareStatement(sql);
      statement.setString(1, incomingCall.getText());
      statement.setString(2, incomingCall.getModule().getValue());
      statement.setString(3, incomingCall.getHandler().getValue());
      statement.executeUpdate();
    }
  }
}
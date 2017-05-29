package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;

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

  static void insertToCatalog(String tableName, Characteristic characteristic) throws Exception {
    try (Connection con = new JDBCSQLiteTestConnector().getConnection()) {
      PreparedStatement statement = con.prepareStatement("INSERT INTO " + tableName + " (id, value) VALUES (?, ?)");
      statement.setInt(1, characteristic.getId());
      statement.setString(2, characteristic.getValue());
      statement.executeUpdate();
    }
  }

  static void insertToVocabulary(VocabularyWord vw) throws Exception {
    try (Connection con = new JDBCSQLiteTestConnector().getConnection()) {
      PreparedStatement statement = con.prepareStatement("INSERT INTO Vocabulary (id, value) VALUES (?, ?)");
      statement.setInt(1, vw.getId());
      statement.setString(2, vw.getValue());
      statement.executeUpdate();
    }
  }

  static void insertIncomingCall(IncomingCall incomingCall) throws Exception {
    String sql = "INSERT INTO IncomingCalls (Text, Module, Handler) VALUES (?, ?, ?)";
    try (Connection con = new JDBCSQLiteTestConnector().getConnection()) {
      PreparedStatement statement = con.prepareStatement(sql);
      statement.setString(1, incomingCall.getText());
      statement.setString(2, incomingCall.getCharacteristic("Module").getValue());
      statement.setString(3, incomingCall.getCharacteristic("Handler").getValue());
      statement.executeUpdate();
    }
  }
}
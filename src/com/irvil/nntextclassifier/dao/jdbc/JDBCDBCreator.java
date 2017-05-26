package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCDBCreator implements StorageCreator {
  private JDBCConnector connector;

  public JDBCDBCreator(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public void createStorage() {
    List<String> sqlQueries = new ArrayList<>();

    // create database structure
    //

    sqlQueries.add("CREATE TABLE IF NOT EXISTS IncomingCalls " +
        "( Text TEXT, Category TEXT, Module TEXT, Handler TEXT )");
    sqlQueries.add("CREATE TABLE IF NOT EXISTS Vocabulary " +
        "( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT UNIQUE )");
    sqlQueries.add("CREATE TABLE IF NOT EXISTS Modules " +
        "( Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT )");
    sqlQueries.add("CREATE TABLE IF NOT EXISTS Categories " +
        "( Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT )");
    sqlQueries.add("CREATE TABLE IF NOT EXISTS Handlers " +
        "( Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT )");

    // delete all records if tables already exists
    //

    sqlQueries.add("DELETE FROM Modules");
    sqlQueries.add("DELETE FROM Categories");
    sqlQueries.add("DELETE FROM Handlers");
    sqlQueries.add("DELETE FROM Vocabulary");

    // reset autoincrement keys
    sqlQueries.add("DELETE FROM sqlite_sequence WHERE name IN ('Modules', 'Categories', 'Handlers', 'Vocabulary')");

    executeQueries(sqlQueries);
  }

  private void executeQueries(List<String> sqlQueries) {
    try (Connection con = connector.getConnection()) {
      Statement stmnt = con.createStatement();

      for (String sqlQuery : sqlQueries) {
        stmnt.execute(sqlQuery);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.StorageCreator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCDBCreator implements StorageCreator {
  @Override
  public void createStorage() {
    List<String> sql = new ArrayList<>();

    sql.add("CREATE TABLE IF NOT EXISTS IncomingCalls " +
        "( Text TEXT, Category TEXT, Module TEXT, Handler TEXT )");
    sql.add("CREATE TABLE IF NOT EXISTS Vocabulary " +
        "( Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT UNIQUE )");
    sql.add("CREATE TABLE IF NOT EXISTS Modules " +
        "( Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT )");
    sql.add("CREATE TABLE IF NOT EXISTS Categories " +
        "( Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT )");
    sql.add("CREATE TABLE IF NOT EXISTS Handlers " +
        "( Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, Value TEXT )");

    sql.add("DELETE FROM Modules");
    sql.add("DELETE FROM Categories");
    sql.add("DELETE FROM Handlers");
    sql.add("DELETE FROM Vocabulary");
    sql.add("DELETE FROM sqlite_sequence WHERE name IN ('Modules', 'Categories', 'Handlers', 'Vocabulary')");

    try (Connection con = DBConnector.getDBConnection()) {
      Statement stmnt = con.createStatement();

      for (String query : sql) {
        stmnt.execute(query);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
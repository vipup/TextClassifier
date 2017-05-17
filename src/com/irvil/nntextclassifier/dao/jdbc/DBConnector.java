package com.irvil.nntextclassifier.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// todo: create DBConnector strategy. Make getDBConnection non static
// todo: DependencyInjection pattern
public class DBConnector {
  public static Connection getDBConnection() {
    Connection dbConnection = null;

    try {
      Class.forName("org.sqlite.JDBC");
      dbConnection = DriverManager.getConnection("jdbc:sqlite:./db/NNTextClassifier.db");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }

    return dbConnection;
  }
}
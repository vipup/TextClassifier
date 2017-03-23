package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.HandlerDAO;
import com.irvil.nntextclassifier.model.Handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCHandlerDAO implements HandlerDAO {
  @Override
  public int getCount() {
    return 0;
  }

  @Override
  public List<Handler> getAll() {
    return null;
  }

  @Override
  public Handler findByID(int id) {
    return null;
  }

  @Override
  public Handler findByValue(String value) {
    return null;
  }

  @Override
  public void add(Handler object) {
    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement insertStatement = con.prepareStatement("INSERT INTO Handlers (value) VALUES (?)");
      insertStatement.setString(1, object.getValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

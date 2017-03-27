package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCHandlerDAO extends JDBCCatalogDAO<Handler> {
  @Override
  protected String getTableName() {
    return "Handlers";
  }

  @Override
  public Handler findByID(int id) {
    ResultSet rs = getResultSetByID(id);

    try {
      return new Handler(rs.getInt("Id"), rs.getString("Value"));
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}

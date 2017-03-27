package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Category;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCCategoryDAO extends JDBCCatalogDAO<Category> {
  @Override
  protected String getTableName() {
    return "Categories";
  }

  @Override
  public Category findByID(int id) {
    ResultSet rs = getResultSetByID(id);

    try {
      return new Category(rs.getInt("Id"), rs.getString("Value"));
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
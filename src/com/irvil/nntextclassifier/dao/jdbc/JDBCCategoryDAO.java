package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CategoryDAO;
import com.irvil.nntextclassifier.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCCategoryDAO implements CategoryDAO {
  @Override
  public int getCount() {
    return 0;
  }

  @Override
  public List<Category> getAll() {
    return null;
  }

  @Override
  public Category findByID(int id) {
    return null;
  }

  @Override
  public Category findByValue(String value) {
    return null;
  }

  @Override
  public void add(Category object) {
    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement insertStatement = con.prepareStatement("INSERT INTO Categories (value) VALUES (?)");
      insertStatement.setString(1, object.getValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
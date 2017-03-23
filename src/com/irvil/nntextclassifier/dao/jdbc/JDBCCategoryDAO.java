package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CategoryDAO;
import com.irvil.nntextclassifier.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCCategoryDAO implements CategoryDAO {
  @Override
  public int getCount() {
    int count = 0;

    try (Connection con = DBConnector.getDBConnection()) {
      ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM Categories AS Count");

      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return count;
  }

  @Override
  public List<Category> getAll() {
    return null;
  }

  @Override
  public Category findByID(int id) {
    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement statement = con.prepareStatement("SELECT * FROM Categories WHERE Id = ?");
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        return new Category(rs.getInt("Id"), rs.getString("Value"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

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

  @Override
  public Category findByVector(double[] vector) {
    return findByID(getIndexOfMaxValue(vector) + 1);
  }

  // todo: move to utils
  private int getIndexOfMaxValue(double[] vector) {
    int maxIndex = 0;
    double maxValue = vector[0];

    for (int i = 1; i < vector.length; i++) {
      if (vector[i] > maxValue) {
        maxValue = vector[i];
        maxIndex = i;
      }
    }

    return maxIndex;
  }
}
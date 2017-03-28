package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.Catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class JDBCGenericDAO<T extends Catalog> implements GenericDAO<T> {
  @Override
  public int getCount() {
    int count = 0;
    String sql = "SELECT COUNT(*) FROM " + getTableName();

    try (Connection con = DBConnector.getDBConnection()) {
      ResultSet rs = con.createStatement().executeQuery(sql);

      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return count;
  }

  @Override
  public T findByID(int id) {
    String sql = "SELECT Value FROM " + getTableName() + " WHERE Id = ?";

    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement statement = con.prepareStatement(sql);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        return createObject(id, rs.getString("Value"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public void add(T object) {
    String sql = "INSERT INTO " + getTableName() + " (Value) VALUES (?)";

    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement insertStatement = con.prepareStatement(sql);
      insertStatement.setString(1, object.getValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  protected abstract String getTableName();

  protected abstract T createObject(int id, String value);
}
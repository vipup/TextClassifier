package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class JDBCGenericDAO<T extends Catalog> implements GenericDAO<T> {
  private JDBCConnector connector;

  JDBCGenericDAO(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public int getCount() {
    int count = 0;
    String sql = "SELECT COUNT(*) FROM " + getTableName();

    try (Connection con = connector.getConnection()) {
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

    try (Connection con = connector.getConnection()) {
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
  public T findByValue(String value) {
    String sql = "SELECT Id FROM " + getTableName() + " WHERE Value = ?";

    try (Connection con = connector.getConnection()) {
      PreparedStatement statement = con.prepareStatement(sql);
      statement.setString(1, value);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        return createObject(rs.getInt("Id"), value);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public T findByVector(double[] vector) {
    if (vector != null && vector.length > 0) {
      return findByID(getIndexOfMaxValue(vector) + 1);
    }

    return null;
  }

  @Override
  public void add(T object) {
    String sql = "INSERT INTO " + getTableName() + " (Value) VALUES (?)";

    try (Connection con = connector.getConnection()) {
      PreparedStatement insertStatement = con.prepareStatement(sql);
      insertStatement.setString(1, object.getValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private int getIndexOfMaxValue(double[] vector) {
    int indexOfMaxValue = 0;
    double maxValue = vector[0];

    for (int i = 1; i < vector.length; i++) {
      if (vector[i] > maxValue) {
        maxValue = vector[i];
        indexOfMaxValue = i;
      }
    }

    return indexOfMaxValue;
  }

  protected abstract String getTableName();

  protected abstract T createObject(int id, String value);
}
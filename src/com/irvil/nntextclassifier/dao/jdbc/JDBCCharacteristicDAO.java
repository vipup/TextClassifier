package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract class JDBCCharacteristicDAO<T> implements CharacteristicDAO<T> {
  private JDBCConnector connector;

  JDBCCharacteristicDAO(JDBCConnector connector) {
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
  public List<T> getAll() {
    List<T> list = new ArrayList<>();
    String sql = "SELECT Id, Value FROM " + getTableName();

    try (Connection con = connector.getConnection()) {
      ResultSet rs = con.createStatement().executeQuery(sql);

      while (rs.next()) {
        list.add(createObject(rs.getInt("Id"), rs.getString("Value")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
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
  public void add(T object) {
    String sql = "INSERT INTO " + getTableName() + " (Value) VALUES (?)";

    try (Connection con = connector.getConnection()) {
      PreparedStatement insertStatement = con.prepareStatement(sql);
      insertStatement.setString(1, object.toString());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  protected abstract String getTableName();

  protected abstract T createObject(int id, String value);
}
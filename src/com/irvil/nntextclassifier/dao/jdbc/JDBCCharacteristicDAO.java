package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCCharacteristicDAO implements CharacteristicDAO {
  private JDBCConnector connector;
  private String characteristicName;

  public JDBCCharacteristicDAO(String characteristicName, JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.characteristicName = characteristicName;
    this.connector = connector;
  }

  @Override
  public List<Characteristic> getAll() {
    List<Characteristic> characteristics = new ArrayList<>();

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsValues.Id, CharacteristicsValues.Value " +
          "FROM CharacteristicsValues JOIN CharacteristicsNames " +
          "ON CharacteristicsNames.Id = CharacteristicsValues.CharacteristicsNameId " +
          "WHERE CharacteristicsNames.Name = ?";

      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, characteristicName);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        characteristics.add(new Characteristic(rs.getInt("Id"), rs.getString("Value")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return characteristics;
  }

  @Override
  public void add(Characteristic characteristic) {
    if (characteristic != null &&
        !characteristic.getValue().equals("") &&
        !isCharacteristicExistsInDB(characteristic)) {
      try (Connection con = connector.getConnection()) {
        String sqlInsert = "INSERT INTO CharacteristicsValues (Id, CharacteristicsNameId, Value) VALUES (?, ?, ?)";
        PreparedStatement statement = con.prepareStatement(sqlInsert);
        statement.setInt(1, getLastCharacteristicsValueId() + 1);
        statement.setInt(2, getCharacteristicNameId());
        statement.setString(3, characteristic.getValue());
        statement.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isCharacteristicExistsInDB(Characteristic characteristic) {
    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsValues.Id " +
          "FROM CharacteristicsValues JOIN CharacteristicsNames " +
          "ON CharacteristicsNames.Id = CharacteristicsValues.CharacteristicsNameId " +
          "WHERE CharacteristicsNames.Name = ? AND CharacteristicsValues.Value = ?";

      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, characteristicName);
      statement.setString(2, characteristic.getValue());
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  private int getLastCharacteristicsValueId() {
    int maxCharacteristicsValueId = 0;

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT MAX(CharacteristicsValues.Id) AS MaxID " +
          "FROM CharacteristicsValues JOIN CharacteristicsNames " +
          "ON CharacteristicsNames.Id = CharacteristicsValues.CharacteristicsNameId " +
          "WHERE CharacteristicsNames.Name = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, characteristicName);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) { // found
        maxCharacteristicsValueId = rs.getInt("MaxID"); // get characteristic name Id from DB
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return maxCharacteristicsValueId;
  }

  private int getCharacteristicNameId() {
    int characteristicNameId = 0;

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT Id FROM CharacteristicsNames WHERE Name = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, characteristicName);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) { // found
        characteristicNameId = rs.getInt("Id");
      } else { // not found
        characteristicNameId = insertCharacteristicName();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return characteristicNameId;
  }

  private int insertCharacteristicName() {
    int characteristicNameId = 0;

    try (Connection con = connector.getConnection()) {
      String sqlInsert = "INSERT INTO CharacteristicsNames (Name) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, characteristicName);
      statement.executeUpdate();

      // get inserted row Id
      //

      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        characteristicNameId = generatedKeys.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return characteristicNameId;
  }
}
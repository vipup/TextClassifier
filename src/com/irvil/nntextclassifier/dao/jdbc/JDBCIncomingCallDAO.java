package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCIncomingCallDAO implements IncomingCallDAO {
  private JDBCConnector connector;

  public JDBCIncomingCallDAO(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public List<IncomingCall> getAll() {
    List<IncomingCall> incomingCalls = new ArrayList<>();

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT Id, Text FROM IncomingCalls";
      ResultSet rs = con.createStatement().executeQuery(sqlSelect);

      while (rs.next()) {
        incomingCalls.add(new IncomingCall(rs.getString("Text"), getCharacteristics(rs.getInt("Id"))));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return incomingCalls;
  }

  private Map<String, Characteristic> getCharacteristics(int incomingCallId) {
    Map<String, Characteristic> characteristics = new HashMap<>();

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsNames.Name AS CharacteristicName, " +
          "CharacteristicsValues.Id AS CharacteristicId, " +
          "CharacteristicsValues.Value AS CharacteristicValue " +
          "FROM IncomingCallsCharacteristics " +
          "LEFT JOIN CharacteristicsNames " +
          "ON IncomingCallsCharacteristics.CharacteristicsNameId = CharacteristicsNames.Id " +
          "LEFT JOIN CharacteristicsValues " +
          "ON IncomingCallsCharacteristics.CharacteristicsValueId = CharacteristicsValues.Id " +
          "AND IncomingCallsCharacteristics.CharacteristicsNameId = CharacteristicsValues.CharacteristicsNameId " +
          "WHERE IncomingCallsCharacteristics.IncomingCallId = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setInt(1, incomingCallId);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        characteristics.put(rs.getString("CharacteristicName"), new Characteristic(rs.getInt("CharacteristicId"), rs.getString("CharacteristicValue")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return characteristics;
  }

  @Override
  public void add(IncomingCall incomingCall) {
    if (incomingCall != null &&
        incomingCall.getCharacteristics() != null &&
        incomingCall.getCharacteristics().size() != 0 &&
        isCharacteristicsValuesExists(incomingCall)) {

      int incomingCallId = insertToIncomingCallsTable(incomingCall);

      // save all characteristics to DB
      //

      for (Map.Entry<String, Characteristic> entry : incomingCall.getCharacteristics().entrySet()) {
        insertToIncomingCallsCharacteristicsTable(incomingCallId, entry.getKey(), entry.getValue().getValue());
      }
    }
  }

  private boolean isCharacteristicsValuesExists(IncomingCall incomingCall) {
    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsValues.Id " +
          "FROM CharacteristicsValues JOIN CharacteristicsNames " +
          "ON CharacteristicsValues.CharacteristicsNameId = CharacteristicsNames.Id " +
          "WHERE CharacteristicsNames.Name = ? AND CharacteristicsValues.Value = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);

      for (Map.Entry<String, Characteristic> entry : incomingCall.getCharacteristics().entrySet()) {
        statement.setString(1, entry.getKey());
        statement.setString(2, entry.getValue().getValue());
        ResultSet rs = statement.executeQuery();

        if (!rs.next()) {
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return true;
  }

  private int insertToIncomingCallsTable(IncomingCall incomingCall) {
    int incomingCallId = 0;

    try (Connection con = connector.getConnection()) {
      String sqlInsert = "INSERT INTO IncomingCalls (Text) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, incomingCall.getText());
      statement.executeUpdate();

      ResultSet generatedKeys = statement.getGeneratedKeys();

      if (generatedKeys.next()) {
        incomingCallId = generatedKeys.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return incomingCallId;
  }

  private void insertToIncomingCallsCharacteristicsTable(int incomingCallId, String characteristicsName, String characteristicsValue) {
    try (Connection con = connector.getConnection()) {
      String sqlInsert = "INSERT INTO IncomingCallsCharacteristics (IncomingCallId, CharacteristicsNameId, CharacteristicsValueId) VALUES (?, ?, ?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert);
      statement.setInt(1, incomingCallId);
      statement.setInt(2, getCharacteristicsNameId(characteristicsName));
      statement.setInt(3, getCharacteristicsValueId(characteristicsName, characteristicsValue));
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private int getCharacteristicsValueId(String characteristicsName, String characteristicsValue) {
    int characteristicsValueId = 0;

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsValues.Id " +
          "FROM CharacteristicsValues JOIN CharacteristicsNames " +
          "ON CharacteristicsNames.Id = CharacteristicsValues.CharacteristicsNameId " +
          "WHERE CharacteristicsNames.Name = ? AND CharacteristicsValues.Value = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, characteristicsName);
      statement.setString(2, characteristicsValue);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        characteristicsValueId = rs.getInt("Id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return characteristicsValueId;
  }

  private int getCharacteristicsNameId(String characteristicsName) {
    int characteristicsNameId = 0;

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT Id FROM CharacteristicsNames WHERE Name = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, characteristicsName);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        characteristicsNameId = rs.getInt("Id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return characteristicsNameId;
  }
}
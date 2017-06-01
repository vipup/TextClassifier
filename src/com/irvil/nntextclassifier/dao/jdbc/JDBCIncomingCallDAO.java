package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.EmptyRecordException;
import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.NotExistsException;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
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
        incomingCalls.add(new IncomingCall(rs.getString("Text"), getCharacteristicsValues(rs.getInt("Id"))));
      }
    } catch (SQLException ignored) {
    }

    return incomingCalls;
  }

  @Override
  public void add(IncomingCall incomingCall) throws EmptyRecordException, NotExistsException {
    if (incomingCall == null ||
        incomingCall.getText().equals("") ||
        incomingCall.getCharacteristics() == null ||
        incomingCall.getCharacteristics().size() == 0) {
      throw new EmptyRecordException("Incoming call is null or empty");
    }

    try (Connection con = connector.getConnection()) {
      if (!fillCharacteristicNamesAndValuesIDs(incomingCall)) {
        throw new NotExistsException("Characteristic value not exists");
      }

      String sqlInsert = "INSERT INTO IncomingCalls (Text) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, incomingCall.getText());
      statement.executeUpdate();

      ResultSet generatedKeys = statement.getGeneratedKeys();

      if (generatedKeys.next()) {
        // save all characteristics to DB
        //

        for (Map.Entry<Characteristic, CharacteristicValue> entry : incomingCall.getCharacteristics().entrySet()) {
          insertToIncomingCallsCharacteristicsTable(generatedKeys.getInt(1), entry.getKey(), entry.getValue());
        }
      }
    } catch (SQLException ignored) {
    }
  }

  private Map<Characteristic, CharacteristicValue> getCharacteristicsValues(int incomingCallId) throws SQLException {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsNames.Id AS CharacteristicId, " +
          "CharacteristicsNames.Name AS CharacteristicName, " +
          "CharacteristicsValues.Id AS CharacteristicValueId, " +
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
        Characteristic characteristic = new Characteristic(rs.getInt("CharacteristicId"), rs.getString("CharacteristicName"));
        CharacteristicValue characteristicValue = new CharacteristicValue(rs.getInt("CharacteristicValueId"), rs.getString("CharacteristicValue"));
        characteristics.put(characteristic, characteristicValue);
      }
    }

    return characteristics;
  }

  private boolean fillCharacteristicNamesAndValuesIDs(IncomingCall incomingCall) throws SQLException {
    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT CharacteristicsNames.Id AS CharacteristicId, " +
          "CharacteristicsValues.Id AS CharacteristicValueId " +
          "FROM CharacteristicsValues JOIN CharacteristicsNames " +
          "ON CharacteristicsValues.CharacteristicsNameId = CharacteristicsNames.Id " +
          "WHERE CharacteristicsNames.Name = ? AND CharacteristicsValues.Value = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);

      for (Map.Entry<Characteristic, CharacteristicValue> entry : incomingCall.getCharacteristics().entrySet()) {
        statement.setString(1, entry.getKey().getName());
        statement.setString(2, entry.getValue().getValue());
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
          entry.getKey().setId(rs.getInt("CharacteristicId"));
          entry.getValue().setId(rs.getInt("CharacteristicValueId"));
        } else {
          return false;
        }
      }
    }

    return true;
  }

  private void insertToIncomingCallsCharacteristicsTable(int incomingCallId, Characteristic characteristic, CharacteristicValue characteristicValue) throws SQLException {
    try (Connection con = connector.getConnection()) {
      String sqlInsert = "INSERT INTO IncomingCallsCharacteristics (IncomingCallId, CharacteristicsNameId, CharacteristicsValueId) VALUES (?, ?, ?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert);
      statement.setInt(1, incomingCallId);
      statement.setInt(2, characteristic.getId());
      statement.setInt(3, characteristicValue.getId());
      statement.executeUpdate();
    }
  }
}
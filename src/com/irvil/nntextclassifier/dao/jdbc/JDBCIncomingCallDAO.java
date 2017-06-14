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
        incomingCalls.add(new IncomingCall(rs.getString("Text"), getCharacteristicsValues(con, rs.getInt("Id"))));
      }
    } catch (SQLException ignored) {
    }

    return incomingCalls;
  }

  @Override
  public void addAll(List<IncomingCall> incomingCalls) throws EmptyRecordException, NotExistsException {
    if (incomingCalls == null ||
        incomingCalls.size() == 0) {
      throw new EmptyRecordException("Incoming calls is null or empty");
    }

    try (Connection con = connector.getConnection()) {
      con.setAutoCommit(false);

      // prepare sql query
      //

      String sqlInsert = "INSERT INTO IncomingCalls (Text) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

      //

      for (IncomingCall incomingCall : incomingCalls) {
        // check
        //

        if (incomingCall == null ||
            incomingCall.getText().equals("") ||
            incomingCall.getCharacteristics() == null ||
            incomingCall.getCharacteristics().size() == 0) {
          throw new EmptyRecordException("Incoming call is null or empty");
        }

        if (!fillCharacteristicNamesAndValuesIDs(con, incomingCall)) {
          throw new NotExistsException("Characteristic value not exists");
        }

        // insert
        //

        statement.setString(1, incomingCall.getText());
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();

        if (generatedKeys.next()) {
          // save all characteristics to DB
          //

          for (Map.Entry<Characteristic, CharacteristicValue> entry : incomingCall.getCharacteristics().entrySet()) {
            insertToIncomingCallsCharacteristicsTable(con, generatedKeys.getInt(1), entry.getKey(), entry.getValue());
          }
        }
      }

      con.commit();
      con.setAutoCommit(true);
    } catch (SQLException ignored) {
      ignored.printStackTrace();
    }
  }

  private Map<Characteristic, CharacteristicValue> getCharacteristicsValues(Connection con, int incomingCallId) throws SQLException {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();

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

    return characteristics;
  }

  private boolean fillCharacteristicNamesAndValuesIDs(Connection con, IncomingCall incomingCall) throws SQLException {
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

    return true;
  }

  private void insertToIncomingCallsCharacteristicsTable(Connection con, int incomingCallId, Characteristic characteristic, CharacteristicValue characteristicValue) throws SQLException {
    String sqlInsert = "INSERT INTO IncomingCallsCharacteristics (IncomingCallId, CharacteristicsNameId, CharacteristicsValueId) VALUES (?, ?, ?)";
    PreparedStatement statement = con.prepareStatement(sqlInsert);
    statement.setInt(1, incomingCallId);
    statement.setInt(2, characteristic.getId());
    statement.setInt(3, characteristicValue.getId());
    statement.executeUpdate();
  }
}
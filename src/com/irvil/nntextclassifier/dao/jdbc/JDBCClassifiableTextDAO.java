package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.ClassifiableTextDAO;
import com.irvil.nntextclassifier.dao.EmptyRecordException;
import com.irvil.nntextclassifier.dao.NotExistsException;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.ClassifiableText;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCClassifiableTextDAO implements ClassifiableTextDAO {
  private JDBCConnector connector;

  public JDBCClassifiableTextDAO(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public List<ClassifiableText> getAll() {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();

    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT Id, Text FROM ClassifiableTexts";
      ResultSet rs = con.createStatement().executeQuery(sqlSelect);

      while (rs.next()) {
        classifiableTexts.add(new ClassifiableText(rs.getString("Text"), getCharacteristicsValues(con, rs.getInt("Id"))));
      }
    } catch (SQLException ignored) {
    }

    return classifiableTexts;
  }

  @Override
  public void addAll(List<ClassifiableText> classifiableTexts) throws EmptyRecordException, NotExistsException {
    if (classifiableTexts == null ||
        classifiableTexts.size() == 0) {
      throw new EmptyRecordException("Classifiable texts is null or empty");
    }

    try (Connection con = connector.getConnection()) {
      con.setAutoCommit(false);

      // prepare sql query
      //

      String sqlInsert = "INSERT INTO ClassifiableTexts (Text) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

      //

      for (ClassifiableText classifiableText : classifiableTexts) {
        // check
        //

        if (classifiableText == null ||
            classifiableText.getText().equals("") ||
            classifiableText.getCharacteristics() == null ||
            classifiableText.getCharacteristics().size() == 0) {
          throw new EmptyRecordException("Classifiable text is null or empty");
        }

        if (!fillCharacteristicNamesAndValuesIDs(con, classifiableText)) {
          throw new NotExistsException("Characteristic value not exists");
        }

        // insert
        //

        statement.setString(1, classifiableText.getText());
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();

        if (generatedKeys.next()) {
          // save all characteristics to DB
          //

          for (Map.Entry<Characteristic, CharacteristicValue> entry : classifiableText.getCharacteristics().entrySet()) {
            insertToClassifiableTextsCharacteristicsTable(con, generatedKeys.getInt(1), entry.getKey(), entry.getValue());
          }
        }
      }

      con.commit();
      con.setAutoCommit(true);
    } catch (SQLException ignored) {
      ignored.printStackTrace();
    }
  }

  private Map<Characteristic, CharacteristicValue> getCharacteristicsValues(Connection con, int classifiableTextId) throws SQLException {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();

    String sqlSelect = "SELECT CharacteristicsNames.Id AS CharacteristicId, " +
        "CharacteristicsNames.Name AS CharacteristicName, " +
        "CharacteristicsValues.Id AS CharacteristicValueId, " +
        "CharacteristicsValues.Value AS CharacteristicValue " +
        "FROM ClassifiableTextsCharacteristics " +
        "LEFT JOIN CharacteristicsNames " +
        "ON ClassifiableTextsCharacteristics.CharacteristicsNameId = CharacteristicsNames.Id " +
        "LEFT JOIN CharacteristicsValues " +
        "ON ClassifiableTextsCharacteristics.CharacteristicsValueId = CharacteristicsValues.Id " +
        "AND ClassifiableTextsCharacteristics.CharacteristicsNameId = CharacteristicsValues.CharacteristicsNameId " +
        "WHERE ClassifiableTextsCharacteristics.ClassifiableTextId = ?";
    PreparedStatement statement = con.prepareStatement(sqlSelect);
    statement.setInt(1, classifiableTextId);
    ResultSet rs = statement.executeQuery();

    while (rs.next()) {
      Characteristic characteristic = new Characteristic(rs.getInt("CharacteristicId"), rs.getString("CharacteristicName"));
      CharacteristicValue characteristicValue = new CharacteristicValue(rs.getInt("CharacteristicValueId"), rs.getString("CharacteristicValue"));
      characteristics.put(characteristic, characteristicValue);
    }

    return characteristics;
  }

  private boolean fillCharacteristicNamesAndValuesIDs(Connection con, ClassifiableText classifiableText) throws SQLException {
    String sqlSelect = "SELECT CharacteristicsNames.Id AS CharacteristicId, " +
        "CharacteristicsValues.Id AS CharacteristicValueId " +
        "FROM CharacteristicsValues JOIN CharacteristicsNames " +
        "ON CharacteristicsValues.CharacteristicsNameId = CharacteristicsNames.Id " +
        "WHERE CharacteristicsNames.Name = ? AND CharacteristicsValues.Value = ?";
    PreparedStatement statement = con.prepareStatement(sqlSelect);

    for (Map.Entry<Characteristic, CharacteristicValue> entry : classifiableText.getCharacteristics().entrySet()) {
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

  private void insertToClassifiableTextsCharacteristicsTable(Connection con, int classifiableTextId, Characteristic characteristic, CharacteristicValue characteristicValue) throws SQLException {
    String sqlInsert = "INSERT INTO ClassifiableTextsCharacteristics (ClassifiableTextId, CharacteristicsNameId, CharacteristicsValueId) VALUES (?, ?, ?)";
    PreparedStatement statement = con.prepareStatement(sqlInsert);
    statement.setInt(1, classifiableTextId);
    statement.setInt(2, characteristic.getId());
    statement.setInt(3, characteristicValue.getId());
    statement.executeUpdate();
  }
}
package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    List<IncomingCall> list = new ArrayList<>();
    String sql =
        "SELECT IncomingCalls.Text, " +
            "Modules.Id AS ModuleId, Modules.Value AS ModuleValue," +
            "Handlers.Id AS HandlerId, Handlers.Value AS HandlerValue, " +
            "Categories.Id AS CategoryId, Categories.Value AS CategoryValue FROM IncomingCalls " +
            "LEFT JOIN Modules ON IncomingCalls.Module = Modules.Value " +
            "LEFT JOIN Categories ON IncomingCalls.Category = Categories.Value " +
            "LEFT JOIN Handlers ON IncomingCalls.Handler = Handlers.Value";

    try (Connection con = connector.getConnection()) {
      ResultSet rs = con.createStatement().executeQuery(sql);

      while (rs.next()) {
        Map<String, Characteristic> characteristics = new HashMap<>();

        characteristics.put("Module", new Characteristic(rs.getInt("ModuleId"), rs.getString("ModuleValue")));
        characteristics.put("Handler", new Characteristic(rs.getInt("HandlerId"), rs.getString("HandlerValue")));

        list.add(new IncomingCall(rs.getString("Text"), characteristics));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
  }

  @Override
  public List<Characteristic> getUniqueValueOfCharacteristic(String characteristicName) {
    List<Characteristic> uniqueCharacteristics = new ArrayList<>();
    String sql = "SELECT DISTINCT " + characteristicName + " FROM IncomingCalls";

    try (Connection con = connector.getConnection()) {
      ResultSet rs = con.createStatement().executeQuery(sql);

      while (rs.next()) {
        uniqueCharacteristics.add(new Characteristic(0, rs.getString(characteristicName)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return uniqueCharacteristics;
  }
}
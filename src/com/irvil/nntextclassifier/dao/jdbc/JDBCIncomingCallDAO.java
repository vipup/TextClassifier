package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        Module module = new Module(rs.getInt("ModuleId"), rs.getString("ModuleValue"));
        Handler handler = new Handler(rs.getInt("HandlerId"), rs.getString("HandlerValue"));

        list.add(new IncomingCall(rs.getString("Text"), module, handler));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
  }

  @Override
  public List<Module> getUniqueModules() {
    List<Module> modules = new ArrayList<>();

    for (String value : getUniqueCatalog("Module")) {
      modules.add(new Module(0, value));
    }

    return modules;
  }

  @Override
  public List<Handler> getUniqueHandlers() {
    List<Handler> handlers = new ArrayList<>();

    for (String value : getUniqueCatalog("Handler")) {
      handlers.add(new Handler(0, value));
    }

    return handlers;
  }

  private List<String> getUniqueCatalog(String fieldName) {
    List<String> catalog = new ArrayList<>();
    String sql = "SELECT DISTINCT " + fieldName + " FROM IncomingCalls";

    try (Connection con = connector.getConnection()) {
      ResultSet rs = con.createStatement().executeQuery(sql);

      while (rs.next()) {
        catalog.add(rs.getString(fieldName));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return catalog;
  }
}
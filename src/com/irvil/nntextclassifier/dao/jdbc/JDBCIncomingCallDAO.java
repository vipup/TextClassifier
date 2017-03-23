package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCIncomingCallDAO implements IncomingCallDAO {
  @Override
  public int getCount() {
    return 0;
  }

  @Override
  public List<IncomingCall> getAll() {
    List<IncomingCall> list = new ArrayList<>();

    try (Connection con = DBConnector.getDBConnection()) {
      String sql =
          "SELECT IncomingCalls.Text, " +
              "Modules.Id AS ModuleId, Modules.Value AS ModuleValue," +
              "Handlers.Id AS HandlerId, Handlers.Value AS HandlerValue, " +
              "Categories.Id AS CategoryId, Categories.Value AS CategoryValue FROM IncomingCalls " +
              "LEFT JOIN Modules ON IncomingCalls.Module = Modules.Value " +
              "LEFT JOIN Categories ON IncomingCalls.Category = Categories.Value " +
              "LEFT JOIN Handlers ON IncomingCalls.Handler = Handlers.Value";

      ResultSet rs = con.createStatement().executeQuery(sql);

      while (rs.next()) {
        Module module = new Module(rs.getInt("ModuleId"), rs.getString("ModuleValue"));
        Handler handler = new Handler(rs.getInt("HandlerId"), rs.getString("HandlerValue"));
        Category category = new Category(rs.getInt("CategoryId"), rs.getString("CategoryValue"));

        list.add(new IncomingCall(rs.getString("Text"), module, handler, category));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return list;
  }

  @Override
  public IncomingCall findByID(int id) {
    return null;
  }

  @Override
  public IncomingCall findByValue(String value) {
    return null;
  }

  @Override
  public void add(IncomingCall object) {

  }

  @Override
  public List<Module> getAllModules() {
    List<Module> modules = new ArrayList<>();
    List<String> catalog = getCatalog("Module");

    for (String value : catalog) {
      modules.add(new Module(0, value));
    }

    return modules;
  }

  @Override
  public List<Handler> getAllHandlers() {
    List<Handler> handlers = new ArrayList<>();
    List<String> catalog = getCatalog("Handler");

    for (String value : catalog) {
      handlers.add(new Handler(0, value));
    }

    return handlers;
  }

  @Override
  public List<Category> getAllCategories() {
    List<Category> categories = new ArrayList<>();
    List<String> catalog = getCatalog("Category");

    for (String value : catalog) {
      categories.add(new Category(0, value));
    }

    return categories;
  }

  private List<String> getCatalog(String fieldName) {
    List<String> catalog = new ArrayList<>();

    try (Connection con = DBConnector.getDBConnection()) {
      ResultSet rs = con.createStatement().executeQuery("SELECT DISTINCT " + fieldName + " FROM IncomingCalls");

      while (rs.next()) {
        catalog.add(rs.getString(fieldName));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return catalog;
  }
}
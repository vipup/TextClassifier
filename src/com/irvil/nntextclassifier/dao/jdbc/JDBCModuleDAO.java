package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Module;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCModuleDAO extends JDBCCatalogDAO<Module> {
  @Override
  protected String getTableName() {
    return "Modules";
  }

  @Override
  public Module findByID(int id) {
    ResultSet rs = getResultSetByID(id);

    try {
      return new Module(rs.getInt("Id"), rs.getString("Value"));
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
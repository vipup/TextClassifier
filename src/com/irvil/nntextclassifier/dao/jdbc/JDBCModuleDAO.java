package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Module;

public class JDBCModuleDAO extends JDBCCatalogDAO<Module> {
  public JDBCModuleDAO(JDBCConnector connector) {
    super(connector);
  }

  @Override
  protected String getTableName() {
    return "Modules";
  }

  @Override
  protected Module createObject(int id, String value) {
    return new Module(id, value);
  }
}
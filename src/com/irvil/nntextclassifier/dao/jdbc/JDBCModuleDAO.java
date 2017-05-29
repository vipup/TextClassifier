package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;

public class JDBCModuleDAO extends JDBCCharacteristicDAO<Characteristic> {
  public JDBCModuleDAO(JDBCConnector connector) {
    super(connector);
  }

  @Override
  protected String getTableName() {
    return "Modules";
  }

  @Override
  protected Characteristic createObject(int id, String value) {
    return new Characteristic(id, value);
  }
}
package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Characteristic;

public class JDBCHandlerDAO extends JDBCCharacteristicDAO<Characteristic> {
  public JDBCHandlerDAO(JDBCConnector connector) {
    super(connector);
  }

  @Override
  protected String getTableName() {
    return "Handlers";
  }

  @Override
  protected Characteristic createObject(int id, String value) {
    return new Characteristic(id, value);
  }
}

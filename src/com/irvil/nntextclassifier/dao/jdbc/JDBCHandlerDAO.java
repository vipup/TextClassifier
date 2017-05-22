package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Handler;

public class JDBCHandlerDAO extends JDBCCatalogDAO<Handler> {
  public JDBCHandlerDAO(JDBCConnector connector) {
    super(connector);
  }

  @Override
  protected String getTableName() {
    return "Handlers";
  }

  @Override
  protected Handler createObject(int id, String value) {
    return new Handler(id, value);
  }
}

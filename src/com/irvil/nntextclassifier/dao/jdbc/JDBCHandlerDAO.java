package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Handler;

public class JDBCHandlerDAO extends JDBCCatalogDAO<Handler> {
  @Override
  protected String getTableName() {
    return "Handlers";
  }

  @Override
  protected Handler createObject(int id, String value) {
    return new Handler(id, value);
  }
}

package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Module;

public class JDBCModuleDAO extends JDBCCatalogDAO<Module> {
  @Override
  protected String getTableName() {
    return "Modules";
  }

  @Override
  protected Module createObject(int id, String value) {
    return new Module(id, value);
  }
}
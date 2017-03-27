package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.jdbc.JDBCModuleDAO;

public class Module extends Catalog {
  public Module(int id, String value) {
    super(id, value, new JDBCModuleDAO());
  }
}
package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.jdbc.JDBCCategoryDAO;

public class Category extends Catalog {
  public Category(int id, String value) {
    super(id, value, new JDBCCategoryDAO());
  }
}
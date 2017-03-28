package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.model.Category;

public class JDBCCategoryDAO extends JDBCCatalogDAO<Category> {
  @Override
  protected String getTableName() {
    return "Categories";
  }

  @Override
  protected Category createObject(int id, String value) {
    return new Category(id, value);
  }
}
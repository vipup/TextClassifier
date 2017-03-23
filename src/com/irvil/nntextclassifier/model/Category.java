package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.jdbc.JDBCCategoryDAO;

public class Category {
  private int id;
  private String value;

  public Category(int id, String value) {
    this.id = id;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public double[] asVector() {
    double[] vector = new double[new JDBCCategoryDAO().getCount()];
    vector[id - 1] = 1;

    return vector;
  }

  @Override
  public String toString() {
    return value;
  }
}
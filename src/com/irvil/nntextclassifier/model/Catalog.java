package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.GenericDAO;

// todo: rename to Characteristic and delete Category, Module...etc
public abstract class Catalog {
  private final int id;
  private final String value;
  private final GenericDAO dao;

  protected Catalog(int id, String value, GenericDAO dao) {
    this.id = id;
    this.value = value;
    this.dao = dao;
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  // example:
  // count = 5; id = 4;
  // vector = {0, 0, 0, 1, 0}
  public double[] asVector() {
    double[] vector = new double[dao.getCount()];
    vector[id - 1] = 1;

    return vector;
  }
}
package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.GenericDAO;

public abstract class Catalog {
  private int id;
  private String value;
  private GenericDAO dao;

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

  public double[] asVector() {
    double[] vector = new double[dao.getCount()];
    vector[id - 1] = 1;

    return vector;
  }

  @Override
  public String toString() {
    return value;
  }
}
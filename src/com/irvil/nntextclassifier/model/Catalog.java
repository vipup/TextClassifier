package com.irvil.nntextclassifier.model;

// todo: rename to Characteristic and delete Category, Module...etc
public abstract class Catalog {
  private final int id;
  private final String value;

  protected Catalog(int id, String value) {
    this.id = id;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }
}
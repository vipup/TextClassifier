package com.irvil.nntextclassifier.model;

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

  @Override
  public String toString() {
    return value;
  }
}
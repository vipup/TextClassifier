package com.irvil.nntextclassifier.model;

public class Characteristic {
  private final int id;
  private final String value;

  public Characteristic(int id, String value) {
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
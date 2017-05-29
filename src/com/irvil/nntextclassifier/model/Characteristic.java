package com.irvil.nntextclassifier.model;

public class Characteristic {
  private final String name;
  private final int id;
  private final String value;

  public Characteristic(String name, int id, String value) {
    this.name = name;
    this.id = id;
    this.value = value;
  }

  public String getName() {
    return name;
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
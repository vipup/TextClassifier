package com.irvil.nntextclassifier.model;

public class CharacteristicValue {
  private int id;
  private String value;

  public CharacteristicValue(int id, String value) {
    this.id = id;
    this.value = value;
  }

  public CharacteristicValue(String value) {
    this(0, value);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }
}
package com.irvil.nntextclassifier.model;

import java.util.List;

public class Characteristic {
  int id;
  private String name;
  private List<CharacteristicValue> possibleValues;

  public Characteristic(int id, String name, List<CharacteristicValue> possibleValues) {
    this.id = id;
    this.name = name;
    this.possibleValues = possibleValues;
  }

  public Characteristic(int id, String name) {
    this(id, name, null);
  }

  public Characteristic(String name, List<CharacteristicValue> possibleValues) {
    this(0, name, possibleValues);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public List<CharacteristicValue> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(List<CharacteristicValue> possibleValues) {
    this.possibleValues = possibleValues;
  }
}
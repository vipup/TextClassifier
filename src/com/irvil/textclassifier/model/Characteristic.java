package com.irvil.textclassifier.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class Characteristic {
  private final String name;
  private int id;
  private Set<CharacteristicValue> possibleValues;

  private Characteristic(int id, String name, Set<CharacteristicValue> possibleValues) {
    this.id = id;
    this.name = name;
    this.possibleValues = possibleValues;
  }

  public Characteristic(int id, String name) {
    this(id, name, new LinkedHashSet<>());
  }

  public Characteristic(String name, Set<CharacteristicValue> possibleValues) {
    this(0, name, possibleValues);
  }

  public Characteristic(String name) {
    this(0, name, new LinkedHashSet<>());
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

  public Set<CharacteristicValue> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(Set<CharacteristicValue> possibleValues) {
    this.possibleValues = possibleValues;
  }

  public void addPossibleValue(CharacteristicValue value) {
    possibleValues.add(value);
  }

  @Override
  public boolean equals(Object o) {
    return ((o instanceof Characteristic) && (this.name.equals(((Characteristic) o).getName())));
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
package com.irvil.nntextclassifier.model;

public class Handler {
  private int id;
  private String value;

  public Handler(int id, String value) {
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

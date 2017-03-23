package com.irvil.nntextclassifier.model;

public class VocabularyWord {
  private int id;
  private String value;

  public VocabularyWord(int id, String value) {
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
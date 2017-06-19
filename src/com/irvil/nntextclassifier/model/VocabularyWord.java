package com.irvil.nntextclassifier.model;

public class VocabularyWord {
  private final int id;
  private final String value;

  public VocabularyWord(int id, String value) {
    this.id = id;
    this.value = value;
  }

  public VocabularyWord(String value) {
    this(0, value);
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return ((o instanceof VocabularyWord) && (this.value.equals(((VocabularyWord) o).getValue())));
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }
}
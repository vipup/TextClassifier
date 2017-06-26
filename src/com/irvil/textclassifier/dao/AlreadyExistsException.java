package com.irvil.textclassifier.dao;

public class AlreadyExistsException extends Exception {
  public AlreadyExistsException(String s) {
    super(s);
  }
}
package com.irvil.nntextclassifier.dao;

import java.util.List;

public interface CharacteristicDAO<T> {
  int getCount();

  List<T> getAll();

  T findByID(int id);

  T findByValue(String value);

  void add(T object);
}
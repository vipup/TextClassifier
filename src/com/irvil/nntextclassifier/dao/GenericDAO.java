package com.irvil.nntextclassifier.dao;

public interface GenericDAO<T> {
  int getCount();

  T findByID(int id);

  T findByVector(double[] vector);

  T findByValue(String value);

  void add(T object);
}
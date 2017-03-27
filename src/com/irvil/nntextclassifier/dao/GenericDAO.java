package com.irvil.nntextclassifier.dao;

public interface GenericDAO<T> {
  int getCount();

  T findByID(int id);

  void add(T object);
}
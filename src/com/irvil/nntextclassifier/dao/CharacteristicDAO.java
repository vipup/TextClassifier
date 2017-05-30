package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;

import java.util.List;

public interface CharacteristicDAO {
  List<Characteristic> getAll();

  void add(Characteristic characteristic);
}
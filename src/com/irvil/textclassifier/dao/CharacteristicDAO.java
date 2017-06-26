package com.irvil.textclassifier.dao;

import com.irvil.textclassifier.model.Characteristic;

import java.util.List;

public interface CharacteristicDAO {
  List<Characteristic> getAllCharacteristics();

  Characteristic addCharacteristic(Characteristic characteristic) throws AlreadyExistsException, EmptyRecordException;
}
package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;

import java.util.List;

public interface CharacteristicDAO {
  List<Characteristic> getAllCharacteristics();

  Characteristic addCharacteristic(Characteristic characteristic) throws AlreadyExistsException, EmptyRecordException;
}
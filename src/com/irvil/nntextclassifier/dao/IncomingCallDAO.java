package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.util.List;

public interface IncomingCallDAO {
  List<IncomingCall> getAll();

  List<Characteristic> getUniqueValueOfCharacteristic(String characteristicName);
}
package com.irvil.nntextclassifier.model;

import java.util.Map;

public class IncomingCall {
  private final String text;
  private final Map<Characteristic, CharacteristicValue> characteristics;

  public IncomingCall(String text, Map<Characteristic, CharacteristicValue> characteristics) {
    this.text = text;
    this.characteristics = characteristics;
  }

  public IncomingCall(String text) {
    this(text, null);
  }

  public String getText() {
    return text;
  }

  public Map<Characteristic, CharacteristicValue> getCharacteristics() {
    return characteristics;
  }

  public CharacteristicValue getCharacteristicValue(Characteristic characteristic) {
    return characteristics.get(characteristic);
  }
}
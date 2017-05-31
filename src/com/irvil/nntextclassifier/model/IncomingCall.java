package com.irvil.nntextclassifier.model;

import java.util.Map;

public class IncomingCall {
  private final String text;
  private final Map<String, CharacteristicValue> characteristics;

  public IncomingCall(String text, Map<String, CharacteristicValue> characteristics) {
    this.text = text;
    this.characteristics = characteristics;
  }

  public IncomingCall(String text) {
    this(text, null);
  }

  public String getText() {
    return text;
  }

  public Map<String, CharacteristicValue> getCharacteristics() {
    return characteristics;
  }

  public CharacteristicValue getCharacteristic(String name) {
    return characteristics.get(name);
  }
}
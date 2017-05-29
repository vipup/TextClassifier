package com.irvil.nntextclassifier.model;

import java.util.Map;

public class IncomingCall {
  private final String text;
  //todo: change to Set
  private final Map<String, Characteristic> characteristics;

  public IncomingCall(String text, Map<String, Characteristic> characteristics) {
    this.text = text;
    this.characteristics = characteristics;
  }

  public IncomingCall(String text) {
    this(text, null);
  }

  public String getText() {
    return text;
  }

  public Characteristic getCharacteristic(String name) {
    return characteristics.get(name);
  }
}
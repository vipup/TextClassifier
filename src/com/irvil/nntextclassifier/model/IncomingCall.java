package com.irvil.nntextclassifier.model;

import java.util.List;

public class IncomingCall {
  private final String text;
  //todo: change to Set
  private final List<Characteristic> characteristics;

  public IncomingCall(String text, List<Characteristic> characteristics) {
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
    for (Characteristic c : characteristics) {
      if (c.getName().equals(name)) {
        return c;
      }
    }

    return null;
  }
}
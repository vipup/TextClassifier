package com.irvil.nntextclassifier.observer;

public interface Observable {
  void addObserver(Observer o);

  void removeObserver(Observer o);

  void notifyObservers(String text);
}
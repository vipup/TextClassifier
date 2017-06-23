package com.irvil.nntextclassifier.dao;

import java.io.File;

public interface StorageCreator {
  default void createStorageFolder(String path) {
    new File(path).mkdir();
  }

  void createStorage();

  void clearStorage();
}
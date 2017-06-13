package com.irvil.nntextclassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static Config instance;
  private final Properties properties = new Properties();

  private Config() {
    // read config file
    try (InputStream inputStream = new FileInputStream(new File("./config/config.ini"))) {
      properties.load(inputStream);
    } catch (IOException ignored) {

    }
  }

  static Config getInstance() {
    // create only one object - Singleton pattern
    if (instance == null) {
      instance = new Config();
    }

    return instance;
  }

  boolean isLoaded() {
    return properties.size() > 0;
  }

  String getDbPath() {
    return getProperty("db_path");
  }

  String getDaoType() {
    return getProperty("dao_type");
  }

  String getDBMSType() {
    return getProperty("dbms_type");
  }

  String getSQLiteDbFileName() {
    return getProperty("sqlite_db_filename");
  }

  private String getProperty(String property) {
    return properties.getProperty(property) != null ? properties.getProperty(property) : "";
  }
}
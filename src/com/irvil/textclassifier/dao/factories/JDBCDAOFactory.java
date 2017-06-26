package com.irvil.textclassifier.dao.factories;

import com.irvil.textclassifier.dao.CharacteristicDAO;
import com.irvil.textclassifier.dao.ClassifiableTextDAO;
import com.irvil.textclassifier.dao.StorageCreator;
import com.irvil.textclassifier.dao.VocabularyWordDAO;
import com.irvil.textclassifier.dao.jdbc.JDBCCharacteristicDAO;
import com.irvil.textclassifier.dao.jdbc.JDBCClassifiableTextDAO;
import com.irvil.textclassifier.dao.jdbc.JDBCDBCreator;
import com.irvil.textclassifier.dao.jdbc.JDBCVocabularyWordDAO;
import com.irvil.textclassifier.dao.jdbc.connectors.JDBCConnector;

public class JDBCDAOFactory implements DAOFactory {
  private JDBCConnector connector;

  JDBCDAOFactory(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public ClassifiableTextDAO classifiableTextDAO() {
    return new JDBCClassifiableTextDAO(connector);
  }

  @Override
  public CharacteristicDAO characteristicDAO() {
    return new JDBCCharacteristicDAO(connector);
  }

  @Override
  public VocabularyWordDAO vocabularyWordDAO() {
    return new JDBCVocabularyWordDAO(connector);
  }

  @Override
  public StorageCreator storageCreator() {
    return new JDBCDBCreator(connector);
  }
}
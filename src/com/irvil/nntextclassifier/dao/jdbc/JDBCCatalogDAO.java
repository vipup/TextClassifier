package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CatalogDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.Catalog;

abstract class JDBCCatalogDAO<T extends Catalog> extends JDBCGenericDAO<T> implements CatalogDAO<T> {
  JDBCCatalogDAO(JDBCConnector connector) {
    super(connector);
  }

  @Override
  public T findByVector(double[] vector) {
    if (vector != null && vector.length > 0) {
      return findByID(getIndexOfMaxValue(vector) + 1);
    }

    return null;
  }

  private int getIndexOfMaxValue(double[] vector) {
    int indexOfMaxValue = 0;
    double maxValue = vector[0];

    for (int i = 1; i < vector.length; i++) {
      if (vector[i] > maxValue) {
        maxValue = vector[i];
        indexOfMaxValue = i;
      }
    }

    return indexOfMaxValue;
  }
}
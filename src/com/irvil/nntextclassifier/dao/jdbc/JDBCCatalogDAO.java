package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CatalogDAO;
import com.irvil.nntextclassifier.model.Catalog;

public abstract class JDBCCatalogDAO<T extends Catalog> extends JDBCGenericDAO<T> implements CatalogDAO<T> {
  @Override
  public T findByVector(double[] vector) {
    return findByID(getIndexOfMaxValue(vector) + 1);
  }

  private int getIndexOfMaxValue(double[] vector) {
    int maxIndex = 0;
    double maxValue = vector[0];

    for (int i = 1; i < vector.length; i++) {
      if (vector[i] > maxValue) {
        maxValue = vector[i];
        maxIndex = i;
      }
    }

    return maxIndex;
  }
}
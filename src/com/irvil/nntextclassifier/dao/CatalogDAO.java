package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Catalog;

public interface CatalogDAO<T extends Catalog> extends GenericDAO<T> {
  T findByVector(double[] vector);
}

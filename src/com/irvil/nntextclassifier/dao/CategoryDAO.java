package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Category;

public interface CategoryDAO extends GenericDAO<Category> {
  Category findByVector(double[] vector);
}

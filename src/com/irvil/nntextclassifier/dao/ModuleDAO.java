package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Module;

public interface ModuleDAO extends GenericDAO<Module> {
  Module findByVector(double[] vector);
}

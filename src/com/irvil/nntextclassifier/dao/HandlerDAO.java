package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Handler;

public interface HandlerDAO extends GenericDAO<Handler> {
  Handler findByVector(double[] vector);
}

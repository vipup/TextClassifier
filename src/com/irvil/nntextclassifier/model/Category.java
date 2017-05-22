package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.DAOFactory;

public class Category extends Catalog {
  public Category(int id, String value) {
    super(id, value, DAOFactory.categoryDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }
}
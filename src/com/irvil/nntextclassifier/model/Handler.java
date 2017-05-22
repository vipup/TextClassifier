package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.DAOFactory;

public class Handler extends Catalog {
  public Handler(int id, String value) {
    super(id, value, DAOFactory.handlerDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }
}

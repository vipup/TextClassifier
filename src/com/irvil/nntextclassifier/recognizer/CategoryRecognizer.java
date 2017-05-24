package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class CategoryRecognizer extends Recognizer {
  public CategoryRecognizer() {
    super(DAOFactory.categoryDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }

  public CategoryRecognizer(File file) {
    super(file, DAOFactory.categoryDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }

  @Override
  protected int getCatalogId(IncomingCall incomingCall) {
    return incomingCall.getCategory().getId();
  }

  @Override
  public String toString() {
    return "CategoryRecognizer";
  }
}
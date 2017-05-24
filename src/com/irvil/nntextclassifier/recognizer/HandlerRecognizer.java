package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class HandlerRecognizer extends Recognizer {
  public HandlerRecognizer() {
    super(DAOFactory.handlerDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }

  public HandlerRecognizer(File file) {
    super(file, DAOFactory.handlerDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }

  @Override
  protected int getCatalogId(IncomingCall incomingCall) {
    return incomingCall.getHandler().getId();
  }

  @Override
  public String toString() {
    return "HandlerRecognizer";
  }
}
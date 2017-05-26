package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class HandlerRecognizer extends Recognizer {
  public HandlerRecognizer(DAOFactory daoFactory) {
    super(daoFactory.handlerDAO(), daoFactory);
  }

  public HandlerRecognizer(File file, DAOFactory daoFactory) {
    super(file, daoFactory.handlerDAO(), daoFactory);
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
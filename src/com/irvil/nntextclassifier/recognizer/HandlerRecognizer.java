package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class HandlerRecognizer extends Recognizer {
  public HandlerRecognizer() {
    super(DAOFactory.handlerDAO("jdbc", "SQLite"));
  }

  public HandlerRecognizer(File file) {
    super(file, DAOFactory.handlerDAO("jdbc", "SQLite"));
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getHandler().asVector();
  }

  @Override
  public String toString() {
    return "HandlerRecognizer";
  }
}
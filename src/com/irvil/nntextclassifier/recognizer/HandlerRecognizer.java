package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.jdbc.JDBCHandlerDAO;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class HandlerRecognizer extends Recognizer {
  public HandlerRecognizer() {
    super(new JDBCHandlerDAO());
  }

  public HandlerRecognizer(File file) {
    super(file, new JDBCHandlerDAO());
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
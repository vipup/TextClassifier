package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.HandlerDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCHandlerDAO;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;

// todo: check in translate
public class HandlerRecognizer extends Recognizer<Handler> {
  private HandlerDAO handlerDAO;

  @Override
  protected int getOutputLayerSize() {
    initializeDAO();
    return handlerDAO.getCount();
  }

  @Override
  protected Handler convertVectorToValue(double[] output) {
    initializeDAO();
    return handlerDAO.findByVector(output);
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getHandler().asVector();
  }

  private void initializeDAO() {
    if (handlerDAO == null) {
      handlerDAO = new JDBCHandlerDAO();
    }
  }
}
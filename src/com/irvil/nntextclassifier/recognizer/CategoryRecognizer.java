package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class CategoryRecognizer extends Recognizer {
  public CategoryRecognizer() {
    super(DAOFactory.categoryDAO("jdbc"));
  }

  public CategoryRecognizer(File file) {
    super(file, DAOFactory.categoryDAO("jdbc"));
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getCategory().asVector();
  }

  @Override
  public String toString() {
    return "CategoryRecognizer";
  }
}
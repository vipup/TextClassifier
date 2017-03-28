package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.jdbc.JDBCCategoryDAO;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class CategoryRecognizer extends Recognizer {
  public CategoryRecognizer() {
    super(new JDBCCategoryDAO());
  }

  public CategoryRecognizer(File file) {
    super(file, new JDBCCategoryDAO());
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
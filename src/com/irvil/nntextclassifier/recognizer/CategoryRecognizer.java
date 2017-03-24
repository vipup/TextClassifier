package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.CategoryDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCCategoryDAO;
import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.IncomingCall;

// todo: check in translate
public class CategoryRecognizer extends Recognizer<Category> {
  private CategoryDAO categoryDAO;

  @Override
  protected int getOutputLayerSize() {
    initializeDAO();
    return categoryDAO.getCount();
  }

  @Override
  protected Category convertVectorToValue(double[] output) {
    initializeDAO();
    return categoryDAO.findByVector(output);
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getCategory().asVector();
  }

  private void initializeDAO() {
    if (categoryDAO == null) {
      categoryDAO = new JDBCCategoryDAO();
    }
  }

  @Override
  public String toString() {
    return "CategoryRecognizer";
  }
}
package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.ModuleDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCModuleDAO;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;

// todo: check in translate
public class ModuleRecognizer extends Recognizer<Module> {
  private ModuleDAO moduleDAO;

  @Override
  protected int getOutputLayerSize() {
    initializeDAO();
    return moduleDAO.getCount();
  }

  @Override
  protected Module convertVectorToValue(double[] output) {
    initializeDAO();
    return moduleDAO.findByVector(output);
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getModule().asVector();
  }

  private void initializeDAO() {
    if (moduleDAO == null) {
      moduleDAO = new JDBCModuleDAO();
    }
  }

  @Override
  public String toString() {
    return "ModuleRecognizer";
  }
}
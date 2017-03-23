package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.ModuleDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCModuleDAO;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;

import java.io.File;

// todo: check in translate
public class ModuleRecognizer extends Recognizer<Module> {
  private ModuleDAO moduleDAO;

  public ModuleRecognizer() {
    super();
    this.moduleDAO = new JDBCModuleDAO();
    this.outputLayerSize = getOutputLayerSize();
  }

  public ModuleRecognizer(File trainedNetwork) {
    super(trainedNetwork);
    this.moduleDAO = new JDBCModuleDAO();
    this.outputLayerSize = getOutputLayerSize();
  }

  private int getOutputLayerSize() {
    return moduleDAO.getCount();
  }

  @Override
  protected Module convertVectorToValue(double[] output) {
    return moduleDAO.findByVector(output);
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getModule().asVector();
  }
}
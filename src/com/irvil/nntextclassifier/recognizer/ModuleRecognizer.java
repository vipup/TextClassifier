package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.jdbc.JDBCModuleDAO;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class ModuleRecognizer extends Recognizer {
  public ModuleRecognizer() {
    super(new JDBCModuleDAO());
  }

  public ModuleRecognizer(File file) {
    super(file, new JDBCModuleDAO());
  }

  @Override
  protected double[] getCatalogValueVector(IncomingCall incomingCall) {
    return incomingCall.getModule().asVector();
  }

  @Override
  public String toString() {
    return "ModuleRecognizer";
  }
}
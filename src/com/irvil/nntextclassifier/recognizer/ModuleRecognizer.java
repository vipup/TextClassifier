package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class ModuleRecognizer extends Recognizer {
  public ModuleRecognizer() {
    super(DAOFactory.moduleDAO("jdbc", "SQLite"));
  }

  public ModuleRecognizer(File file) {
    super(file, DAOFactory.moduleDAO("jdbc", "SQLite"));
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
package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class ModuleRecognizer extends Recognizer {
  public ModuleRecognizer() {
    super(DAOFactory.moduleDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
  }

  public ModuleRecognizer(File file) {
    super(file, DAOFactory.moduleDAO(Config.getInstance().getDaoType(), Config.getInstance().getDBMSType()));
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
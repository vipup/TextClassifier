package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;

import java.io.File;

public class ModuleRecognizer extends Recognizer {
  public ModuleRecognizer(DAOFactory daoFactory) {
    super(daoFactory.moduleDAO().getAll(), daoFactory.vocabularyWordDAO().getAll());
  }

  public ModuleRecognizer(File file, DAOFactory daoFactory) {
    super(file, daoFactory.moduleDAO().getAll(), daoFactory.vocabularyWordDAO().getAll());
  }

  @Override
  protected int getCatalogId(IncomingCall incomingCall) {
    return incomingCall.getModule().getId();
  }

  @Override
  public String toString() {
    return "ModuleRecognizer";
  }
}
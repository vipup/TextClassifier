package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;

import java.io.File;

public class ModuleRecognizer extends Recognizer {
  public ModuleRecognizer(DAOFactory daoFactory) {
    super(daoFactory.moduleDAO().getAll(), daoFactory.vocabularyWordDAO().getAll(), new FilteredUnigram());
  }

  public ModuleRecognizer(File file, DAOFactory daoFactory) {
    super(file, daoFactory.moduleDAO().getAll(), daoFactory.vocabularyWordDAO().getAll(), new FilteredUnigram());
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
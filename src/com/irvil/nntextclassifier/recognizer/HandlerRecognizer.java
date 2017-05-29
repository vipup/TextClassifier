package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;

import java.io.File;

public class HandlerRecognizer extends Recognizer {
  public HandlerRecognizer(DAOFactory daoFactory) {
    super(daoFactory.handlerDAO().getAll(), daoFactory.vocabularyWordDAO().getAll(), new FilteredUnigram());
  }

  public HandlerRecognizer(File file, DAOFactory daoFactory) {
    super(file, daoFactory.handlerDAO().getAll(), daoFactory.vocabularyWordDAO().getAll(), new FilteredUnigram());
  }

  @Override
  protected int getCatalogId(IncomingCall incomingCall) {
    return incomingCall.getHandler().getId();
  }

  @Override
  public String toString() {
    return "HandlerRecognizer";
  }
}
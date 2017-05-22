package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.ngram.NGramStrategy;

import java.util.Set;

public class IncomingCall {
  private final String text;
  private final Module module;
  private final Handler handler;
  private final Category category;

  public IncomingCall(String text, Module module, Handler handler, Category category) {
    this.text = text;
    this.module = module;
    this.handler = handler;
    this.category = category;
  }

  public IncomingCall(String text) {
    this(text, null, null, null);
  }

  public String getText() {
    return text;
  }

  public Module getModule() {
    return module;
  }

  public Handler getHandler() {
    return handler;
  }

  public Category getCategory() {
    return category;
  }

  // todo: use asVector from VocabularyWord
  public double[] getTextAsWordVector(NGramStrategy nGram) {
    VocabularyWordDAO vocabularyWordDAO = DAOFactory.vocabularyWordDAO("jdbc", "SQLite");
    double[] vector = new double[vocabularyWordDAO.getCount()];

    // convert text to nGram
    Set<String> uniqueValues = nGram.getNGram(text);

    // create vector
    for (String word : uniqueValues) {
      VocabularyWord vw = vocabularyWordDAO.findByValue(word);

      if (vw != null) {
        vector[vw.getId() - 1] = 1;
      }
    }

    return vector;
  }

  @Override
  public String toString() {
    return text + " (Module: " + module + ", Category: " + category + ", Handler: " + handler + ")";
  }
}
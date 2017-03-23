package com.irvil.nntextclassifier.model;

import com.irvil.nntextclassifier.ngram.NGramStrategy;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCVocabularyWordDAO;

import java.util.Set;

public class IncomingCall {
  private String text;
  private Module module;

  public IncomingCall(String text, Module module) {
    this.text = text;
    this.module = module;
  }

  public String getText() {
    return text;
  }

  public Module getModule() {
    return module;
  }

  public double[] getTextAsWordVector(NGramStrategy nGram) {
    VocabularyWordDAO vocabularyWordDAO = new JDBCVocabularyWordDAO();
    double[] vector = new double[vocabularyWordDAO.getCount()];
    Set<String> uniqueValues = nGram.getNGram(text);

    for (String word : uniqueValues) {
      VocabularyWord vw = vocabularyWordDAO.findByValue(word);

      if (vw != null) {
        vector[vw.getId() - 1] = 1;
      }
    }

    return vector;
  }
}
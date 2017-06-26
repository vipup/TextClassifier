package com.irvil.textclassifier.dao;

import com.irvil.textclassifier.model.VocabularyWord;

import java.util.List;

public interface VocabularyWordDAO {
  List<VocabularyWord> getAll();

  void addAll(List<VocabularyWord> vocabulary) throws EmptyRecordException, AlreadyExistsException;
}

package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.VocabularyWord;

import java.util.List;

public interface VocabularyWordDAO {
  List<VocabularyWord> getAll();

  void add(VocabularyWord vocabularyWord) throws EmptyRecordException, AlreadyExistsException;
}

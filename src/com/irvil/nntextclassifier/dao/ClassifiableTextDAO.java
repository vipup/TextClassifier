package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.ClassifiableText;

import java.util.List;

public interface ClassifiableTextDAO {
  List<ClassifiableText> getAll();

  void addAll(List<ClassifiableText> classifiableTexts) throws EmptyRecordException, NotExistsException;
}
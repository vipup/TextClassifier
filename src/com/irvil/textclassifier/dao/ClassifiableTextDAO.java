package com.irvil.textclassifier.dao;

import com.irvil.textclassifier.model.ClassifiableText;

import java.util.List;

public interface ClassifiableTextDAO {
  List<ClassifiableText> getAll();

  void addAll(List<ClassifiableText> classifiableTexts) throws EmptyRecordException, NotExistsException;
}
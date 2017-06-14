package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.IncomingCall;

import java.util.List;

public interface IncomingCallDAO {
  List<IncomingCall> getAll();

  void addAll(List<IncomingCall> incomingCalls) throws EmptyRecordException, NotExistsException;
}
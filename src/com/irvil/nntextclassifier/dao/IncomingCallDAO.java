package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.IncomingCall;

import java.util.List;

public interface IncomingCallDAO {
  List<IncomingCall> getAll();

  void add(IncomingCall incomingCall) throws EmptyRecordException, NotExistsException;
}
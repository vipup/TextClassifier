package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;

import java.util.List;

public interface IncomingCallDAO {
  List<IncomingCall> getAll();

  List<Module> getUniqueModules();

  List<Handler> getUniqueHandlers();
}

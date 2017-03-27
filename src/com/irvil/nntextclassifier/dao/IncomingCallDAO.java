package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Category;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;

import java.util.List;

public interface IncomingCallDAO {
  List<IncomingCall> getAll();

  List<Module> getAllModules();

  List<Handler> getAllHandlers();

  List<Category> getAllCategories();
}

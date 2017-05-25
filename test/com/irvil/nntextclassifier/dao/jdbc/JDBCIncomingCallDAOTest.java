package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCIncomingCallDAOTest {
  private IncomingCallDAO incomingCallDAO = new JDBCIncomingCallDAO(new SQLiteJDBCTestConnector());

  @Test
  public void getAll() throws Exception {
    List<IncomingCall> incomingCalls = incomingCallDAO.getAll();

    // check size
    assertEquals(incomingCalls.size(), 3);

    // check text
    assertEquals(incomingCalls.get(0).getText(), "test test test test");
    assertEquals(incomingCalls.get(1).getText(), "test1 test1 test1 test1");
    assertEquals(incomingCalls.get(2).getText(), "test1 test1 test1 test1");

    // check modules
    assertEquals(incomingCalls.get(0).getModule().getValue(), "PM");
    assertEquals(incomingCalls.get(1).getModule().getValue(), "MM");
    assertEquals(incomingCalls.get(2).getModule().getValue(), "MM");

    // check handlers
    assertEquals(incomingCalls.get(0).getHandler().getValue(), "User 1");
    assertEquals(incomingCalls.get(1).getHandler().getValue(), "User 2");
    assertEquals(incomingCalls.get(2).getHandler().getValue(), "User 2");
  }

  @Test
  public void getUniqueModules() throws Exception {
    List<Module> modules = incomingCallDAO.getUniqueModules();

    // check size
    assertEquals(modules.size(), 2);

    // check modules
    assertEquals(modules.get(0).getValue(), "PM");
    assertEquals(modules.get(1).getValue(), "MM");
  }

  @Test
  public void getUniqueHandlers() throws Exception {
    List<Handler> handlers = incomingCallDAO.getUniqueHandlers();

    // check size
    assertEquals(handlers.size(), 2);

    // check modules
    assertEquals(handlers.get(0).getValue(), "User 1");
    assertEquals(handlers.get(1).getValue(), "User 2");
  }
}
package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.IncomingCallDAO;
import com.irvil.nntextclassifier.model.Handler;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.Module;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCIncomingCallDAOTest {
  private IncomingCallDAO incomingCallDAO;

  @Before
  public void initializeTable() throws Exception {
    incomingCallDAO = new JDBCIncomingCallDAO(new JDBCSQLiteTestConnector());

    String tableName = "IncomingCalls";
    JDBCDatabaseUtilities.cleanTable(tableName);
    JDBCDatabaseUtilities.insertIncomingCall(new IncomingCall("text text", new Module(0, "PM"), new Handler(0, "User 1")));
    JDBCDatabaseUtilities.insertIncomingCall(new IncomingCall("text1 text1", new Module(0, "MM"), new Handler(0, "User 2")));
    JDBCDatabaseUtilities.insertIncomingCall(new IncomingCall("text1 text1", new Module(0, "MM"), new Handler(0, "User 2")));

    tableName = "Handlers";
    JDBCDatabaseUtilities.cleanTable(tableName);
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Handler(1, "User 1"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Handler(2, "User 2"));

    tableName = "Modules";
    JDBCDatabaseUtilities.cleanTable(tableName);
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Module(1, "PM"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Module(2, "MM"));
  }

  @Test
  public void getAll() throws Exception {
    List<IncomingCall> incomingCalls = incomingCallDAO.getAll();

    // check size
    assertEquals(incomingCalls.size(), 3);

    // check text
    assertEquals(incomingCalls.get(0).getText(), "text text");
    assertEquals(incomingCalls.get(1).getText(), "text1 text1");
    assertEquals(incomingCalls.get(2).getText(), "text1 text1");

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
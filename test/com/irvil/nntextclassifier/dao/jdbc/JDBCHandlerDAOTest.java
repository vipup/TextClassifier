package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.Handler;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCHandlerDAOTest {
  private GenericDAO<Catalog> handlerDAO;

  @Before
  public void initializeTable() throws Exception {
    handlerDAO = new JDBCHandlerDAO(new JDBCSQLiteTestConnector());

    String tableName = "Handlers";
    JDBCDatabaseUtilities.cleanTable(tableName);
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Handler(1, "User 1"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Handler(2, "User 2"));
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(handlerDAO.getCount(), 2);
  }

  @Test
  public void getAll() throws Exception {
    List<Catalog> characteristics = handlerDAO.getAll();

    assertEquals(characteristics.get(0).getId(), 1);
    assertEquals(characteristics.get(1).getId(), 2);

    assertEquals(characteristics.get(0).getValue(), "User 1");
    assertEquals(characteristics.get(1).getValue(), "User 2");
  }

  @Test
  public void findByID() throws Exception {
    Catalog handler = handlerDAO.findByID(1);
    assertEquals(handler.getValue(), "User 1");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    Catalog handler = handlerDAO.findByID(10);
    assertEquals(handler, null);
  }

  @Test
  public void findByValue() throws Exception {
    Catalog handler = handlerDAO.findByValue("User 2");
    assertEquals(handler.getId(), 2);
  }

  @Test
  public void findByValueNonexistent() throws Exception {
    Catalog handler = handlerDAO.findByValue("testtesttest");
    assertEquals(handler, null);
  }

  @Test
  public void findByValueNull() throws Exception {
    Catalog handler = handlerDAO.findByValue(null);
    assertEquals(handler, null);
  }

  @Test
  public void add() throws Exception {
    int beforeCount = handlerDAO.getCount();

    String value = "Test add()";
    handlerDAO.add(new Handler(0, value));

    assertEquals(handlerDAO.getCount(), beforeCount + 1);
    assertEquals(handlerDAO.findByValue(value).getValue(), value);
  }
}
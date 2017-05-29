package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.Module;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCModuleDAOTest {
  private GenericDAO<Catalog> moduleDAO;

  @Before
  public void initializeTable() throws Exception {
    moduleDAO = new JDBCModuleDAO(new JDBCSQLiteTestConnector());

    String tableName = "Modules";
    JDBCDatabaseUtilities.cleanTable(tableName);
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Module(1, "PM"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new Module(2, "MM"));
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(moduleDAO.getCount(), 2);
  }

  @Test
  public void getAll() throws Exception {
    List<Catalog> characteristics = moduleDAO.getAll();

    assertEquals(characteristics.get(0).getId(), 1);
    assertEquals(characteristics.get(1).getId(), 2);

    assertEquals(characteristics.get(0).getValue(), "PM");
    assertEquals(characteristics.get(1).getValue(), "MM");
  }

  @Test
  public void findByID() throws Exception {
    Catalog module = moduleDAO.findByID(1);
    assertEquals(module.getValue(), "PM");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    Catalog module = moduleDAO.findByID(10);
    assertEquals(module, null);
  }

  @Test
  public void findByValue() throws Exception {
    Catalog module = moduleDAO.findByValue("PM");
    assertEquals(module.getId(), 1);
  }

  @Test
  public void findByValueNonexistent() throws Exception {
    Catalog module = moduleDAO.findByValue("testtesttest");
    assertEquals(module, null);
  }

  @Test
  public void findByValueNull() throws Exception {
    Catalog module = moduleDAO.findByValue(null);
    assertEquals(module, null);
  }

  @Test
  public void add() throws Exception {
    int beforeCount = moduleDAO.getCount();

    String value = "Test add()";
    moduleDAO.add(new Module(0, value));

    assertEquals(moduleDAO.getCount(), beforeCount + 1);
    assertEquals(moduleDAO.findByValue(value).getValue(), value);
  }
}
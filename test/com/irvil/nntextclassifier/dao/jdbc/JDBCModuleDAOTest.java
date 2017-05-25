package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.Catalog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JDBCModuleDAOTest {
  private GenericDAO<Catalog> moduleDAO = new JDBCModuleDAO(new SQLiteJDBCTestConnector());

  @Test
  public void getCount() throws Exception {
    assertEquals(moduleDAO.getCount(), 2);
  }

  @Test
  public void findByVector() throws Exception {
    double[] vector = new double[]{0.1, 0.3, 0.2};

    Catalog module = moduleDAO.findByVector(vector);
    assertEquals(module.getValue(), "MM");
  }

  @Test
  public void findByVectorNonexistent() throws Exception {
    double[] vector = new double[]{0.1, 0.2, 0.3};

    Catalog module = moduleDAO.findByVector(vector);
    assertEquals(module, null);
  }

  @Test
  public void findByVectorAllEqual() throws Exception {
    double[] vector = new double[]{0.1, 0.1, 0.1};

    Catalog module = moduleDAO.findByVector(vector);
    assertEquals(module.getValue(), "PM");
  }

  @Test
  public void findByVectorEmptyVector() throws Exception {
    double[] vector = new double[0];

    Catalog module = moduleDAO.findByVector(vector);
    assertEquals(module, null);
  }

  @Test
  public void findByVectorNullVector() throws Exception {
    Catalog module = moduleDAO.findByVector(null);
    assertEquals(module, null);
  }

  @Test
  public void findByID() throws Exception {
    Catalog module = (Catalog) moduleDAO.findByID(1);
    assertEquals(module.getValue(), "PM");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    Catalog module = (Catalog) moduleDAO.findByID(10);
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

  // todo: add test
  @Test
  public void add() throws Exception {

  }
}
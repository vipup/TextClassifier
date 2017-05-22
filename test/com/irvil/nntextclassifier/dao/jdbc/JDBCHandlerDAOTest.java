package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CatalogDAO;
import com.irvil.nntextclassifier.model.Catalog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JDBCHandlerDAOTest {
  private CatalogDAO handlerDAO = new JDBCHandlerDAO(new SQLiteJDBCTestConnector());

  @Test
  public void findByVector() throws Exception {
    double[] vector = new double[] {0.1, 0.3, 0.2};

    Catalog handler = handlerDAO.findByVector(vector);
    assertEquals(handler.getValue(), "User 2");
  }

  @Test
  public void findByVectorNonexistent() throws Exception {
    double[] vector = new double[] {0.1, 0.2, 0.3};

    Catalog handler = handlerDAO.findByVector(vector);
    assertEquals(handler, null);
  }

  @Test
  public void findByVectorAllEqual() throws Exception {
    double[] vector = new double[] {0.1, 0.1, 0.1};

    Catalog handler = handlerDAO.findByVector(vector);
    assertEquals(handler.getValue(), "User 1");
  }

  @Test
  public void findByVectorEmptyVector() throws Exception {
    double[] vector = new double[0];

    Catalog handler = handlerDAO.findByVector(vector);
    assertEquals(handler, null);
  }

  @Test
  public void findByVectorNullVector() throws Exception {
    Catalog handler = handlerDAO.findByVector(null);
    assertEquals(handler, null);
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(handlerDAO.getCount(), 2);
  }

  @Test
  public void findByID() throws Exception {
    Catalog handler = (Catalog) handlerDAO.findByID(1);
    assertEquals(handler.getValue(), "User 1");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    Catalog handler = (Catalog) handlerDAO.findByID(10);
    assertEquals(handler, null);
  }

  // todo: add test
  @Test
  public void add() throws Exception {

  }
}
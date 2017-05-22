package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.CatalogDAO;
import com.irvil.nntextclassifier.model.Catalog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JDBCCategoryDAOTest {
  private CatalogDAO categoryDAO = new JDBCCategoryDAO(new SQLiteJDBCTestConnector());

  @Test
  public void findByVector() throws Exception {
    double[] vector = new double[] {0.1, 0.3, 0.2};

    Catalog category = categoryDAO.findByVector(vector);
    assertEquals(category.getValue(), "Category 2");
  }

  @Test
  public void findByVectorNonexistent() throws Exception {
    double[] vector = new double[] {0.1, 0.2, 0.3};

    Catalog category = categoryDAO.findByVector(vector);
    assertEquals(category, null);
  }

  @Test
  public void findByVectorAllEqual() throws Exception {
    double[] vector = new double[] {0.1, 0.1, 0.1};

    Catalog category = categoryDAO.findByVector(vector);
    assertEquals(category.getValue(), "Category 1");
  }

  @Test
  public void findByVectorEmptyVector() throws Exception {
    double[] vector = new double[0];

    Catalog category = categoryDAO.findByVector(vector);
    assertEquals(category, null);
  }

  @Test
  public void findByVectorNullVector() throws Exception {
    Catalog category = categoryDAO.findByVector(null);
    assertEquals(category, null);
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(categoryDAO.getCount(), 2);
  }

  @Test
  public void findByID() throws Exception {
    Catalog category = (Catalog) categoryDAO.findByID(1);
    assertEquals(category.getValue(), "Category 1");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    Catalog category = (Catalog) categoryDAO.findByID(10);
    assertEquals(category, null);
  }

  // todo: add test
  @Test
  public void add() throws Exception {

  }
}
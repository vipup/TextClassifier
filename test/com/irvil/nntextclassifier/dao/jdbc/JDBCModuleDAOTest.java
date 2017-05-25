package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.Module;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class JDBCModuleDAOTest {
  private GenericDAO<Catalog> moduleDAO;

  @Before
  public void initializeTable() throws Exception {
    moduleDAO = new JDBCModuleDAO(new SQLiteJDBCTestConnector());

    cleanTable();
    insert(new Module(1, "PM"));
    insert(new Module(2, "MM"));
  }

  private void cleanTable() throws Exception {
    try (Connection con = new SQLiteJDBCTestConnector().getDBConnection()) {
      Statement statement = con.createStatement();
      statement.executeUpdate("DELETE FROM Modules");
    }
  }

  private void insert(Module module) throws Exception {
    try (Connection con = new SQLiteJDBCTestConnector().getDBConnection()) {
      PreparedStatement statement = con.prepareStatement("INSERT INTO Modules (id, value) VALUES (?, ?)");
      statement.setInt(1, module.getId());
      statement.setString(2, module.getValue());
      statement.executeUpdate();
    }
  }

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
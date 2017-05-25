package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.Handler;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class JDBCHandlerDAOTest {
  private GenericDAO<Catalog> handlerDAO;

  @Before
  public void initializeTable() throws Exception {
    handlerDAO = new JDBCHandlerDAO(new SQLiteJDBCTestConnector());

    cleanTable();
    insert(new Handler(1, "User 1"));
    insert(new Handler(2, "User 2"));
  }

  // todo: move to separate Class
  private void cleanTable() throws Exception {
    try (Connection con = new SQLiteJDBCTestConnector().getDBConnection()) {
      Statement statement = con.createStatement();
      statement.executeUpdate("DELETE FROM Handlers");
    }
  }

  private void insert(Handler handler) throws Exception {
    try (Connection con = new SQLiteJDBCTestConnector().getDBConnection()) {
      PreparedStatement statement = con.prepareStatement("INSERT INTO Handlers (id, value) VALUES (?, ?)");
      statement.setInt(1, handler.getId());
      statement.setString(2, handler.getValue());
      statement.executeUpdate();
    }
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(handlerDAO.getCount(), 2);
  }

  @Test
  public void findByVector() throws Exception {
    double[] vector = new double[]{0.1, 0.3, 0.2};

    Catalog handler = handlerDAO.findByVector(vector);
    assertEquals(handler.getValue(), "User 2");
  }

  @Test
  public void findByVectorNonexistent() throws Exception {
    double[] vector = new double[]{0.1, 0.2, 0.3};

    Catalog handler = handlerDAO.findByVector(vector);
    assertEquals(handler, null);
  }

  @Test
  public void findByVectorAllEqual() throws Exception {
    double[] vector = new double[]{0.1, 0.1, 0.1};

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
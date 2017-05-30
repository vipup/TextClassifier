package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.CharacteristicDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.Characteristic;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCCharacteristicDAOTest {
  private CharacteristicDAO moduleDAO;
  private CharacteristicDAO handlerDAO;
  private CharacteristicDAO categoryDAO;

  @Before
  public void initializeTable() throws Exception {
    Config config = Config.getInstance();
    JDBCConnector jdbcConnector = new JDBCSQLiteConnector(config.getDbPath() + "/test.db");
    moduleDAO = new JDBCCharacteristicDAO("Module", jdbcConnector);
    handlerDAO = new JDBCCharacteristicDAO("Handler", jdbcConnector);
    categoryDAO = new JDBCCharacteristicDAO("Category", jdbcConnector);

    // clear tables
    //

    JDBCDatabaseUtilities.cleanTable(jdbcConnector, "CharacteristicsNames");
    JDBCDatabaseUtilities.cleanTable(jdbcConnector, "CharacteristicsValues");

    // fill Module characteristic
    //

    moduleDAO.add(new Characteristic(0, "PM")); // ok
    moduleDAO.add(new Characteristic(0, "MM")); // ok
    moduleDAO.add(new Characteristic(0, "MM")); // error: already exists
    moduleDAO.add(new Characteristic(0, "")); // error: empty
    moduleDAO.add(null); // error: null

    // fill Handler characteristic
    //

    handlerDAO.add(new Characteristic(0, "User 1")); // ok
    handlerDAO.add(new Characteristic(0, "User 2")); // ok
    handlerDAO.add(new Characteristic(0, "User 3")); // ok
  }

  @Test
  public void getAllModules() throws Exception {
    List<Characteristic> modules = moduleDAO.getAll();

    assertEquals(modules.size(), 2);

    assertEquals(modules.get(0).getId(), 1);
    assertEquals(modules.get(1).getId(), 2);
    assertEquals(modules.get(0).getValue(), "PM");
    assertEquals(modules.get(1).getValue(), "MM");
  }

  @Test
  public void getAllHandlers() throws Exception {
    List<Characteristic> handlers = handlerDAO.getAll();

    assertEquals(handlers.size(), 3);

    assertEquals(handlers.get(0).getId(), 1);
    assertEquals(handlers.get(1).getId(), 2);
    assertEquals(handlers.get(2).getId(), 3);
    assertEquals(handlers.get(0).getValue(), "User 1");
    assertEquals(handlers.get(1).getValue(), "User 2");
    assertEquals(handlers.get(2).getValue(), "User 3");
  }

  @Test
  public void getAllCategories() throws Exception {
    List<Characteristic> categories = categoryDAO.getAll();

    assertEquals(categories.size(), 0);
  }
}
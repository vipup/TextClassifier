package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public abstract class IncomingCallDAOTest {
  protected StorageCreator storageCreator;
  protected CharacteristicDAO characteristicDAO;
  protected IncomingCallDAO incomingCallDAO;
  protected VocabularyWordDAO vocabularyWordDAO;

  @Before
  public void setUp() throws Exception {
    initializeDAO();
    Helper.fillStorageWithTestData(storageCreator, characteristicDAO, incomingCallDAO, vocabularyWordDAO);
  }

  public abstract void initializeDAO();

  @Test
  public void getAll() throws Exception {
    List<IncomingCall> incomingCalls = incomingCallDAO.getAll();

    // check size
    assertEquals(incomingCalls.size(), 3);

    // check text
    //

    assertEquals(incomingCalls.get(0).getText(), "text text");
    assertEquals(incomingCalls.get(1).getText(), "text1 text1");
    assertEquals(incomingCalls.get(2).getText(), "text1 text1");

    // check characteristics
    //

    assertEquals(incomingCalls.get(0).getCharacteristics().size(), 2);
    assertEquals(incomingCalls.get(0).getCharacteristicValue(new Characteristic("Module")).getId(), 1);
    assertEquals(incomingCalls.get(0).getCharacteristicValue(new Characteristic("Module")).getValue(), "PM");
    assertEquals(incomingCalls.get(0).getCharacteristicValue(new Characteristic("Handler")).getId(), 1);
    assertEquals(incomingCalls.get(0).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 1");

    assertEquals(incomingCalls.get(1).getCharacteristics().size(), 2);
    assertEquals(incomingCalls.get(1).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(incomingCalls.get(1).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(incomingCalls.get(1).getCharacteristicValue(new Characteristic("Handler")).getId(), 2);
    assertEquals(incomingCalls.get(1).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 2");

    assertEquals(incomingCalls.get(2).getCharacteristics().size(), 2);
    assertEquals(incomingCalls.get(2).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(incomingCalls.get(2).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(incomingCalls.get(2).getCharacteristicValue(new Characteristic("Handler")).getId(), 2);
    assertEquals(incomingCalls.get(2).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 2");
  }

  @Test(expected = EmptyRecordException.class)
  public void addNullList() throws Exception {
    incomingCallDAO.addAll(null);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmptyList() throws Exception {
    incomingCallDAO.addAll(new ArrayList<>());
  }

  @Test(expected = EmptyRecordException.class)
  public void addNull() throws Exception {
    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(null);
    incomingCallDAO.addAll(incomingCalls);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmpty() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));

    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("", characteristics));
    incomingCallDAO.addAll(incomingCalls);
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicsNull() throws Exception {
    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("text text", null));
    incomingCallDAO.addAll(incomingCalls);
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicsEmpty() throws Exception {
    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("text text", new HashMap<>()));
    incomingCallDAO.addAll(incomingCalls);
  }

  @Test(expected = NotExistsException.class)
  public void addCharacteristicNotExists() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Test"), new CharacteristicValue("User 1"));

    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("text text", characteristics));
    incomingCallDAO.addAll(incomingCalls);
  }

  @Test(expected = NotExistsException.class)
  public void addCharacteristicValueNotExists() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 4"));

    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("text text", characteristics));
    incomingCallDAO.addAll(incomingCalls);
  }

  @Test
  public void add() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));

    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("text2 text2", characteristics));
    incomingCallDAO.addAll(incomingCalls);

    // check record from DB
    //

    List<IncomingCall> incomingCallsFromDB = incomingCallDAO.getAll();

    // check size
    assertEquals(incomingCallsFromDB.size(), 4);

    // check text
    //

    assertEquals(incomingCallsFromDB.get(3).getText(), "text2 text2");

    // check characteristics
    //

    assertEquals(incomingCallsFromDB.get(3).getCharacteristics().size(), 2);
    assertEquals(incomingCallsFromDB.get(3).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(incomingCallsFromDB.get(3).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(incomingCallsFromDB.get(3).getCharacteristicValue(new Characteristic("Handler")).getId(), 1);
    assertEquals(incomingCallsFromDB.get(3).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 1");
  }
}
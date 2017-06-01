package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import org.junit.Before;
import org.junit.Test;

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
  public void addNull() throws Exception {
    incomingCallDAO.add(null);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmpty() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));
    incomingCallDAO.add(new IncomingCall("", characteristics));
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicsNull() throws Exception {
    incomingCallDAO.add(new IncomingCall("text text", null));
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicsEmpty() throws Exception {
    incomingCallDAO.add(new IncomingCall("text text", new HashMap<>()));
  }

  @Test(expected = NotExistsException.class)
  public void addCharacteristicNotExists() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Test"), new CharacteristicValue("User 1"));
    incomingCallDAO.add(new IncomingCall("text text", characteristics));
  }

  @Test(expected = NotExistsException.class)
  public void addCharacteristicValueNotExists() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 4"));
    incomingCallDAO.add(new IncomingCall("text text", characteristics));
  }

  @Test
  public void add() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));
    incomingCallDAO.add(new IncomingCall("text2 text2", characteristics));

    // check record from DB
    //

    List<IncomingCall> incomingCalls = incomingCallDAO.getAll();

    // check size
    assertEquals(incomingCalls.size(), 4);

    // check text
    //

    assertEquals(incomingCalls.get(3).getText(), "text2 text2");

    // check characteristics
    //

    assertEquals(incomingCalls.get(3).getCharacteristics().size(), 2);
    assertEquals(incomingCalls.get(3).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(incomingCalls.get(3).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(incomingCalls.get(3).getCharacteristicValue(new Characteristic("Handler")).getId(), 1);
    assertEquals(incomingCalls.get(3).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 1");
  }
}
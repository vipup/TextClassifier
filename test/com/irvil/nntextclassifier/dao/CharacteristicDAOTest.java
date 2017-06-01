package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class CharacteristicDAOTest {
  protected StorageCreator storageCreator;
  protected CharacteristicDAO characteristicDAO;

  @Before
  public void fillStorage() throws Exception {
    initializeDAO();

    storageCreator.clearStorage();

    // fill Module characteristic
    //

    List<CharacteristicValue> possibleValues = new ArrayList<>();
    possibleValues.add(new CharacteristicValue("PM"));
    possibleValues.add(new CharacteristicValue("MM"));
    characteristicDAO.addCharacteristic(new Characteristic("Module", possibleValues));

    // fill Handler characteristic
    //

    possibleValues = new ArrayList<>();
    possibleValues.add(new CharacteristicValue("User 1"));
    possibleValues.add(new CharacteristicValue("User 2"));
    possibleValues.add(new CharacteristicValue("User 3"));
    characteristicDAO.addCharacteristic(new Characteristic("Handler", possibleValues));
  }

  public abstract void initializeDAO();

  @Test
  public void getAllCharacteristics() throws Exception {
    List<Characteristic> characteristics = characteristicDAO.getAllCharacteristics();

    // check characteristics
    //

    assertEquals(characteristics.size(), 2);

    assertEquals(characteristics.get(0).getId(), 1);
    assertEquals(characteristics.get(0).getName(), "Module");

    assertEquals(characteristics.get(1).getId(), 2);
    assertEquals(characteristics.get(1).getName(), "Handler");

    // check Module possible values
    //

    List<CharacteristicValue> modulePossibleValues = characteristics.get(0).getPossibleValues();

    assertEquals(modulePossibleValues.size(), 2);

    assertEquals(modulePossibleValues.get(0).getId(), 1);
    assertEquals(modulePossibleValues.get(0).getValue(), "PM");

    assertEquals(modulePossibleValues.get(1).getId(), 2);
    assertEquals(modulePossibleValues.get(1).getValue(), "MM");

    // check Handler possible values
    //

    List<CharacteristicValue> handlerPossibleValues = characteristics.get(1).getPossibleValues();

    assertEquals(handlerPossibleValues.size(), 3);

    assertEquals(handlerPossibleValues.get(0).getId(), 1);
    assertEquals(handlerPossibleValues.get(0).getValue(), "User 1");

    assertEquals(handlerPossibleValues.get(1).getId(), 2);
    assertEquals(handlerPossibleValues.get(1).getValue(), "User 2");

    assertEquals(handlerPossibleValues.get(2).getId(), 3);
    assertEquals(handlerPossibleValues.get(2).getValue(), "User 3");
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicNull() throws Exception {
    characteristicDAO.addCharacteristic(null);
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicEmpty() throws Exception {
    List<CharacteristicValue> possibleValues = new ArrayList<>();
    possibleValues.add(new CharacteristicValue("Value 1"));
    characteristicDAO.addCharacteristic(new Characteristic("", possibleValues));
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicNullPossibleValues() throws Exception {
    characteristicDAO.addCharacteristic(new Characteristic("Test", null));
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicEmptyPossibleValues() throws Exception {
    characteristicDAO.addCharacteristic(new Characteristic("Test", new ArrayList<>()));
  }

  @Test(expected = AlreadyExistsException.class)
  public void addCharacteristicExisted() throws Exception {
    List<CharacteristicValue> possibleValues = new ArrayList<>();
    possibleValues.add(new CharacteristicValue("BC"));
    characteristicDAO.addCharacteristic(new Characteristic("Module", possibleValues));
  }

  @Test
  public void addCharacteristic() throws Exception {
    List<CharacteristicValue> possibleValues = new ArrayList<>();
    possibleValues.add(new CharacteristicValue("Value 1"));
    possibleValues.add(new CharacteristicValue(""));
    possibleValues.add(null);
    possibleValues.add(new CharacteristicValue("Value 2"));
    possibleValues.add(new CharacteristicValue("Value 2"));
    Characteristic characteristic = new Characteristic("Test", possibleValues);

    characteristic = characteristicDAO.addCharacteristic(characteristic);

    // check returned object
    //

    assertEquals(characteristic.getId(), 3);
    assertEquals(characteristic.getName(), "Test");
    assertEquals(characteristic.getPossibleValues().size(), 5);
    assertEquals(characteristic.getPossibleValues().get(0).getId(), 1);
    assertEquals(characteristic.getPossibleValues().get(0).getValue(), "Value 1");
    assertEquals(characteristic.getPossibleValues().get(1).getId(), 0);
    assertEquals(characteristic.getPossibleValues().get(1).getValue(), "");
    assertEquals(characteristic.getPossibleValues().get(2), null);
    assertEquals(characteristic.getPossibleValues().get(3).getId(), 2);
    assertEquals(characteristic.getPossibleValues().get(3).getValue(), "Value 2");
    assertEquals(characteristic.getPossibleValues().get(4).getId(), 2);
    assertEquals(characteristic.getPossibleValues().get(4).getValue(), "Value 2");

    // check record from DB
    //

    List<Characteristic> characteristicsFromDb = characteristicDAO.getAllCharacteristics();

    assertEquals(characteristicsFromDb.size(), 3);

    assertEquals(characteristicsFromDb.get(2).getId(), 3);
    assertEquals(characteristicsFromDb.get(2).getName(), "Test");

    // check Test possible values
    //

    List<CharacteristicValue> testPossibleValues = characteristicsFromDb.get(2).getPossibleValues();

    assertEquals(testPossibleValues.size(), 2);

    assertEquals(testPossibleValues.get(0).getId(), 1);
    assertEquals(testPossibleValues.get(0).getValue(), "Value 1");

    assertEquals(testPossibleValues.get(1).getId(), 2);
    assertEquals(testPossibleValues.get(1).getValue(), "Value 2");
  }
}
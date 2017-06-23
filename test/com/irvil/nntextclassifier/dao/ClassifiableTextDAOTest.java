package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.ClassifiableText;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public abstract class ClassifiableTextDAOTest {
  protected StorageCreator storageCreator;
  protected CharacteristicDAO characteristicDAO;
  protected ClassifiableTextDAO classifiableTextDAO;
  protected VocabularyWordDAO vocabularyWordDAO;

  @Before
  public void setUp() throws Exception {
    initializeDAO();
    Helper.fillStorageWithTestData(storageCreator, characteristicDAO, classifiableTextDAO, vocabularyWordDAO);
  }

  protected abstract void initializeDAO();

  @Test
  public void getAll() throws Exception {
    List<ClassifiableText> classifiableTexts = classifiableTextDAO.getAll();

    // check size
    assertEquals(classifiableTexts.size(), 3);

    // check text
    //

    assertEquals(classifiableTexts.get(0).getText(), "text text");
    assertEquals(classifiableTexts.get(1).getText(), "text1 text1");
    assertEquals(classifiableTexts.get(2).getText(), "text1 text1");

    // check characteristics
    //

    assertEquals(classifiableTexts.get(0).getCharacteristics().size(), 2);
    assertEquals(classifiableTexts.get(0).getCharacteristicValue(new Characteristic("Module")).getId(), 1);
    assertEquals(classifiableTexts.get(0).getCharacteristicValue(new Characteristic("Module")).getValue(), "PM");
    assertEquals(classifiableTexts.get(0).getCharacteristicValue(new Characteristic("Handler")).getId(), 1);
    assertEquals(classifiableTexts.get(0).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 1");

    assertEquals(classifiableTexts.get(1).getCharacteristics().size(), 2);
    assertEquals(classifiableTexts.get(1).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(classifiableTexts.get(1).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(classifiableTexts.get(1).getCharacteristicValue(new Characteristic("Handler")).getId(), 2);
    assertEquals(classifiableTexts.get(1).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 2");

    assertEquals(classifiableTexts.get(2).getCharacteristics().size(), 2);
    assertEquals(classifiableTexts.get(2).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(classifiableTexts.get(2).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(classifiableTexts.get(2).getCharacteristicValue(new Characteristic("Handler")).getId(), 2);
    assertEquals(classifiableTexts.get(2).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 2");
  }

  @Test(expected = EmptyRecordException.class)
  public void addNullList() throws Exception {
    classifiableTextDAO.addAll(null);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmptyList() throws Exception {
    classifiableTextDAO.addAll(new ArrayList<>());
  }

  @Test(expected = EmptyRecordException.class)
  public void addNull() throws Exception {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(null);
    classifiableTextDAO.addAll(classifiableTexts);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmpty() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));

    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("", characteristics));
    classifiableTextDAO.addAll(classifiableTexts);
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicsNull() throws Exception {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("text text", null));
    classifiableTextDAO.addAll(classifiableTexts);
  }

  @Test(expected = EmptyRecordException.class)
  public void addCharacteristicsEmpty() throws Exception {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("text text", new HashMap<>()));
    classifiableTextDAO.addAll(classifiableTexts);
  }

  @Test(expected = NotExistsException.class)
  public void addCharacteristicNotExists() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Test"), new CharacteristicValue("User 1"));

    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("text text", characteristics));
    classifiableTextDAO.addAll(classifiableTexts);
  }

  @Test(expected = NotExistsException.class)
  public void addCharacteristicValueNotExists() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 4"));

    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("text text", characteristics));
    classifiableTextDAO.addAll(classifiableTexts);
  }

  @Test
  public void add() throws Exception {
    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));

    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("text2 text2", characteristics));
    classifiableTextDAO.addAll(classifiableTexts);

    // check record from DB
    //

    List<ClassifiableText> classifiableTextsFromDB = classifiableTextDAO.getAll();

    // check size
    assertEquals(classifiableTextsFromDB.size(), 4);

    // check text
    //

    assertEquals(classifiableTextsFromDB.get(3).getText(), "text2 text2");

    // check characteristics
    //

    assertEquals(classifiableTextsFromDB.get(3).getCharacteristics().size(), 2);
    assertEquals(classifiableTextsFromDB.get(3).getCharacteristicValue(new Characteristic("Module")).getId(), 2);
    assertEquals(classifiableTextsFromDB.get(3).getCharacteristicValue(new Characteristic("Module")).getValue(), "MM");
    assertEquals(classifiableTextsFromDB.get(3).getCharacteristicValue(new Characteristic("Handler")).getId(), 1);
    assertEquals(classifiableTextsFromDB.get(3).getCharacteristicValue(new Characteristic("Handler")).getValue(), "User 1");
  }
}
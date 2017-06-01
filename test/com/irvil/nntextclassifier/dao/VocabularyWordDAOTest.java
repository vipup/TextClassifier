package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.VocabularyWord;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class VocabularyWordDAOTest {
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
    List<VocabularyWord> vocabularyWords = vocabularyWordDAO.getAll();

    assertEquals(vocabularyWords.size(), 2);

    assertEquals(vocabularyWords.get(0).getId(), 1);
    assertEquals(vocabularyWords.get(0).getValue(), "Test 1");

    assertEquals(vocabularyWords.get(1).getId(), 2);
    assertEquals(vocabularyWords.get(1).getValue(), "Test 2");
  }

  @Test(expected = EmptyRecordException.class)
  public void addNull() throws Exception {
    vocabularyWordDAO.add(null);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmpty() throws Exception {
    vocabularyWordDAO.add(new VocabularyWord(""));
  }

  @Test(expected = AlreadyExistsException.class)
  public void addExisted() throws Exception {
    vocabularyWordDAO.add(new VocabularyWord("Test 1"));
  }

  @Test
  public void add() throws Exception {
    vocabularyWordDAO.add(new VocabularyWord("Test 3"));

    // check record from DB
    //

    List<VocabularyWord> vocabularyWords = vocabularyWordDAO.getAll();

    assertEquals(vocabularyWords.size(), 3);

    assertEquals(vocabularyWords.get(2).getId(), 3);
    assertEquals(vocabularyWords.get(2).getValue(), "Test 3");
  }
}
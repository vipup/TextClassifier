package com.irvil.textclassifier.dao;

import com.irvil.textclassifier.model.VocabularyWord;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class VocabularyWordDAOTest {
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
    List<VocabularyWord> vocabularyWords = vocabularyWordDAO.getAll();

    assertEquals(vocabularyWords.size(), 2);

    assertEquals(vocabularyWords.get(0).getId(), 1);
    assertEquals(vocabularyWords.get(0).getValue(), "Test 1");

    assertEquals(vocabularyWords.get(1).getId(), 2);
    assertEquals(vocabularyWords.get(1).getValue(), "Test 2");
  }

  @Test(expected = EmptyRecordException.class)
  public void addNullVocabulary() throws Exception {
    vocabularyWordDAO.addAll(null);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmptyVocabulary() throws Exception {
    vocabularyWordDAO.addAll(new ArrayList<>());
  }

  @Test(expected = EmptyRecordException.class)
  public void addNullWord() throws Exception {
    List<VocabularyWord> vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord("Test"));
    vocabulary.add(null);
    vocabularyWordDAO.addAll(vocabulary);
  }

  @Test(expected = EmptyRecordException.class)
  public void addEmptyWord() throws Exception {
    List<VocabularyWord> vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord("Test"));
    vocabulary.add(new VocabularyWord(""));
    vocabularyWordDAO.addAll(vocabulary);
  }

  @Test(expected = AlreadyExistsException.class)
  public void addExisted() throws Exception {
    List<VocabularyWord> vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord("Test"));
    vocabulary.add(new VocabularyWord("Test 1")); // existed
    vocabularyWordDAO.addAll(vocabulary);
  }

  @Test
  public void add() throws Exception {
    List<VocabularyWord> vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord("Test 3"));
    vocabularyWordDAO.addAll(vocabulary);

    // check record from DB
    //

    List<VocabularyWord> vocabularyWords = vocabularyWordDAO.getAll();

    assertEquals(vocabularyWords.size(), 3);

    assertEquals(vocabularyWords.get(2).getId(), 3);
    assertEquals(vocabularyWords.get(2).getValue(), "Test 3");
  }
}
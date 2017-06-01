package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.AlreadyExistsException;
import com.irvil.nntextclassifier.dao.EmptyRecordException;
import com.irvil.nntextclassifier.dao.StorageCreator;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.VocabularyWord;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCVocabularyWordDAOTest {
  private JDBCConnector jdbcConnector = new JDBCSQLiteConnector(Config.getInstance().getDbPath() + "/test.db");
  private StorageCreator storageCreator = new JDBCDBCreator(jdbcConnector);
  private VocabularyWordDAO vocabularyWordDAO = new JDBCVocabularyWordDAO(jdbcConnector);

  @Before
  public void initializeTable() throws Exception {
    storageCreator.clearStorage();

    vocabularyWordDAO.add(new VocabularyWord("Test 1"));
    vocabularyWordDAO.add(new VocabularyWord("Test 2"));
  }

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
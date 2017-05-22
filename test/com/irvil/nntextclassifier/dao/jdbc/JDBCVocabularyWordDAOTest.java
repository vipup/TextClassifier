package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.model.VocabularyWord;
import org.junit.Test;

import static org.junit.Assert.*;

public class JDBCVocabularyWordDAOTest {
  private VocabularyWordDAO vocabularyWordDAO = new JDBCVocabularyWordDAO(new SQLiteJDBCTestConnector());

  @Test
  public void findByValue() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByValue("test");
    assertEquals(vw.getId(), 34);
  }

  @Test
  public void findByValueNonexistent() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByValue("testtesttest");
    assertEquals(vw, null);
  }

  @Test
  public void findByValueNull() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByValue(null);
    assertEquals(vw, null);
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(vocabularyWordDAO.getCount(), 34);
  }

  @Test
  public void findByID() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByID(34);
    assertEquals(vw.getValue(), "test");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByID(150);
    assertEquals(vw, null);
  }

  // todo: add test
  @Test
  public void add() throws Exception {

  }
}
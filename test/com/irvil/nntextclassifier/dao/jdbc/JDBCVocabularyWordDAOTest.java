package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.model.VocabularyWord;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JDBCVocabularyWordDAOTest {
  private GenericDAO<VocabularyWord> vocabularyWordDAO;

  @Before
  public void initializeTable() throws Exception {
    vocabularyWordDAO = new JDBCVocabularyWordDAO(new JDBCSQLiteTestConnector());

    String tableName = "Vocabulary";
    JDBCDatabaseUtilities.cleanTable(tableName);
    JDBCDatabaseUtilities.insertToCatalog(tableName, new VocabularyWord(1, "test 1"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new VocabularyWord(2, "test 2"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new VocabularyWord(3, "test 3"));
    JDBCDatabaseUtilities.insertToCatalog(tableName, new VocabularyWord(4, "test 4"));
  }

  @Test
  public void getCount() throws Exception {
    assertEquals(vocabularyWordDAO.getCount(), 4);
  }

  @Test
  public void findByVector() throws Exception {
    double[] vector = new double[]{0.1, 0.3, 0.2};

    VocabularyWord vw = vocabularyWordDAO.findByVector(vector);
    assertEquals(vw.getValue(), "test 2");
  }

  @Test
  public void findByVectorNonexistent() throws Exception {
    double[] vector = new double[]{0.1, 0.2, 0.3, 0.3, 0.3, 0.6};

    VocabularyWord vw = vocabularyWordDAO.findByVector(vector);
    assertEquals(vw, null);
  }

  @Test
  public void findByVectorAllEqual() throws Exception {
    double[] vector = new double[]{0.1, 0.1, 0.1};

    VocabularyWord vw = vocabularyWordDAO.findByVector(vector);
    assertEquals(vw.getValue(), "test 1");
  }

  @Test
  public void findByVectorEmptyVector() throws Exception {
    double[] vector = new double[0];

    VocabularyWord vw = vocabularyWordDAO.findByVector(vector);
    assertEquals(vw, null);
  }

  @Test
  public void findByVectorNullVector() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByVector(null);
    assertEquals(vw, null);
  }

  @Test
  public void findByValue() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByValue("test 4");
    assertEquals(vw.getId(), 4);
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
  public void findByID() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByID(4);
    assertEquals(vw.getValue(), "test 4");
  }

  @Test
  public void findByIDNonexistent() throws Exception {
    VocabularyWord vw = vocabularyWordDAO.findByID(150);
    assertEquals(vw, null);
  }

  @Test
  public void add() throws Exception {
    int beforeCount = vocabularyWordDAO.getCount();

    String value = "Test add()";
    vocabularyWordDAO.add(new VocabularyWord(0, value));

    assertEquals(vocabularyWordDAO.getCount(), beforeCount + 1);
    assertEquals(vocabularyWordDAO.findByValue(value).getValue(), value);
  }
}
package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.VocabularyWord;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCVocabularyWordDAOTest {
  private VocabularyWordDAO vocabularyWordDAO;

  @Before
  public void initializeTable() throws Exception {
    Config config = Config.getInstance();
    JDBCConnector jdbcConnector = new JDBCSQLiteConnector(config.getDbPath() + "/test.db");
    vocabularyWordDAO = new JDBCVocabularyWordDAO(jdbcConnector);

    JDBCDatabaseUtilities.clearTable(jdbcConnector, "Vocabulary");
    vocabularyWordDAO.add(new VocabularyWord(0, "test 1")); //ok
    vocabularyWordDAO.add(new VocabularyWord(0, "test 2")); //ok
    vocabularyWordDAO.add(new VocabularyWord(0, "test 3")); //ok
    vocabularyWordDAO.add(new VocabularyWord(0, "test 4")); //ok
    vocabularyWordDAO.add(new VocabularyWord(0, "test 4")); // error: already exists
    vocabularyWordDAO.add(new VocabularyWord(0, "")); // error: empty
    vocabularyWordDAO.add(null); // error: null
  }

  @Test
  public void getAll() throws Exception {
    List<VocabularyWord> vocabularyWords = vocabularyWordDAO.getAll();

    assertEquals(vocabularyWords.size(), 4);

    assertEquals(vocabularyWords.get(0).getId(), 1);
    assertEquals(vocabularyWords.get(1).getId(), 2);
    assertEquals(vocabularyWords.get(2).getId(), 3);
    assertEquals(vocabularyWords.get(3).getId(), 4);

    assertEquals(vocabularyWords.get(0).getValue(), "test 1");
    assertEquals(vocabularyWords.get(1).getValue(), "test 2");
    assertEquals(vocabularyWords.get(2).getValue(), "test 3");
    assertEquals(vocabularyWords.get(3).getValue(), "test 4");
  }
}
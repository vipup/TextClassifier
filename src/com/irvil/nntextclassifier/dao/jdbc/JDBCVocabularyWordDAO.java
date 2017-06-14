package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.AlreadyExistsException;
import com.irvil.nntextclassifier.dao.EmptyRecordException;
import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCConnector;
import com.irvil.nntextclassifier.model.VocabularyWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCVocabularyWordDAO implements VocabularyWordDAO {
  private JDBCConnector connector;

  public JDBCVocabularyWordDAO(JDBCConnector connector) {
    if (connector == null) {
      throw new IllegalArgumentException();
    }

    this.connector = connector;
  }

  @Override
  public List<VocabularyWord> getAll() {
    List<VocabularyWord> vocabularyWords = new ArrayList<>();

    try (Connection con = connector.getConnection()) {
      String sql = "SELECT Id, Value FROM Vocabulary";
      ResultSet rs = con.createStatement().executeQuery(sql);

      while (rs.next()) {
        vocabularyWords.add(new VocabularyWord(rs.getInt("Id"), rs.getString("Value")));
      }
    } catch (SQLException ignored) {
    }

    return vocabularyWords;
  }

  @Override
  public void addAll(List<VocabularyWord> vocabulary) throws EmptyRecordException, AlreadyExistsException {
    if (vocabulary == null ||
        vocabulary.size() == 0) {
      throw new EmptyRecordException("Vocabulary is null or empty");
    }

    try (Connection con = connector.getConnection()) {
      con.setAutoCommit(false);

      // prepare sql query
      //

      String sqlInsert = "INSERT INTO Vocabulary (Value) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert);

      //

      for (VocabularyWord vocabularyWord : vocabulary) {
        // check
        //

        if (vocabularyWord == null ||
            vocabularyWord.getValue().equals("")) {
          throw new EmptyRecordException("Vocabulary word is null or empty");
        }

        if (isVocabularyWordExistsInDB(con, vocabularyWord)) {
          throw new AlreadyExistsException("Vocabulary word already exists");
        }

        // insert
        //

        statement.setString(1, vocabularyWord.getValue());
        statement.executeUpdate();
      }

      con.commit();
      con.setAutoCommit(true);
    } catch (SQLException ignored) {
    }
  }

  private boolean isVocabularyWordExistsInDB(Connection con, VocabularyWord vocabularyWord) throws SQLException {
    String sqlSelect = "SELECT Id FROM Vocabulary WHERE Value = ?";
    PreparedStatement statement = con.prepareStatement(sqlSelect);
    statement.setString(1, vocabularyWord.getValue());
    ResultSet rs = statement.executeQuery();

    return rs.next();
  }
}
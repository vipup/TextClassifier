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
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return vocabularyWords;
  }

  @Override
  public void add(VocabularyWord vocabularyWord) throws EmptyRecordException, AlreadyExistsException {
    if (vocabularyWord == null ||
        vocabularyWord.getValue().equals("")) {
      throw new EmptyRecordException("Vocabulary word is null or empty");
    }

    if (isVocabularyWordExistsInDB(vocabularyWord)) {
      throw new AlreadyExistsException("Vocabulary word already exists");
    }

    try (Connection con = connector.getConnection()) {
      String sqlInsert = "INSERT INTO Vocabulary (Value) VALUES (?)";
      PreparedStatement statement = con.prepareStatement(sqlInsert);
      statement.setString(1, vocabularyWord.getValue());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private boolean isVocabularyWordExistsInDB(VocabularyWord vocabularyWord) {
    try (Connection con = connector.getConnection()) {
      String sqlSelect = "SELECT Id FROM Vocabulary WHERE Value = ?";
      PreparedStatement statement = con.prepareStatement(sqlSelect);
      statement.setString(1, vocabularyWord.getValue());
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }
}
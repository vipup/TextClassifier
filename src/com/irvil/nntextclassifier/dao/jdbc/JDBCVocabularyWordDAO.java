package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.model.VocabularyWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCVocabularyWordDAO extends JDBCGenericDAO<VocabularyWord> implements VocabularyWordDAO {
  @Override
  public VocabularyWord findByValue(String value) {
    String sql = "SELECT Id FROM Vocabulary WHERE Value = ?";

    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement statement = con.prepareStatement(sql);
      statement.setString(1, value);
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        return new VocabularyWord(rs.getInt("Id"), value);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  protected String getTableName() {
    return "Vocabulary";
  }

  @Override
  protected VocabularyWord createObject(int id, String value) {
    return new VocabularyWord(id, value);
  }
}
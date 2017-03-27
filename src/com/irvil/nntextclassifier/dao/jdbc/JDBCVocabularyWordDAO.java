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
    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement statement = con.prepareStatement("SELECT Id FROM Vocabulary WHERE Value = ?");
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
  public VocabularyWord findByID(int id) {
    ResultSet rs = getResultSetByID(id);

    try {
      return new VocabularyWord(rs.getInt("Id"), rs.getString("Value"));
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
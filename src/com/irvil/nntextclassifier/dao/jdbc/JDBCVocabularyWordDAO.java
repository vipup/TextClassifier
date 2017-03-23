package com.irvil.nntextclassifier.dao.jdbc;

import com.irvil.nntextclassifier.dao.VocabularyWordDAO;
import com.irvil.nntextclassifier.model.VocabularyWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCVocabularyWordDAO implements VocabularyWordDAO {
  @Override
  public int getCount() {
    int count = 0;

    try (Connection con = DBConnector.getDBConnection()) {
      ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM Vocabulary AS Count");

      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return count;
  }

  @Override
  public List<VocabularyWord> getAll() {
    return null;
  }

  @Override
  public VocabularyWord findByID(int id) {
    throw new UnsupportedOperationException();
  }

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
  public void add(VocabularyWord object) {
    try (Connection con = DBConnector.getDBConnection()) {
      PreparedStatement insertStatement = con.prepareStatement("INSERT INTO Vocabulary (value) VALUES (?)");
      insertStatement.setString(1, object.getValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
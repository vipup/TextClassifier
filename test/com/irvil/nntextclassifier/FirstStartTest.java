package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.dao.factories.JDBCDAOFactory;
import com.irvil.nntextclassifier.dao.jdbc.connectors.JDBCSQLiteConnector;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import com.irvil.nntextclassifier.recognizer.Recognizer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FirstStartTest {
  private FirstStart firstStart;

  @Before
  public void setUp() {
    firstStart = new FirstStart(new JDBCDAOFactory(new JDBCSQLiteConnector("./test_db/test.db")), new FilteredUnigram());
  }

  @Test
  public void createDbFolder() throws Exception {
    FirstStart.createDbFolder("./test_db/test_folder");
    assertEquals(new File("./test_db/test_folder").delete(), true);
  }

  @Test
  public void trainAndSaveRecognizers() throws Exception {
    //firstStart.trainAndSaveRecognizers();
  }

  @Test
  public void createStorage() throws Exception {
    //firstStart.createStorage();
  }

  @Test
  public void readXlsxFile() throws Exception {
    List<IncomingCall> incomingCalls = firstStart.readXlsxFile(new File("./test_db/test.xlsx"));

    //todo: complete method
    assertEquals(incomingCalls.size(), 2);
    assertEquals(incomingCalls.get(0).getText(), "test test");
    assertEquals(incomingCalls.get(0).getCharacteristics().size(), 2);
    assertEquals(incomingCalls.get(0).getCharacteristics(), 2);
  }

  @Test
  public void fillStorage() throws Exception {
  }

  @Test
  public void notifyObservers() throws Exception {
    final String[] msg = {"Msg from observer"};

    firstStart.addObserver((text) -> msg[0] = text);
    firstStart.notifyObservers("Test msg");

    assertEquals(msg[0], "Test msg");
  }
}
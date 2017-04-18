package com.irvil.nntextclassifier.ngram;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UnigramTest {
  private NGramStrategy nGramStrategy;
  private Set<String> uniqueValues;

  @Before
  public void setUp() {
    nGramStrategy = new Unigram();
  }

  @Test
  public void getNGramCyrillicText() throws Exception {
    uniqueValues = nGramStrategy.getNGram("Привет. Хотела бы сделать 235.2 тест метода. Тест,.. Хотел сделал.");
    String[] ideal = {"привет", "хотел", "бы", "сдела", "тест", "метод"};

    assertArrayEquals(ideal, uniqueValues.toArray());
  }

  @Test
  public void getNGramLatinText() throws Exception {
    uniqueValues = nGramStrategy.getNGram("Hello! This is method 23 test. Test methods hello");
    String[] ideal = {"hello", "this", "is", "method", "test", "methods"};

    assertArrayEquals(ideal, uniqueValues.toArray());
  }

  @Test
  public void getNGramEmptyString() throws Exception {
    assertEquals(nGramStrategy.getNGram("").size(), 0);
  }

  @Test
  public void getNGramNull() throws Exception {
    assertEquals(nGramStrategy.getNGram(null).size(), 0);
  }
}
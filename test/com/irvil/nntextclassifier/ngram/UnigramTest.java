package com.irvil.nntextclassifier.ngram;

public class UnigramTest extends NGramStrategyTest {
  @Override
  public void setUp() {
    nGramStrategy = new Unigram();
    idealCyrillicText = new String[] {"привет", "хотела", "бы", "сделать", "235", "2", "тест", "метода", "хотел", "сделал"};
    idealLatinText = new String[] {"hello", "this", "is", "method", "23", "test", "methods"};
  }
}
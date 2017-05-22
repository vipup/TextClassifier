package com.irvil.nntextclassifier.ngram;

public class FilteredBigramTest extends NGramStrategyTest {
  @Override
  public void setUp() {
    nGramStrategy = new FilteredBigram();
    idealCyrillicText = new String[] {"привет хотел", "хотел бы", "бы сдела", "сдела тест", "тест метод"};
    idealLatinText = new String[] {"hello this", "this is", "is method", "method test", "test methods"};
  }
}
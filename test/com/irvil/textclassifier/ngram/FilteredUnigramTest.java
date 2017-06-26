package com.irvil.textclassifier.ngram;

public class FilteredUnigramTest extends NGramStrategyTest {
  @Override
  public void setUp() {
    nGramStrategy = new FilteredUnigram();
    idealCyrillicText = new String[]{"привет", "хотел", "бы", "сдела", "тест", "метод"};
    idealLatinText = new String[]{"hello", "this", "is", "method", "test", "methods"};
  }
}
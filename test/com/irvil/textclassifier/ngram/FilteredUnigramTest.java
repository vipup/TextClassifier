package com.irvil.textclassifier.ngram;

public class FilteredUnigramTest extends NGramStrategyTest {
  @Override
  protected void initializeIdeal() {
    idealCyrillicText = new String[]{"привет", "хотел", "бы", "сдела", "тест", "метод"};
    idealLatinText = new String[]{"hello", "this", "is", "method", "test", "methods"};
  }

  @Override
  protected NGramStrategy getNGramStrategy() {
    return new FilteredUnigram();
  }
}
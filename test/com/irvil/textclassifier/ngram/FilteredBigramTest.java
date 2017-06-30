package com.irvil.textclassifier.ngram;

public class FilteredBigramTest extends NGramStrategyTest {
  @Override
  protected void initializeIdeal() {
    idealCyrillicText = new String[]{"привет хотел", "хотел бы", "бы сдела", "сдела тест", "тест метод"};
    idealLatinText = new String[]{"hello this", "this is", "is method", "method test", "test methods"};
  }

  @Override
  protected NGramStrategy getNGramStrategy() {
    return new Bigram(new FilteredUnigram());
  }
}
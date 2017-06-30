package com.irvil.textclassifier.ngram;

public class BigramTest extends NGramStrategyTest {
  @Override
  protected void initializeIdeal() {
    idealCyrillicText = new String[]{"привет хотела", "хотела бы", "бы сделать", "сделать 235", "235 2", "2 тест", "тест метода", "метода хотел", "хотел сделал"};
    idealLatinText = new String[]{"hello this", "this is", "is method", "method 23", "23 test", "test methods"};
  }

  @Override
  protected NGramStrategy getNGramStrategy() {
    return new Bigram(new Unigram());
  }
}
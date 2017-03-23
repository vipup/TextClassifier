package com.irvil.nntextclassifier.ngram;

import com.irvil.nntextclassifier.PorterStemmer;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Unigram implements NGramStrategy {
  @Override
  public Set<String> getNGram(String text) {
    Set<String> uniqueValues = new LinkedHashSet<>();
    String[] words = split(clean(text));

    for (int i = 0; i < words.length; i++) {
      words[i] = doStem(words[i]);
    }

    Collections.addAll(uniqueValues, words);
    uniqueValues.removeIf(s -> s.equals(""));

    return uniqueValues;
  }

  private String[] split(String text) {
    return text.split("[ \n\t\r$+<>№=]");
  }

  private String clean(String text) {
    return text.toLowerCase().replaceAll("[\\pP\\d]", " ");
  }

  private String doStem(String word) {
    return PorterStemmer.doStem(word);
  }
}
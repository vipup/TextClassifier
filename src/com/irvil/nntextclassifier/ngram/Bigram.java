package com.irvil.nntextclassifier.ngram;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Bigram implements NGramStrategy {
  @Override
  public Set<String> getNGram(String text) {
    Set<String> uniqueValues = new LinkedHashSet<>();
    List<String> words = new ArrayList<>(new Unigram().getNGram(text));

    for (int i = 0; i < words.size() - 1; i++) {
      uniqueValues.add(words.get(i) + " " + words.get(i + 1));
    }

    return uniqueValues;
  }
}
package com.irvil.nntextclassifier.ngram;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class FilteredBigram implements NGramStrategy {
  @Override
  public Set<String> getNGram(String text) {
    List<String> unigram = new ArrayList<>(NGramStrategySimpleFactory.getStrategy("filtered_unigram").getNGram(text));

    // concatenate words to bigrams
    // example: "How are you doing?" => {"how are", "are you", "you doing"}

    Set<String> uniqueValues = new LinkedHashSet<>();

    for (int i = 0; i < unigram.size() - 1; i++) {
      uniqueValues.add(unigram.get(i) + " " + unigram.get(i + 1));
    }

    return uniqueValues;
  }
}
package com.irvil.nntextclassifier.ngram;

import java.util.Set;

public interface NGramStrategy {
  Set<String> getNGram(String text);

  static NGramStrategy getStrategy(String type) {
    switch (type) {
      case "unigram":
        return new Unigram();
      case "filtered_unigram":
        return new FilteredUnigram();
      case "bigram":
        return new Bigram(new Unigram());
      case "filtered_bigram":
        return new Bigram(new FilteredUnigram());
      default:
        return null;
    }
  }
}
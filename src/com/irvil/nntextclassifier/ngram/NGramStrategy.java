package com.irvil.nntextclassifier.ngram;

import java.util.Set;

// todo: create Factory
public interface NGramStrategy {
  Set<String> getNGram(String text);
}
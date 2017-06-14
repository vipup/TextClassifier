package com.irvil.nntextclassifier.ngram;

import java.util.Set;

public interface NGramStrategy {
  Set<String> getNGram(String text);
}
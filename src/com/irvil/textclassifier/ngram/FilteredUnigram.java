package com.irvil.textclassifier.ngram;

import com.irvil.textclassifier.PorterStemmer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class FilteredUnigram implements NGramStrategy {
  @Override
  public Set<String> getNGram(String text) {
    // get all significant words
    String[] words = clean(text).split("[ \n\t\r$+<>№=]");

    // remove endings of words
    for (int i = 0; i < words.length; i++) {
      words[i] = PorterStemmer.doStem(words[i]);
    }

    Set<String> uniqueValues = new LinkedHashSet<>(Arrays.asList(words));
    uniqueValues.removeIf(s -> s.equals(""));

    return uniqueValues;
  }

  private String clean(String text) {
    // remove all digits and punctuation marks
    if (text != null) {
      return text.toLowerCase().replaceAll("[\\pP\\d]", " ");
    } else {
      return "";
    }
  }
}
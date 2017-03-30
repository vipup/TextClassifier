package com.irvil.nntextclassifier.ngram;

import com.irvil.nntextclassifier.PorterStemmer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Unigram implements NGramStrategy {
  @Override
  public Set<String> getNGram(String text) {
    // get all significant words
    String[] words = split(clean(text));

    // remove endings of words
    for (int i = 0; i < words.length; i++) {
      words[i] = doStem(words[i]);
    }

    Set<String> uniqueValues = new LinkedHashSet<>(Arrays.asList(words));
    uniqueValues.removeIf(s -> s.equals(""));

    return uniqueValues;
  }

  private String[] split(String text) {
    return text.split("[ \n\t\r$+<>№=]");
  }

  private String clean(String text) {
    // remove all digits and punctuation marks
    return text.toLowerCase().replaceAll("[\\pP\\d]", " ");
  }

  private String doStem(String word) {
    // remove ending of word
    return PorterStemmer.doStem(word);
  }
}
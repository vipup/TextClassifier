package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.NGramStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class VocabularyBuilder {
  private NGramStrategy nGram;

  VocabularyBuilder(NGramStrategy nGram) {
    this.nGram = nGram;
  }

  List<VocabularyWord> getVocabulary(List<IncomingCall> incomingCalls) {
    if (incomingCalls == null ||
        incomingCalls.size() == 0) {
      throw new IllegalArgumentException();
    }

    Map<String, Integer> uniqueValues = new HashMap<>();
    List<VocabularyWord> vocabulary = new ArrayList<>();

    // count frequency of use each word (converted to n-gram) from all Incoming Calls Texts
    //

    for (IncomingCall incomingCall : incomingCalls) {
      for (String word : nGram.getNGram(incomingCall.getText())) {
        if (uniqueValues.containsKey(word)) {
          // increase counter
          uniqueValues.put(word, uniqueValues.get(word) + 1);
        } else {
          // add new word
          uniqueValues.put(word, 1);
        }
      }
    }

    // convert uniqueValues to Vocabulary, excluding rare (frequency of use less then 4)
    //

    for (Map.Entry<String, Integer> entry : uniqueValues.entrySet()) {
      if (entry.getValue() > 1) {
        vocabulary.add(new VocabularyWord(entry.getKey()));
      }
    }

    return vocabulary;
  }
}
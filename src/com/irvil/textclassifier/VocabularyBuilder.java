package com.irvil.textclassifier;

import com.irvil.textclassifier.model.ClassifiableText;
import com.irvil.textclassifier.model.VocabularyWord;
import com.irvil.textclassifier.ngram.NGramStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class VocabularyBuilder {
  private final NGramStrategy nGramStrategy;

  VocabularyBuilder(NGramStrategy nGramStrategy) {
    if (nGramStrategy == null) {
      throw new IllegalArgumentException();
    }

    this.nGramStrategy = nGramStrategy;
  }

  List<VocabularyWord> getVocabulary(List<ClassifiableText> classifiableTexts) {
    if (classifiableTexts == null ||
        classifiableTexts.size() == 0) {
      throw new IllegalArgumentException();
    }

    Map<String, Integer> uniqueValues = new HashMap<>();
    List<VocabularyWord> vocabulary = new ArrayList<>();

    // count frequency of use each word (converted to n-gram) from all Classifiable Texts
    //

    for (ClassifiableText classifiableText : classifiableTexts) {
      for (String word : nGramStrategy.getNGram(classifiableText.getText())) {
        if (uniqueValues.containsKey(word)) {
          // increase counter
          uniqueValues.put(word, uniqueValues.get(word) + 1);
        } else {
          // add new word
          uniqueValues.put(word, 1);
        }
      }
    }

    // convert uniqueValues to Vocabulary, excluding infrequent
    //

    for (Map.Entry<String, Integer> entry : uniqueValues.entrySet()) {
      if (entry.getValue() > 3) {
        vocabulary.add(new VocabularyWord(entry.getKey()));
      }
    }

    return vocabulary;
  }
}
package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VocabularyBuilderTest {
  private VocabularyBuilder vocabularyBuilder = new VocabularyBuilder(new FilteredUnigram());

  @Test
  public void getVocabulary() throws Exception {
    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("qw we"));
    incomingCalls.add(new IncomingCall("er we"));
    incomingCalls.add(new IncomingCall("we rt"));
    incomingCalls.add(new IncomingCall("er rt"));

    List<VocabularyWord> vocabulary = vocabularyBuilder.getVocabulary(incomingCalls);

    assertEquals(vocabulary.size(), 3);
    assertEquals(vocabulary.get(0).getValue(), "rt");
    assertEquals(vocabulary.get(1).getValue(), "er");
    assertEquals(vocabulary.get(2).getValue(), "we");
  }

  @Test
  public void getVocabularyOneValue() throws Exception {
    List<IncomingCall> incomingCalls = new ArrayList<>();
    incomingCalls.add(new IncomingCall("qw we"));

    List<VocabularyWord> vocabulary = vocabularyBuilder.getVocabulary(incomingCalls);

    assertEquals(vocabulary.size(), 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getVocabularyNull() throws Exception {
    vocabularyBuilder.getVocabulary(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getVocabularyEmpty() throws Exception {
    vocabularyBuilder.getVocabulary(new ArrayList<>());
  }
}
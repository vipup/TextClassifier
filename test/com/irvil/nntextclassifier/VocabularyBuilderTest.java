package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.model.ClassifiableText;
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
    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("qw we"));
    classifiableTexts.add(new ClassifiableText("er we"));
    classifiableTexts.add(new ClassifiableText("we rt"));
    classifiableTexts.add(new ClassifiableText("er rt"));

    List<VocabularyWord> vocabulary = vocabularyBuilder.getVocabulary(classifiableTexts);

    assertEquals(vocabulary.size(), 3);
    assertEquals(vocabulary.get(0).getValue(), "rt");
    assertEquals(vocabulary.get(1).getValue(), "er");
    assertEquals(vocabulary.get(2).getValue(), "we");
  }

  @Test
  public void getVocabularyOneValue() throws Exception {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();
    classifiableTexts.add(new ClassifiableText("qw we"));

    List<VocabularyWord> vocabulary = vocabularyBuilder.getVocabulary(classifiableTexts);

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
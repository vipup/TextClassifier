package com.irvil.textclassifier.classifier;

import com.irvil.textclassifier.model.Characteristic;
import com.irvil.textclassifier.model.CharacteristicValue;
import com.irvil.textclassifier.model.ClassifiableText;
import com.irvil.textclassifier.model.VocabularyWord;
import com.irvil.textclassifier.ngram.FilteredUnigram;
import com.irvil.textclassifier.ngram.NGramStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ClassifierTest {
  private final File trainedClassifier = new File("./test_db/TestNeuralNetworkClassifier");
  private final NGramStrategy nGramStrategy = new FilteredUnigram();
  private Classifier classifier;
  private Characteristic characteristic;
  private List<VocabularyWord> vocabulary;

  @Before
  public void loadFromFile() {
    // create characteristic
    //

    Set<CharacteristicValue> possibleValues = new LinkedHashSet<>();
    possibleValues.add(new CharacteristicValue(1, "get"));
    possibleValues.add(new CharacteristicValue(2, "set"));
    possibleValues.add(new CharacteristicValue(3, "add"));
    characteristic = new Characteristic("Method", possibleValues);

    // create vocabulary
    //

    vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord(1, "their"));
    vocabulary.add(new VocabularyWord(2, "specified"));
    vocabulary.add(new VocabularyWord(3, "that"));
    vocabulary.add(new VocabularyWord(4, "and"));
    vocabulary.add(new VocabularyWord(5, "shifts"));
    vocabulary.add(new VocabularyWord(6, "subsequent"));
    vocabulary.add(new VocabularyWord(7, "if"));
    vocabulary.add(new VocabularyWord(8, "element"));
    vocabulary.add(new VocabularyWord(9, "adds"));
    vocabulary.add(new VocabularyWord(10, "in"));
    vocabulary.add(new VocabularyWord(11, "replaces"));
    vocabulary.add(new VocabularyWord(12, "one"));
    vocabulary.add(new VocabularyWord(13, "this"));
    vocabulary.add(new VocabularyWord(14, "optional"));
    vocabulary.add(new VocabularyWord(15, "right"));
    vocabulary.add(new VocabularyWord(16, "list"));
    vocabulary.add(new VocabularyWord(17, "any"));
    vocabulary.add(new VocabularyWord(18, "the"));
    vocabulary.add(new VocabularyWord(19, "with"));
    vocabulary.add(new VocabularyWord(20, "indices"));
    vocabulary.add(new VocabularyWord(21, "at"));
    vocabulary.add(new VocabularyWord(22, "currently"));
    vocabulary.add(new VocabularyWord(23, "elements"));
    vocabulary.add(new VocabularyWord(24, "returns"));
    vocabulary.add(new VocabularyWord(25, "position"));
    vocabulary.add(new VocabularyWord(26, "to"));
    vocabulary.add(new VocabularyWord(27, "operation"));
    vocabulary.add(new VocabularyWord(28, "inserts"));

    // load trained classifier
    //

    classifier = new Classifier(trainedClassifier, characteristic, vocabulary, nGramStrategy);
  }

  @Test
  public void createNetwork() {
    new Classifier(characteristic, vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonexistentFile() {
    new Classifier(new File("./test_db/nonexistentFile"), characteristic, vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullCharacteristic() {
    new Classifier(trainedClassifier, null, vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyCharacteristic() {
    new Classifier(trainedClassifier, new Characteristic("Test"), vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullVocabulary() {
    new Classifier(trainedClassifier, characteristic, null, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyVocabulary() {
    new Classifier(trainedClassifier, characteristic, new ArrayList<>(), nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullNGram() {
    new Classifier(trainedClassifier, characteristic, vocabulary, null);
  }

  @Test
  public void classify() throws Exception {
    ClassifiableText ctGet = new ClassifiableText("Returns the element at the specified position in this list");
    CharacteristicValue cvGet = classifier.classify(ctGet);

    assertEquals(cvGet.getId(), 1);
    assertEquals(cvGet.getValue(), "get");

    //

    ClassifiableText ctSet = new ClassifiableText("Replaces the element at the specified position in this list with the specified element (optional operation)");
    CharacteristicValue cvSet = classifier.classify(ctSet);

    assertEquals(cvSet.getId(), 2);
    assertEquals(cvSet.getValue(), "set");

    //

    ClassifiableText ctAdd = new ClassifiableText("Inserts the specified element at the specified position in this list (optional operation). Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their indices)");
    CharacteristicValue cvAdd = classifier.classify(ctAdd);

    assertEquals(cvAdd.getId(), 3);
    assertEquals(cvAdd.getValue(), "add");
  }

  @Test
  public void saveTrainedClassifier() throws Exception {
    classifier.saveTrainedClassifier(new File("./test_db/TestSave"));
    assertEquals(new File("./test_db/TestSave").delete(), true);
  }

  @Test
  public void getCharacteristicName() throws Exception {
    assertEquals(classifier.getCharacteristic().getName(), "Method");
  }

  @Test
  public void train() throws Exception {
    // create list for train
    //

    List<ClassifiableText> classifiableTexts = new ArrayList<>();

    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Method"), new CharacteristicValue(1, "get"));
    classifiableTexts.add(new ClassifiableText("shifts right any this operation", characteristics));

    characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Method"), new CharacteristicValue(3, "add"));
    classifiableTexts.add(new ClassifiableText("that at returns", characteristics));

    // make sure classifier is stupid
    //

    assertNotEquals(classifier.classify(classifiableTexts.get(0)).getValue(), "get");
    assertNotEquals(classifier.classify(classifiableTexts.get(1)).getValue(), "add");

    // train
    classifier.train(classifiableTexts);

    // make sure classifier became smart
    //

    assertEquals(classifier.classify(classifiableTexts.get(0)).getValue(), "get");
    assertEquals(classifier.classify(classifiableTexts.get(1)).getValue(), "add");
    assertEquals(classifier.classify(new ClassifiableText("shifts right sdawwda any this operation")).getValue(), "get");
  }

  @Test
  public void toStringTest() throws Exception {
    assertEquals(classifier.toString(), "MethodNeuralNetworkClassifier");
  }

  @Test
  public void notifyObservers() throws Exception {
    final String[] msg = {"Msg from observer"};

    classifier.addObserver((text) -> msg[0] = text);
    classifier.notifyObservers("Test msg");

    assertEquals(msg[0], "Test msg");
  }

  @Test
  public void shutdown() throws Exception {
    Classifier.shutdown();
  }
}
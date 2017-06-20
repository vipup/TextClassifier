package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RecognizerTest {
  private Recognizer recognizer;
  private Characteristic characteristic;
  private List<VocabularyWord> vocabulary;
  private File trainedRecognizer = new File("./test_db/MethodRecognizerNeuralNetwork");
  private NGramStrategy nGramStrategy = new FilteredUnigram();

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

    // load trained recognizer
    //

    recognizer = new Recognizer(trainedRecognizer, characteristic, vocabulary, nGramStrategy);
  }

  @Test
  public void createNetwork() {
    new Recognizer(characteristic, vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonexistentFile() {
    new Recognizer(new File("./test_db/nonexistentFile"), characteristic, vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullCharacteristic() {
    new Recognizer(trainedRecognizer, null, vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyCharacteristic() {
    new Recognizer(trainedRecognizer, new Characteristic("Test"), vocabulary, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullVocabulary() {
    new Recognizer(trainedRecognizer, characteristic, null, nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyVocabulary() {
    new Recognizer(trainedRecognizer, characteristic, new ArrayList<>(), nGramStrategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullNGram() {
    new Recognizer(trainedRecognizer, characteristic, vocabulary, null);
  }

  @Test
  public void recognize() throws Exception {
    IncomingCall icGet = new IncomingCall("Returns the element at the specified position in this list");
    CharacteristicValue cvGet = recognizer.recognize(icGet);

    assertEquals(cvGet.getId(), 1);
    assertEquals(cvGet.getValue(), "get");

    //

    IncomingCall icSet = new IncomingCall("Replaces the element at the specified position in this list with the specified element (optional operation)");
    CharacteristicValue cvSet = recognizer.recognize(icSet);

    assertEquals(cvSet.getId(), 2);
    assertEquals(cvSet.getValue(), "set");

    //

    IncomingCall icAdd = new IncomingCall("Inserts the specified element at the specified position in this list (optional operation). Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their indices)");
    CharacteristicValue cvAdd = recognizer.recognize(icAdd);

    assertEquals(cvAdd.getId(), 3);
    assertEquals(cvAdd.getValue(), "add");
  }

  @Test
  public void saveTrainedRecognizer() throws Exception {
    recognizer.saveTrainedRecognizer(new File("./test_db/TestSave"));
    assertEquals(new File("./test_db/TestSave").delete(), true);
  }

  @Test
  public void getCharacteristicName() throws Exception {
    assertEquals(recognizer.getCharacteristicName(), "Method");
  }

  @Test
  public void train() throws Exception {
    // create list for train
    //

    List<IncomingCall> incomingCalls = new ArrayList<>();

    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Method"), new CharacteristicValue(1, "get"));
    incomingCalls.add(new IncomingCall("shifts right any this operation", characteristics));

    characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Method"), new CharacteristicValue(3, "add"));
    incomingCalls.add(new IncomingCall("that at returns", characteristics));

    // make sure recognizer is stupid
    //

    assertNotEquals(recognizer.recognize(incomingCalls.get(0)).getValue(), "get");
    assertNotEquals(recognizer.recognize(incomingCalls.get(1)).getValue(), "add");

    // train
    recognizer.train(incomingCalls);

    // make sure recognizer became smart
    //

    assertEquals(recognizer.recognize(incomingCalls.get(0)).getValue(), "get");
    assertEquals(recognizer.recognize(incomingCalls.get(1)).getValue(), "add");
    assertEquals(recognizer.recognize(new IncomingCall("shifts right sdawwda any this operation")).getValue(), "get");
  }

  @Test
  public void toStringTest() throws Exception {
    assertEquals(recognizer.toString(), "MethodRecognizerNeuralNetwork");
  }

  @Test
  public void notifyObservers() throws Exception {
    final String[] msg = {"Msg from observer"};

    recognizer.addObserver((text) -> msg[0] = text);
    recognizer.notifyObservers("Test msg");

    assertEquals(msg[0], "Test msg");
  }

  @Test
  public void shutdown() throws Exception {
    Recognizer.shutdown();
  }
}
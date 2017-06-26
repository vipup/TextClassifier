package com.irvil.textclassifier.recognizer;

import com.irvil.textclassifier.model.Characteristic;
import com.irvil.textclassifier.model.CharacteristicValue;
import com.irvil.textclassifier.model.ClassifiableText;
import com.irvil.textclassifier.model.VocabularyWord;
import com.irvil.textclassifier.ngram.NGramStrategy;
import com.irvil.textclassifier.observer.Observable;
import com.irvil.textclassifier.observer.Observer;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.PersistError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public class Recognizer implements Observable {
  private final Characteristic characteristic;
  private final int inputLayerSize;
  private final int outputLayerSize;
  private final BasicNetwork network;
  private final List<VocabularyWord> vocabulary;
  private final NGramStrategy nGramStrategy;
  private final List<Observer> observers = new ArrayList<>();

  public Recognizer(File trainedNetwork, Characteristic characteristic, List<VocabularyWord> vocabulary, NGramStrategy nGramStrategy) {
    if (characteristic == null ||
        characteristic.getName().equals("") ||
        characteristic.getPossibleValues() == null ||
        characteristic.getPossibleValues().size() == 0 ||
        vocabulary == null ||
        vocabulary.size() == 0 ||
        nGramStrategy == null) {
      throw new IllegalArgumentException();
    }

    this.characteristic = characteristic;
    this.vocabulary = vocabulary;
    this.inputLayerSize = vocabulary.size();
    this.outputLayerSize = characteristic.getPossibleValues().size();
    this.nGramStrategy = nGramStrategy;

    if (trainedNetwork == null) {
      this.network = createNeuralNetwork();
    } else {
      // load neural network from file
      try {
        this.network = (BasicNetwork) loadObject(trainedNetwork);
      } catch (PersistError e) {
        throw new IllegalArgumentException();
      }
    }
  }

  public Recognizer(Characteristic characteristic, List<VocabularyWord> vocabulary, NGramStrategy nGramStrategy) {
    this(null, characteristic, vocabulary, nGramStrategy);
  }

  public static void shutdown() {
    Encog.getInstance().shutdown();
  }

  private BasicNetwork createNeuralNetwork() {
    BasicNetwork network = new BasicNetwork();

    // input layer
    network.addLayer(new BasicLayer(null, true, inputLayerSize));

    // hidden layer
    network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputLayerSize / 2));

    // output layer
    network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputLayerSize));

    network.getStructure().finalizeStructure();
    network.reset();

    return network;
  }

  public CharacteristicValue recognize(ClassifiableText classifiableText) {
    double[] output = new double[outputLayerSize];

    // calculate output vector
    network.compute(getTextAsVectorOfWords(classifiableText), output);
    Encog.getInstance().shutdown();

    return convertVectorToCharacteristic(output);
  }

  private CharacteristicValue convertVectorToCharacteristic(double[] vector) {
    int idOfMaxValue = getIdOfMaxValue(vector);

    // find CharacteristicValue with found Id
    //

    for (CharacteristicValue c : characteristic.getPossibleValues()) {
      if (c.getId() == idOfMaxValue) {
        return c;
      }
    }

    return null;
  }

  private int getIdOfMaxValue(double[] vector) {
    int indexOfMaxValue = 0;
    double maxValue = vector[0];

    for (int i = 1; i < vector.length; i++) {
      if (vector[i] > maxValue) {
        maxValue = vector[i];
        indexOfMaxValue = i;
      }
    }

    return indexOfMaxValue + 1;
  }

  public void saveTrainedRecognizer(File trainedNetwork) {
    saveObject(trainedNetwork, network);
    notifyObservers("Trained Recognizer for Characteristics '" + characteristic.getName() + "' saved. Wait...");
  }

  public Characteristic getCharacteristic() {
    return characteristic;
  }

  public void train(List<ClassifiableText> classifiableTexts) {
    // prepare input and ideal vectors
    // input <- ClassifiableText text vector
    // ideal <- characteristicValue vector
    //

    double[][] input = getInput(classifiableTexts);
    double[][] ideal = getIdeal(classifiableTexts);

    // train
    //

    Propagation train = new ResilientPropagation(network, new BasicMLDataSet(input, ideal));
    train.setThreadCount(16);

    do {
      train.iteration();
      notifyObservers("Training Recognizer for Characteristics '" + characteristic.getName() + "'. Errors: " + String.format("%.2f", train.getError() * 100) + "%. Wait...");
    } while (train.getError() > 0.01);

    train.finishTraining();
    notifyObservers("Recognizer for Characteristics '" + characteristic.getName() + "' trained. Wait...");
  }

  private double[][] getInput(List<ClassifiableText> classifiableTexts) {
    double[][] input = new double[classifiableTexts.size()][inputLayerSize];

    // convert all classifiable texts to vectors
    //

    int i = 0;

    for (ClassifiableText classifiableText : classifiableTexts) {
      input[i++] = getTextAsVectorOfWords(classifiableText);
    }

    return input;
  }

  private double[][] getIdeal(List<ClassifiableText> classifiableTexts) {
    double[][] ideal = new double[classifiableTexts.size()][outputLayerSize];

    // convert all classifiable text characteristics to vectors
    //

    int i = 0;

    for (ClassifiableText classifiableText : classifiableTexts) {
      ideal[i++] = getCharacteristicAsVector(classifiableText);
    }

    return ideal;
  }

  // example:
  // count = 5; id = 4;
  // vector = {0, 0, 0, 1, 0}
  private double[] getCharacteristicAsVector(ClassifiableText classifiableText) {
    double[] vector = new double[outputLayerSize];
    vector[classifiableText.getCharacteristicValue(characteristic).getId() - 1] = 1;
    return vector;
  }

  private double[] getTextAsVectorOfWords(ClassifiableText classifiableText) {
    double[] vector = new double[inputLayerSize];

    // convert text to nGramStrategy
    Set<String> uniqueValues = nGramStrategy.getNGram(classifiableText.getText());

    // create vector
    //

    for (String word : uniqueValues) {
      VocabularyWord vw = findWordInVocabulary(word);

      if (vw != null) { // word found in vocabulary
        vector[vw.getId() - 1] = 1;
      }
    }

    return vector;
  }

  private VocabularyWord findWordInVocabulary(String word) {
    try {
      return vocabulary.get(vocabulary.indexOf(new VocabularyWord(word)));
    } catch (NullPointerException | IndexOutOfBoundsException e) {
      return null;
    }
  }

  @Override
  public String toString() {
    return characteristic.getName() + "RecognizerNeuralNetwork";
  }

  @Override
  public void addObserver(Observer o) {
    observers.add(o);
  }

  @Override
  public void removeObserver(Observer o) {
    observers.remove(o);
  }

  @Override
  public void notifyObservers(String text) {
    for (Observer o : observers) {
      o.update(text);
    }
  }
}
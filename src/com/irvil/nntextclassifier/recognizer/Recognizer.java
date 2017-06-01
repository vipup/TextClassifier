package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.NGramStrategy;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public class Recognizer {
  private final String characteristicName;
  private final int inputLayerSize;
  private final int outputLayerSize;
  private BasicNetwork network;
  private final List<CharacteristicValue> characteristicValue;
  private final List<VocabularyWord> vocabulary;
  private final NGramStrategy nGram;

  // todo: change parameters to Characteristic
  public Recognizer(String characteristicName, List<CharacteristicValue> characteristicValue, List<VocabularyWord> vocabulary, NGramStrategy nGram) {
    this.characteristicName = characteristicName;
    this.characteristicValue = characteristicValue;
    this.vocabulary = vocabulary;
    this.inputLayerSize = vocabulary.size();
    this.outputLayerSize = characteristicValue.size();
    this.nGram = nGram;

    // create neural network
    this.network = new BasicNetwork();

    // input layer
    this.network.addLayer(new BasicLayer(null, true, inputLayerSize));

    // hidden layer
    this.network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputLayerSize / 4));

    // output layer
    this.network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputLayerSize));

    this.network.getStructure().finalizeStructure();
    this.network.reset();
  }

  public Recognizer(File trainedNetwork, String characteristicName, List<CharacteristicValue> characteristicValue, List<VocabularyWord> vocabulary, NGramStrategy nGram) {
    this(characteristicName, characteristicValue, vocabulary, nGram);

    // load neural network from file
    this.network = (BasicNetwork) loadObject(trainedNetwork);
  }

  public CharacteristicValue recognize(IncomingCall incomingCall) {
    double[] output = new double[outputLayerSize];

    // calculate output vector
    network.compute(getTextAsVectorOfWords(incomingCall), output);
    Encog.getInstance().shutdown();

    return convertVectorToCharacteristic(output);
  }

  private CharacteristicValue convertVectorToCharacteristic(double[] vector) {
    int idOfMaxValue = getIdOfMaxValue(vector);

    // find CharacteristicValue with found Id
    //

    for (CharacteristicValue c : characteristicValue) {
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
  }

  public void train(List<IncomingCall> incomingCalls) {
    // prepare input and ideal vectors
    // input <- IncomingCall text vector
    // ideal <- characteristicValue vector
    //

    double[][] input = getInput(incomingCalls);
    double[][] ideal = getIdeal(incomingCalls);

    // train
    //

    Propagation train = new ResilientPropagation(network, new BasicMLDataSet(input, ideal));
    train.setThreadCount(16);

    do {
      train.iteration();
      // todo: addPossibleValue observer
      //System.out.println("Error: " + train.getError());
    } while (train.getError() > 0.01);

    train.finishTraining();
  }

  private double[][] getInput(List<IncomingCall> incomingCalls) {
    double[][] input = new double[incomingCalls.size()][inputLayerSize];
    int i = 0;

    // convert all incoming call texts to vectors
    //

    for (IncomingCall incomingCall : incomingCalls) {
      input[i++] = getTextAsVectorOfWords(incomingCall);
    }
    return input;
  }

  private double[][] getIdeal(List<IncomingCall> incomingCalls) {
    double[][] ideal = new double[incomingCalls.size()][outputLayerSize];
    int i = 0;

    // convert all incoming call characteristics to vectors
    //

    for (IncomingCall incomingCall : incomingCalls) {
      ideal[i++] = getCharacteristicAsVector(incomingCall);
    }
    return ideal;
  }

  // example:
  // count = 5; id = 4;
  // vector = {0, 0, 0, 1, 0}
  private double[] getCharacteristicAsVector(IncomingCall incomingCall) {
    double[] vector = new double[outputLayerSize];

    // todo: fix
    //vector[incomingCall.getCharacteristic(characteristicName).getId() - 1] = 1;

    return vector;
  }

  private double[] getTextAsVectorOfWords(IncomingCall incomingCall) {
    double[] vector = new double[inputLayerSize];

    // convert text to nGram
    Set<String> uniqueValues = nGram.getNGram(incomingCall.getText());

    // create vector
    //

    for (String word : uniqueValues) {
      VocabularyWord vw = findWordInVocabulary(word);

      if (vw != null) {
        vector[vw.getId() - 1] = 1;
      }
    }

    return vector;
  }

  private VocabularyWord findWordInVocabulary(String word) {
    for (VocabularyWord vw : vocabulary) {
      if (vw.getValue().equals(word)) {
        return vw;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    return characteristicName + "Recognizer";
  }
}
package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.CatalogDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCIncomingCallDAO;
import com.irvil.nntextclassifier.dao.jdbc.JDBCVocabularyWordDAO;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.ngram.Unigram;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.io.File;
import java.util.List;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public abstract class Recognizer {
  private final int outputLayerSize;
  private final BasicNetwork network;
  private final int inputLayerSize;
  private final CatalogDAO dao;

  protected Recognizer(CatalogDAO dao) {
    this.inputLayerSize = new JDBCVocabularyWordDAO().getCount();
    this.outputLayerSize = dao.getCount();
    this.dao = dao;

    this.network = new BasicNetwork();
    this.network.addLayer(new BasicLayer(null, true, inputLayerSize));
    this.network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputLayerSize / 4));
    this.network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputLayerSize));
    this.network.getStructure().finalizeStructure();
    this.network.reset();
  }

  protected Recognizer(File trainedNetwork, CatalogDAO dao) {
    this.inputLayerSize = new JDBCVocabularyWordDAO().getCount();
    this.outputLayerSize = dao.getCount();
    this.dao = dao;

    this.network = (BasicNetwork) loadObject(trainedNetwork);
  }

  public Catalog recognize(IncomingCall incomingCall) {
    double[] output = new double[outputLayerSize];
    network.compute(incomingCall.getTextAsWordVector(new Unigram()), output);
    Encog.getInstance().shutdown();

    return dao.findByVector(output);
  }

  public void train() {
    List<IncomingCall> incomingCallsTrain = new JDBCIncomingCallDAO().getAll();

    double[][] input = new double[incomingCallsTrain.size()][inputLayerSize];
    double[][] ideal = new double[incomingCallsTrain.size()][outputLayerSize];
    int i = 0;

    for (IncomingCall incomingCall : incomingCallsTrain) {
      input[i] = incomingCall.getTextAsWordVector(new Unigram());
      ideal[i] = getCatalogValueVector(incomingCall);
      i++;
    }

    MLDataSet trainingData = new BasicMLDataSet(input, ideal);
    ResilientPropagation train = new ResilientPropagation(network, trainingData);
    train.setThreadCount(16);

    do {
      train.iteration();
      System.out.println("Error: " + train.getError());
    } while (train.getError() > 0.01);

    train.finishTraining();
  }

  public void saveTrainedNetwork(File trainedNetwork) {
    saveObject(trainedNetwork, network);
  }

  protected abstract double[] getCatalogValueVector(IncomingCall incomingCall);
}
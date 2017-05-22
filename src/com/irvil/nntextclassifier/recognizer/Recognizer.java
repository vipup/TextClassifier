package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.Config;
import com.irvil.nntextclassifier.dao.CatalogDAO;
import com.irvil.nntextclassifier.dao.DAOFactory;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.io.File;
import java.util.List;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public abstract class Recognizer {
  private Config config = Config.getInstance();

  private final int inputLayerSize;
  private final int outputLayerSize;
  private final BasicNetwork network;
  private final CatalogDAO catalogDAO;

  Recognizer(CatalogDAO catalogDAO) {
    this.inputLayerSize = DAOFactory.vocabularyWordDAO(config.getDaoType(), config.getDBMSType()).getCount();
    this.outputLayerSize = catalogDAO.getCount();
    this.catalogDAO = catalogDAO;

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

  Recognizer(File trainedNetwork, CatalogDAO catalogDAO) {
    this.inputLayerSize = DAOFactory.vocabularyWordDAO(config.getDaoType(), config.getDBMSType()).getCount();
    this.outputLayerSize = catalogDAO.getCount();
    this.catalogDAO = catalogDAO;

    // load neural network from file
    this.network = (BasicNetwork) loadObject(trainedNetwork);
  }

  public Catalog recognize(IncomingCall incomingCall) {
    double[] output = new double[outputLayerSize];

    // calculate output vector
    network.compute(incomingCall.getTextAsWordVector(new FilteredUnigram()), output);
    Encog.getInstance().shutdown();

    // convert output vector to characteristic
    return catalogDAO.findByVector(output);
  }

  public void train() {
    List<IncomingCall> incomingCallsTrain = DAOFactory.incomingCallDAO(config.getDaoType(), config.getDBMSType()).getAll();

    // prepare input and ideal vectors
    // input <- IncomingCall text vector
    // ideal <- characteristic vector
    // todo: extract to other method
    //

    double[][] input = new double[incomingCallsTrain.size()][inputLayerSize];
    double[][] ideal = new double[incomingCallsTrain.size()][outputLayerSize];
    int i = 0;

    for (IncomingCall incomingCall : incomingCallsTrain) {
      input[i] = incomingCall.getTextAsWordVector(new FilteredUnigram());
      ideal[i] = getCatalogValueVector(incomingCall);
      i++;
    }

    // train
    //

    Propagation train = new ResilientPropagation(network, new BasicMLDataSet(input, ideal));
    train.setThreadCount(16);

    do {
      train.iteration();
      // todo: add observer
      //System.out.println("Error: " + train.getError());
    } while (train.getError() > 0.01);

    train.finishTraining();
  }

  public void saveTrainedRecognizer(File trainedNetwork) {
    saveObject(trainedNetwork, network);
  }

  protected abstract double[] getCatalogValueVector(IncomingCall incomingCall);
}
package com.irvil.nntextclassifier.recognizer;

import com.irvil.nntextclassifier.dao.GenericDAO;
import com.irvil.nntextclassifier.dao.factories.DAOFactory;
import com.irvil.nntextclassifier.model.Catalog;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;
import com.irvil.nntextclassifier.ngram.FilteredUnigram;
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

// todo: make Recognizer independent from DAOs
public abstract class Recognizer {
  private final DAOFactory daoFactory;
  private final int inputLayerSize;
  private final int outputLayerSize;
  private final BasicNetwork network;
  private final GenericDAO<Catalog> catalogDAO;

  Recognizer(GenericDAO<Catalog> catalogDAO, DAOFactory daoFactory) {
    this.daoFactory = daoFactory;
    this.inputLayerSize = daoFactory.vocabularyWordDAO().getCount();
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

  Recognizer(File trainedNetwork, GenericDAO<Catalog> catalogDAO, DAOFactory daoFactory) {
    this.daoFactory = daoFactory;
    this.inputLayerSize = daoFactory.vocabularyWordDAO().getCount();
    this.outputLayerSize = catalogDAO.getCount();
    this.catalogDAO = catalogDAO;

    // load neural network from file
    this.network = (BasicNetwork) loadObject(trainedNetwork);
  }

  public Catalog recognize(IncomingCall incomingCall) {
    double[] output = new double[outputLayerSize];

    // calculate output vector
    network.compute(getTextAsWordVector(incomingCall, new FilteredUnigram()), output);
    Encog.getInstance().shutdown();

    // convert output vector to characteristic
    return catalogDAO.findByVector(output);
  }

  public void train() {
    List<IncomingCall> incomingCallsTrain = daoFactory.incomingCallDAO().getAll();

    // prepare input and ideal vectors
    // input <- IncomingCall text vector
    // ideal <- characteristic vector
    // todo: extract to other method
    //

    double[][] input = new double[incomingCallsTrain.size()][inputLayerSize];
    double[][] ideal = new double[incomingCallsTrain.size()][outputLayerSize];
    int i = 0;

    for (IncomingCall incomingCall : incomingCallsTrain) {
      input[i] = getTextAsWordVector(incomingCall, new FilteredUnigram());
      ideal[i] = getCatalogValueAsVector(incomingCall);
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

  // example:
  // count = 5; id = 4;
  // vector = {0, 0, 0, 1, 0}
  private double[] getCatalogValueAsVector(IncomingCall incomingCall) {
    double[] vector = new double[outputLayerSize];
    vector[getCatalogId(incomingCall) - 1] = 1;

    return vector;
  }

  private double[] getTextAsWordVector(IncomingCall incomingCall, NGramStrategy nGram) {
    GenericDAO<VocabularyWord> vocabularyWordDAO = daoFactory.vocabularyWordDAO();
    double[] vector = new double[vocabularyWordDAO.getCount()];

    // convert text to nGram
    Set<String> uniqueValues = nGram.getNGram(incomingCall.getText());

    // create vector
    for (String word : uniqueValues) {
      VocabularyWord vw = vocabularyWordDAO.findByValue(word);

      if (vw != null) {
        vector[vw.getId() - 1] = 1;
      }
    }

    return vector;
  }

  protected abstract int getCatalogId(IncomingCall incomingCall);
}
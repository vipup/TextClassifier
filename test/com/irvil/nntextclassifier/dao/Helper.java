package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;

import java.util.*;

class Helper {
  static void fillStorageWithTestData(StorageCreator storageCreator, CharacteristicDAO characteristicDAO, IncomingCallDAO incomingCallDAO, VocabularyWordDAO vocabularyWordDAO) throws Exception {
    //storageCreator.createStorage();
    storageCreator.clearStorage();

    // fill Module characteristic
    //

    Set<CharacteristicValue> possibleValues = new LinkedHashSet<>();
    possibleValues.add(new CharacteristicValue("PM"));
    possibleValues.add(new CharacteristicValue("MM"));
    characteristicDAO.addCharacteristic(new Characteristic("Module", possibleValues));

    // fill Handler characteristic
    //

    possibleValues = new LinkedHashSet<>();
    possibleValues.add(new CharacteristicValue("User 1"));
    possibleValues.add(new CharacteristicValue("User 2"));
    possibleValues.add(new CharacteristicValue("User 3"));
    characteristicDAO.addCharacteristic(new Characteristic("Handler", possibleValues));

    // fill incoming calls
    //

    List<IncomingCall> incomingCalls = new ArrayList<>();

    Map<Characteristic, CharacteristicValue> characteristics1 = new HashMap<>();
    characteristics1.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics1.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));
    incomingCalls.add(new IncomingCall("text text", characteristics1));

    Map<Characteristic, CharacteristicValue> characteristics2 = new HashMap<>();
    characteristics2.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics2.put(new Characteristic("Handler"), new CharacteristicValue("User 2"));
    incomingCalls.add(new IncomingCall("text1 text1", characteristics2));

    Map<Characteristic, CharacteristicValue> characteristics3 = new HashMap<>();
    characteristics3.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics3.put(new Characteristic("Handler"), new CharacteristicValue("User 2"));
    incomingCalls.add(new IncomingCall("text1 text1", characteristics3));

    incomingCallDAO.addAll(incomingCalls);

    // fill vocabulary
    List<VocabularyWord> vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord("Test 1"));
    vocabulary.add(new VocabularyWord("Test 2"));

    vocabularyWordDAO.addAll(vocabulary);
  }
}
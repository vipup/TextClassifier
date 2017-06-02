package com.irvil.nntextclassifier.dao;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.IncomingCall;
import com.irvil.nntextclassifier.model.VocabularyWord;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class Helper {
  static void fillStorageWithTestData(StorageCreator storageCreator, CharacteristicDAO characteristicDAO, IncomingCallDAO incomingCallDAO, VocabularyWordDAO vocabularyWordDAO) throws Exception {
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

    Map<Characteristic, CharacteristicValue> characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));
    incomingCallDAO.add(new IncomingCall("text text", characteristics));

    characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 2"));
    incomingCallDAO.add(new IncomingCall("text1 text1", characteristics));

    characteristics = new HashMap<>();
    characteristics.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics.put(new Characteristic("Handler"), new CharacteristicValue("User 2"));
    incomingCallDAO.add(new IncomingCall("text1 text1", characteristics));

    // fill vocabulary

    vocabularyWordDAO.add(new VocabularyWord("Test 1"));
    vocabularyWordDAO.add(new VocabularyWord("Test 2"));
  }
}
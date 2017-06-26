package com.irvil.textclassifier.dao;

import com.irvil.textclassifier.model.Characteristic;
import com.irvil.textclassifier.model.CharacteristicValue;
import com.irvil.textclassifier.model.ClassifiableText;
import com.irvil.textclassifier.model.VocabularyWord;

import java.util.*;

class Helper {
  static void fillStorageWithTestData(StorageCreator storageCreator, CharacteristicDAO characteristicDAO, ClassifiableTextDAO classifiableTextDAO, VocabularyWordDAO vocabularyWordDAO) throws Exception {
    storageCreator.createStorage();
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

    // fill classifiable texts
    //

    List<ClassifiableText> classifiableTexts = new ArrayList<>();

    Map<Characteristic, CharacteristicValue> characteristics1 = new HashMap<>();
    characteristics1.put(new Characteristic("Module"), new CharacteristicValue("PM"));
    characteristics1.put(new Characteristic("Handler"), new CharacteristicValue("User 1"));
    classifiableTexts.add(new ClassifiableText("text text", characteristics1));

    Map<Characteristic, CharacteristicValue> characteristics2 = new HashMap<>();
    characteristics2.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics2.put(new Characteristic("Handler"), new CharacteristicValue("User 2"));
    classifiableTexts.add(new ClassifiableText("text1 text1", characteristics2));

    Map<Characteristic, CharacteristicValue> characteristics3 = new HashMap<>();
    characteristics3.put(new Characteristic("Module"), new CharacteristicValue("MM"));
    characteristics3.put(new Characteristic("Handler"), new CharacteristicValue("User 2"));
    classifiableTexts.add(new ClassifiableText("text1 text1", characteristics3));

    classifiableTextDAO.addAll(classifiableTexts);

    // fill vocabulary
    List<VocabularyWord> vocabulary = new ArrayList<>();
    vocabulary.add(new VocabularyWord("Test 1"));
    vocabulary.add(new VocabularyWord("Test 2"));

    vocabularyWordDAO.addAll(vocabulary);
  }
}
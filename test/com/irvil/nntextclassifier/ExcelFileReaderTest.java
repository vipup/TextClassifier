package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.ClassifiableText;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExcelFileReaderTest {
  private final ExcelFileReader excelFileReader = new ExcelFileReader();

  @Test
  public void xlsxToClassifiableTexts() throws Exception {
    List<ClassifiableText> classifiableTexts = excelFileReader.xlsxToClassifiableTexts(new File("./test_db/test.xlsx"), 1);

    assertEquals(classifiableTexts.size(), 3);

    assertEquals(classifiableTexts.get(0).getText(), "test1 test1");
    assertEquals(classifiableTexts.get(0).getCharacteristics().size(), 2);
    assertEquals(classifiableTexts.get(0).getCharacteristics().get(new Characteristic("Characteristic 1")).getValue(), "val1");
    assertEquals(classifiableTexts.get(0).getCharacteristics().get(new Characteristic("Characteristic 2")).getValue(), "val3");

    assertEquals(classifiableTexts.get(1).getText(), "test2 test2");
    assertEquals(classifiableTexts.get(1).getCharacteristics().size(), 2);
    assertEquals(classifiableTexts.get(1).getCharacteristics().get(new Characteristic("Characteristic 1")).getValue(), "val2");
    assertEquals(classifiableTexts.get(1).getCharacteristics().get(new Characteristic("Characteristic 2")).getValue(), "val4");

    assertEquals(classifiableTexts.get(2).getText(), "test3 test3");
    assertEquals(classifiableTexts.get(2).getCharacteristics().size(), 2);
    assertEquals(classifiableTexts.get(2).getCharacteristics().get(new Characteristic("Characteristic 1")).getValue(), "val5");
    assertEquals(classifiableTexts.get(2).getCharacteristics().get(new Characteristic("Characteristic 2")).getValue(), "val6");
  }

  @Test(expected = FileNotFoundException.class)
  public void xlsxToClassifiableTextsNotExistingFile() throws Exception {
    excelFileReader.xlsxToClassifiableTexts(new File("./test_db/test1.xlsx"), 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void xlsxToClassifiableTextsNotExistingFile1() throws Exception {
    excelFileReader.xlsxToClassifiableTexts(null, 1);
  }
}
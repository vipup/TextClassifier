package com.irvil.nntextclassifier;

import com.irvil.nntextclassifier.model.Characteristic;
import com.irvil.nntextclassifier.model.CharacteristicValue;
import com.irvil.nntextclassifier.model.ClassifiableText;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// todo: add test
class ExcelFileReader {
  List<ClassifiableText> xlsxToClassifiableTexts(File xlsxFile) throws IOException {
    List<ClassifiableText> classifiableTexts = new ArrayList<>();

    try (XSSFWorkbook excelFile = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
      XSSFSheet sheet = excelFile.getSheetAt(0);

      // create Characteristics catalog
      // first row contains Characteristics names from second to last columns
      //

      List<Characteristic> characteristics = new ArrayList<>();

      for (int i = 1; i < sheet.getRow(0).getLastCellNum(); i++) {
        characteristics.add(new Characteristic(sheet.getRow(0).getCell(i).getStringCellValue()));
      }

      // fill ClassifiableTexts
      // start from second row
      //

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Map<Characteristic, CharacteristicValue> characteristicsValues = new HashMap<>();

        for (int j = 1; j < sheet.getRow(i).getLastCellNum(); j++) {
          characteristicsValues.put(characteristics.get(j - 1), new CharacteristicValue(sheet.getRow(i).getCell(j).getStringCellValue()));
        }

        // exclude empty rows
        if (!sheet.getRow(i).getCell(0).getStringCellValue().equals("")) {
          classifiableTexts.add(new ClassifiableText(sheet.getRow(i).getCell(0).getStringCellValue(), characteristicsValues));
        }
      }

      return classifiableTexts;
    }
  }
}

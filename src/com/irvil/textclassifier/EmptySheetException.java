package com.irvil.textclassifier;

class EmptySheetException extends Exception {
  @Override
  public String getMessage() {
    return "Excel sheet is empty";
  }
}

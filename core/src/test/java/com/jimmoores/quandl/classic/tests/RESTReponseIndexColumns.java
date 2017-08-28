package com.jimmoores.quandl.classic.tests;

/**
 * Contains common constants used by classes that load and save Quandl request/response data for unit testing purposes.
 */
public enum RESTReponseIndexColumns {
  /**
   * URI column in CSV index file.
   */
  URI("URI", 0),
  /**
   * File name in CSV index file.
   */
  FILE("File", 1),
  /**
   * Exception class column in CSV index file.
   */
  EXCEPTION_CLASS("Exception Class", 2),
  /**
   * Index of Exception message column in CSV index file.
   */
  EXCEPTION_MESSAGE("Exception Message", 3);
  
  private String _columnLabel;
  private int _columnIndex;

  /**
   * 
   * @param columnLabel the label of the column
   * @param columnIndex the index of the column
   */
  RESTReponseIndexColumns(final String columnLabel, final int columnIndex) {
    _columnLabel = columnLabel;
    _columnIndex = columnIndex;
  }
  
  /**
   * Get the label name of this column (the string in the first row of the CSV file).
   * @return the label, not null
   */
  String getColumnLabel() {
    return _columnLabel;
  }
  
  /**
   * Get the index of this column (zero-based).
   * @return the index
   */
  int getColumnIndex() {
    return _columnIndex;
  }
}

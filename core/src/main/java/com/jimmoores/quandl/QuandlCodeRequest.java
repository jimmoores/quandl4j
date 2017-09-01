package com.jimmoores.quandl;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * Class for representing a request for either a single column for a given Quandl code, or a request for all columns for a given Quandl
 * code. It is passed in as part of a MultiDataSetRequest to QuandlConnector.getMultipleDataSets().
 */
public final class QuandlCodeRequest {
  private final String _quandlCode;
  private final Integer _columnNumber;

  private QuandlCodeRequest(final String quandlCode, final Integer columnNumber) {
    _quandlCode = quandlCode;
    _columnNumber = columnNumber;
  }

  /**
   * Request just a single column for a given quandlCode.
   * 
   * @param quandlCode the Quandl code you're interested in, not null
   * @param columnNumber the column number (determined by meta-data or a single request) of the data you want, not null
   * @return an request instance, not null
   */
  public static QuandlCodeRequest singleColumn(final String quandlCode, final int columnNumber) {
    ArgumentChecker.notNull(quandlCode, "quandlCode");
    return new QuandlCodeRequest(quandlCode, columnNumber);
  }

  /**
   * Request all columns for a given quandlCode.
   * 
   * @param quandlCode the Quandl code you're interested in, not null
   * @return an request instance, not null
   */
  public static QuandlCodeRequest allColumns(final String quandlCode) {
    ArgumentChecker.notNull(quandlCode, "quandlCode");
    return new QuandlCodeRequest(quandlCode, null);
  }

  /**
   * @return the quandl code, not null
   */
  public String getQuandlCode() {
    return _quandlCode;
  }

  /**
   * @return the column number requested, or null if all columns were requested.
   */
  public Integer getColumnNumber() {
    return _columnNumber;
  }

  /**
   * @return true if the request is for a single column
   */
  public boolean isSingleColumnRequest() {
    return _columnNumber != null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (_columnNumber != null) {
      result = prime * result + _columnNumber.hashCode();
    }
    result = prime * result + _quandlCode.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof QuandlCodeRequest)) {
      return false;
    }
    QuandlCodeRequest other = (QuandlCodeRequest) obj;
    if (!_quandlCode.equals(other._quandlCode)) {
      return false;
    }
    if (_columnNumber == null) {
      if (other._columnNumber != null) {
        return false;
      }
    } else if (!_columnNumber.equals(other._columnNumber)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    if (isSingleColumnRequest()) {
      return "QuandlCodeRequest[quandlCode=" + _quandlCode + ", columnNumber=" + _columnNumber + "]";
    } else {
      return "QuandlCodeRequest[quandlCode=" + _quandlCode + ", all columns]";
    }
  }

}

package com.jimmoores.quandl;

/**
 * An enumerated type representing the required result ordering from a Quandl REST API request.
 */
public enum SortOrder {
  /**
   * Ascending order.
   */
  ASCENDING("asc"),
  /**
   * Descending order.
   */
  DESCENDING("desc");

  private String _quandlString;

  /**
   * Enum constructor allowing the specification of the appropriate Quandl API string to be passed to the REST request.
   * 
   * @param quandlString
   *          the string to pass to the Quandl REST API
   */
  SortOrder(final String quandlString) {
    _quandlString = quandlString;
  }

  /**
   * Get the string value to pass to the Quandl REST API.
   * 
   * @return the string to pass to the Quandl REST API for this enum value
   */
  String getQuandlString() {
    return _quandlString;
  }
}

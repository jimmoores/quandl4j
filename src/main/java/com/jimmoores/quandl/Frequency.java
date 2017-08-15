package com.jimmoores.quandl;

/**
 * Enumerated type representing the frequency of data being requested.
 */
public enum Frequency {
  /**
   * Use underlying data frequency.
   */
  NONE("none"),
  /**
   * Daily sampling.
   */
  DAILY("daily"),
  /**
   * Weekly sampling.
   */
  WEEKLY("weekly"),
  /**
   * Monthly sampling.
   */
  MONTHLY("monthly"),
  /**
   * Quarterly sampling.
   */
  QUARTERLY("quarterly"),
  /**
   * Annual sampling.
   */
  ANNUAL("annual");

  private String _quandlString;

  /**
   * Constructor to augment enum with correct quandl API string.
   * 
   * @param quandlString
   *          string to pass to Quandl REST API
   */
  Frequency(final String quandlString) {
    // TODO: null check
    _quandlString = quandlString;
  }

  /**
   * Get the string to pass to the Quandl REST API associated with this enum.
   * 
   * @return the correct string to pass to the Quandl REST API, not null.
   */
  String getQuandlString() {
    return _quandlString;
  }
}

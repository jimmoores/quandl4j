package com.jimmoores.quandl;

/**
 * Enumerated type to represent a server-side pre-processing transformation of the source data.
 */
public enum Transform {
  /**
   * Leave data unchanged.
   */
  NONE("none"),
  /**
   * Row-on-row change.
   * y'[t] = y[t] - y[t-1]
   */
  DIFF("diff"),
  /**
   * Row-on-row % change.
   * y'[t] = (y[t] - y[y-1]) / y[t-1]
   */
  RDIFF("rdiff"),
  /**
   * Cumulative sum.
   * y'[t] = y[t] + y[t-1] + ... + y[0]
   */
  CUMUL("cumul"),
  /**
   * Start at 100.
   * y'[t] = (y[t] / y[0]) * 100
   */
  NORMALIZE("normalize");
  
  private String _quandlString;
  
  /**
   * Create a transform enum with the appropriate quandl REST parameter string embedded.
   * @param quandlString the Quandl REST API string
   */
  Transform(final String quandlString) {
    _quandlString = quandlString;
  }
  
  /**
   * Get the Quandl REST API string for this enum.
   * @return the string expected by the Quandl REST API
   */
  String getQuandlString() {
    return _quandlString;
  }
}

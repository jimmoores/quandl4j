package com.jimmoores.quandl.example;

import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;

/**
 * Example 4.
 */
public final class Example4 {
  /**
   * Private default constructor.
   */
  private Example4() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    MetaDataResult metaData = session.getMetaData(MetaDataRequest.of("WIKI/AAPL"));
    System.out.println(metaData.toPrettyPrintedString());
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example4 example = new Example4();
    example.run();
  }
}

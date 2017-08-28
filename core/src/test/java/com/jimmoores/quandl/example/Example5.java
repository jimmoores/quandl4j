package com.jimmoores.quandl.example;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.QuandlSession;

/**
 * @deprecated this example covers the deprecated API.
 * Example 5.
 */
public final class Example5 {
  /**
   * Private default constructor.
   */
  private Example5() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    MetaDataResult metaData = session.getMetaData(MultiMetaDataRequest.of("WIKI/AAPL", "DOE/RWTC", "WIKI/MSFT"));
    System.out.println(metaData.toPrettyPrintedString());
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example5 example = new Example5();
    example.run();
  }
}

package com.jimmoores.quandl.example;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.v2.GenericQuandlSessionInterface;

/**
 * Example 1.
 */
public final class Example1 {
  /**
   * Private default constructor.
   */
  private Example1() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    TabularResult tabularResult = session.getDataSet(
        DataSetRequest.Builder.of("WIKI/AAPL").build());
    System.out.println(tabularResult.toPrettyPrintedString());
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example1 example = new Example1();
    example.run();
  }
}

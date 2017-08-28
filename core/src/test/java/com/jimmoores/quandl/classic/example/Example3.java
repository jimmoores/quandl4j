package com.jimmoores.quandl.classic.example;

import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.classic.ClassicQuandlSession;

/**
 * Example 3.
 */
public final class Example3 {
  /**
   * Private default constructor.
   */
  private Example3() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    ClassicQuandlSession session = ClassicQuandlSession.create();
    MetaDataResult metaData = session.getMetaData(MetaDataRequest.of("WIKI/AAPL"));
    System.out.println(metaData.toPrettyPrintedString());
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example3 example = new Example3();
    example.run();
  }
}

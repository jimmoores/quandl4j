package com.jimmoores.quandl.example;

import java.util.Map;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.util.PrettyPrinter;

/**
 * @deprecated this example covers the deprecated API.
 * Example 6.
 */
public final class Example6 {

  /**
   * Private default constructor.
   */
  private Example6() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    Map<String, HeaderDefinition> headers = session.getMultipleHeaderDefinition(MultiMetaDataRequest.of("WIKI/AAPL", "DOE/RWTC", "WIKI/MSFT"));
    System.out.println(PrettyPrinter.toPrettyPrintedString(headers));
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example6 example = new Example6();
    example.run();
  }
}

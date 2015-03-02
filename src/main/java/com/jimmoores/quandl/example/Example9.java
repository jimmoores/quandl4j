package com.jimmoores.quandl.example;

import com.jimmoores.quandl.*;
import com.jimmoores.quandl.caching.RetentionPolicy;

import java.io.FileNotFoundException;

/**
 * Example 8.
 * Demonstrates caching
 */
public final class Example9
{

  /**
   * Private default constructor.
   */
  private Example9() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    SessionOptions sessionOptions = SessionOptions.Builder.withoutAuthToken()
      .withCacheDir(".")
      .withDefaultRetentionPolicy(RetentionPolicy.Day)
      .build();
    QuandlSession session = QuandlSession.create(sessionOptions);
    TabularResult tabularResult = null;
    try
    {
      tabularResult = session.getDataSet(
        DataSetRequest.Builder.of("WIKI/AAPL").build());
      System.out.println(tabularResult.toPrettyPrintedString());
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example9 example = new Example9();
    example.run();
  }
}

package com.jimmoores.quandl.example;

import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;

/**
 * Example 7.
 */
public final class Example7 {

  /**
   * Private default constructor.
   */
  private Example7() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    SearchResult searchResult = session.search(new SearchRequest.Builder().withQuery("Apple").withMaxPerPage(2).build());
    System.out.println(searchResult.toPrettyPrintedString());
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example7 example = new Example7();
    example.run();
  }
}

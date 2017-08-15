package com.jimmoores.quandl.example;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.v2.GenericQuandlSessionInterface;

/**
 * Example 8.
 */
public final class Example8 {

  /**
   * Private default constructor.
   */
  private Example8() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    SearchResult searchResult = session.search(new SearchRequest.Builder().withQuery("Apple").withMaxPerPage(2).build());
    System.out.println("Current page:" + searchResult.getCurrentPage());
    System.out.println("Documents per page:" + searchResult.getDocumentsPerPage());
    System.out.println("Total matching documents:" + searchResult.getTotalDocuments());
    for (MetaDataResult document : searchResult.getMetaDataResultList()) {
      System.out.println("Quandl code " + document.getQuandlCode() + " matched");
      System.out.println("Available columns are: " + document.getHeaderDefinition());
    }
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example8 example = new Example8();
    example.run();
  }
}

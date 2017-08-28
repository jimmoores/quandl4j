package com.jimmoores.quandl.classic.example;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.classic.ClassicQuandlSession;

/**
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
    ClassicQuandlSession session = ClassicQuandlSession.create();
    SearchResult searchResult = session.search(SearchRequest.Builder.ofQuery("Apple").withMaxPerPage(2).build());
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
    Example5 example = new Example5();
    example.run();
  }
}

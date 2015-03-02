package main.java.com.jimmoores.quandl.example;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;

/**
 * Example 8.
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
    QuandlSession session = QuandlSession.create();
    SearchResult searchResult = session.search(SearchRequest.Builder.of("Apple").withMaxPerPage(2).build());
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
    com.jimmoores.quandl.example.Example8 example = new com.jimmoores.quandl.example.Example8();
    example.run();
  }
}
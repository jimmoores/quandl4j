package com.jimmoores.quandl.classic.example;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.Transform;
import com.jimmoores.quandl.classic.ClassicQuandlSession;
import com.jimmoores.quandl.util.PrettyPrinter;

/**
 * Demo using Quandl library.
 */
public final class Demo {
  private static final int CLOSE_COLUMN = 4;

  /**
   * Private default constructor.
   */
  private Demo() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    ClassicQuandlSession quandl = ClassicQuandlSession.create();
    SearchResult searchResult = quandl.search(SearchRequest.Builder.ofQuery("Apple").withMaxPerPage(2).build());
    System.out.println(searchResult.toPrettyPrintedString());
    for (MetaDataResult metaData : searchResult.getMetaDataResultList()) {
      System.out.println(PrettyPrinter.toPrettyPrintedString(metaData.getRawJSON()));
    }
    TabularResult tabularResult = null;

    tabularResult = quandl.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL").withFrequency(Frequency.QUARTERLY)
                                                                          .withColumn(CLOSE_COLUMN).withTransform(Transform.NORMALIZE).build());
    System.out.println(PrettyPrinter.toPrettyPrintedString(tabularResult));
    MetaDataResult metaData = quandl.getMetaData(MetaDataRequest.of("WIKI/AAPL"));
    System.out.println(PrettyPrinter.toPrettyPrintedString(metaData.getRawJSON()));
  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Demo demo = new Demo();
    demo.run();
  }
}

package com.jimmoores.quandl.example;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.QuandlCodeRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.Transform;
import com.jimmoores.quandl.util.PrettyPrinter;

/**
 * Demo using Quandl library.
 */
public final class Demo {
  private static final int CLOSE_COLUMN = 4;
  private static final LocalDate RECENTISH_DATE = LocalDate.of(2013, 1, 1);

  /**
   * Private default constructor.
   */
  private Demo() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession quandl = QuandlSession.create();
    SearchResult searchResult = quandl.search(SearchRequest.Builder.of("Apple").withMaxPerPage(2).build());
    System.out.println(searchResult.toPrettyPrintedString());
    for (MetaDataResult metaData : searchResult.getMetaDataResultList()) {
      System.out.println(PrettyPrinter.toPrettyPrintedString(metaData.getRawJSON()));
    }
    TabularResult tabularResult = null;

    tabularResult = quandl.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL").withFrequency(Frequency.QUARTERLY)
                                                                          .withColumn(CLOSE_COLUMN).withTransform(Transform.NORMALIZE).build());
    System.out.println(PrettyPrinter.toPrettyPrintedString(tabularResult));
    @SuppressWarnings("deprecation")
    TabularResult tabularResultMulti = quandl.getDataSets(
                                       MultiDataSetRequest.Builder.of(
                                         QuandlCodeRequest.allColumns("WIKI/AAPL"), 
                                         QuandlCodeRequest.allColumns("DOE/RWTC")
                                       ).withStartDate(RECENTISH_DATE)
                                       .build());
    System.out.println(PrettyPrinter.toPrettyPrintedString(tabularResultMulti));
    MetaDataResult metaData = quandl.getMetaData(MetaDataRequest.of("WIKI/AAPL"));
    System.out.println(PrettyPrinter.toPrettyPrintedString(metaData.getRawJSON()));
    MetaDataResult metaData2 = quandl.getMetaData(MultiMetaDataRequest.of("WIKI/AAPL", "DOE/RWTC", "WIKI/MSFT"));
    System.out.println(PrettyPrinter.toPrettyPrintedString(metaData2.getRawJSON()));
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

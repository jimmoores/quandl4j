package com.jimmoores.quandl.example;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.QuandlCodeRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;

/**
 * Example 3.
 */
public final class Example3 {
  private static final int CLOSE_COLUMN = 4;
  private static final LocalDate RECENTISH_DATE = LocalDate.of(2013, 1, 1);

  /**
   * Private default constructor.
   */
  private Example3() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    @SuppressWarnings("deprecation")
    TabularResult tabularResultMulti = session.getDataSets(
        MultiDataSetRequest.Builder
          .of(
            QuandlCodeRequest.singleColumn("WIKI/AAPL", CLOSE_COLUMN),
            QuandlCodeRequest.allColumns("DOE/RWTC")
          )
          .withStartDate(RECENTISH_DATE)
          .withFrequency(Frequency.MONTHLY)
          .build());
    System.out.println(tabularResultMulti.toPrettyPrintedString());
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

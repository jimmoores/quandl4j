package com.jimmoores.quandl.example;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.QuandlCodeRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;

/**
 * Example 3a.
 */
public final class Example3a {
  private static final int CLOSE_COLUMN = 4;
  private static final LocalDate RECENTISH_DATE = LocalDate.of(2013, 1, 1);

  /**
   * Private default constructor.
   */
  private Example3a() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    QuandlSession session = QuandlSession.create();
    TabularResult tabularResultMulti = null;
    try
    {
      tabularResultMulti = session.getDataSets(
          MultiDataSetRequest.Builder
            .of(
              QuandlCodeRequest.singleColumn("WIKI/AAPL", CLOSE_COLUMN),
              QuandlCodeRequest.allColumns("DOE/RWTC")
            )
            .withStartDate(RECENTISH_DATE)
            .withFrequency(Frequency.MONTHLY)
            .build());
      System.out.println("Header definition: " + tabularResultMulti.getHeaderDefinition());
      Iterator<Row> iter = tabularResultMulti.iterator();
      while (iter.hasNext()) {
        Row row = iter.next();
        LocalDate date = row.getLocalDate("Date");
        Double value = row.getDouble("DOE/RWTC - Value");
        System.out.println("Value on date " + date + " was " + value);
      }
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
    Example3a example = new Example3a();
    example.run();
  }
}

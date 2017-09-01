package com.jimmoores.quandl.classic.example;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.classic.ClassicQuandlSession;

/**
 * Example 3.
 */
public final class Example3 {
  private static final LocalDate RECENTISH_DATE = LocalDate.of(2017, 06, 01);

  /**
   * Private default constructor.
   */
  private Example3() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    ClassicQuandlSession session = ClassicQuandlSession.create();
    TabularResult tabularResult = session.getDataSet(
        DataSetRequest.Builder
          .of("SSE/VROS") // VERIANOS REAL ESTATE AG on Boerse Stuttgart
          .withColumn(3) // Last (looked up previously)
          .withStartDate(RECENTISH_DATE)
          .withFrequency(Frequency.MONTHLY)
          .build());
    System.out.println("Header definition: " + tabularResult.getHeaderDefinition());
    for (final Row row : tabularResult) {
      LocalDate date = row.getLocalDate("Date");
      Double value = row.getDouble("Last");
      System.out.println("Value on date " + date + " was " + value);
    } 
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

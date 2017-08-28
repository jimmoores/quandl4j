package com.jimmoores.quandl.classic.example;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.Transform;
import com.jimmoores.quandl.classic.ClassicQuandlSession;

/**
 * Example 2.
 */
public final class Example2 {
  private static final int CLOSE_COLUMN = 4;

  /**
   * Private default constructor.
   */
  private Example2() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    ClassicQuandlSession session = ClassicQuandlSession.create();
    TabularResult tabularResult = session.getDataSet(
        DataSetRequest.Builder
          .of("WIKI/AAPL")
          .withFrequency(Frequency.QUARTERLY)
          .withColumn(CLOSE_COLUMN)
          .withTransform(Transform.NORMALIZE)
          .build());
    System.out.println(tabularResult.toPrettyPrintedString());  }

  /**
   * Main entry point.
   * @param args command line arguments
   */
  public static void main(final String[] args) {
    Example2 example = new Example2();
    example.run();
  }
}

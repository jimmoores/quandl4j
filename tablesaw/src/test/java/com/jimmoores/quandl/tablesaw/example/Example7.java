package com.jimmoores.quandl.tablesaw.example;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.tablesaw.TableSawQuandlSession;

import tech.tablesaw.api.Table;

/**
 * Example 7
 */
public class Example7 {
  /**
   * Private default constructor.
   */
  private Example7() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    TableSawQuandlSession session = TableSawQuandlSession.create();
    Table table = session.getDataSet(
        DataSetRequest.Builder.of("WIKI/AAPL").withMaxRows(10).build());
    System.out.println(table);
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

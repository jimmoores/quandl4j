package com.jimmoores.quandl.tablesaw.example;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.tablesaw.TableSawQuandlSession;

import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Bar;

/**
 * Example 7
 */
public class Example8 {
  /**
   * Private default constructor.
   */
  private Example8() {
  }
 
  /**
   * The main body of the code.
   */
  private void run() {
    TableSawQuandlSession session = TableSawQuandlSession.create();
    Table table = session.getDataSet(
        DataSetRequest.Builder.of("WIKI/AAPL").build());
    // Create a new column containing the year
    ShortColumn yearColumn = table.dateColumn("Date").year();
    yearColumn.setName("Year");
    table.addColumn(yearColumn);
    // Create max, min and total volume tables aggregated by year
    Table summaryMax = table.max("Close").by("year");
    Table summaryMin = table.min("Close").by("year");
    Table summaryVolume = table.sum("Volume").by("year");
    // Create a new table from each of these
    Table summary = Table.create("Summary", summaryMax.column(0), summaryMax.column(1), summaryMin.column(1), summaryVolume.column(1));
    // Show the max close price as a graph.
    try {
      Bar.show("Max Close Price by year", summary.shortColumn("Year"), summary.numericColumn(1));
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(summary);
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

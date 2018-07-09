package com.jimmoores.quandl.tablesaw.example;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.tablesaw.TableSawQuandlSession;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Bar;

/**
 * Example 8
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
    NumberColumn yearColumn = table.dateColumn("Date").year();
    yearColumn.setName("Year");
    table.addColumns(yearColumn);
    // Create max, min and total volume tables aggregated by year
    Table summaryMax = table.summarize("Close", max).by("year");
    Table summaryMin = table.summarize("Close", min).by("year");
    Table summaryVolume = table.summarize("Volume", sum).by("year");
    // Create a new table from each of these
    Table summary = Table.create("Summary", summaryMax.column(0), summaryMax.column(1), summaryMin.column(1), summaryVolume.column(1));
    // Show the max close price as a graph.
    try {
      Bar.show("Max Close Price by year", summary.numberColumn("Year"), summary.numberColumn(1));
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

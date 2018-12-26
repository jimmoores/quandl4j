package com.jimmoores.quandl.tablesaw.example;

import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.tablesaw.TableSawQuandlSession;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.BarTrace;

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
    IntColumn yearColumn = table.dateColumn("Date").year();
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
      Plot.show(new Figure(BarTrace.builder(summary.intColumn("Year"), summary.numberColumn(1)).build()));
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

package com.jimmoores.quandl.processing.classic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.processing.ResponseProcessor;
import com.jimmoores.quandl.util.QuandlRuntimeException;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * ReponseProcessor to process an input stream reply from a query into
 * a TabularResult object.
 */
public class TabularResultResponseProcessor implements ResponseProcessor<TabularResult> {
  /**
   * {@inheritDoc}
   */
  public TabularResult process(final InputStream inputStream, final Request request) {
    CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
    try {
      String[] headerRow = reader.readNext();
      if (headerRow != null) {
        HeaderDefinition headerDef = HeaderDefinition.of(Arrays.asList(headerRow));
        List<Row> rows = new ArrayList<Row>();
        String[] next = reader.readNext();
        while (next != null) {
          if (next.length > headerRow.length) {
            // This row is not the same length as the header row, record how long it is so we can patch in a longer header afterwards.
            String[] stretchedHeaderRow = new String[next.length];
            System.arraycopy(headerRow, 0, stretchedHeaderRow, 0, headerRow.length);
            for (int i = headerRow.length; i < next.length; i++) {
              stretchedHeaderRow[i] = "Column " + i;
            }
            headerRow = stretchedHeaderRow;
            headerDef = HeaderDefinition.of(Arrays.asList(headerRow)); // create a new header with the extended column labels.
            // NOTE: we DON'T go back and patch rows that we've already created. This is because the only case the header is used is
            // to look up rows by name, and given those rows don't contain data for those columns, the logic in Row now just returns
            // null in that case (the case where you ask for a row that isn't present).
          }
          Row row = Row.of(headerDef, next);
          rows.add(row);
          next = reader.readNext();
        }
        reader.close();
        return TabularResult.of(headerDef, rows);
      } else {
        reader.close();
        throw new QuandlRuntimeException("No data returned");
      }
    } catch (IOException | CsvValidationException e) {
      throw new QuandlRuntimeException("Error reading input stream", e);
    }
  }
}

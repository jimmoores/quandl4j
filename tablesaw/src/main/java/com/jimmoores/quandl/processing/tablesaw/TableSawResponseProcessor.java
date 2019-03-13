package com.jimmoores.quandl.processing.tablesaw;

import java.io.IOException;
import java.io.InputStream;

import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.processing.ResponseProcessor;
import com.jimmoores.quandl.processing.TitleRequestProcessor;
import com.jimmoores.quandl.util.QuandlRuntimeException;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TableSawResponseProcessor implements ResponseProcessor<Table> {

  private static final TitleRequestProcessor TITLE_REQUEST_PROCESSOR = new TitleRequestProcessor();
  
  public Table process(InputStream inputStream, Request request) {
    try {
      String name = null;
      if (request != null) {
        name = request.accept(TITLE_REQUEST_PROCESSOR);
      }
      return Table.read().usingOptions(CsvReadOptions.builder(inputStream).tableName(name));
    } catch (IOException ioe) {
      throw new QuandlRuntimeException("Error reading input stream", ioe);
    }
  }
}

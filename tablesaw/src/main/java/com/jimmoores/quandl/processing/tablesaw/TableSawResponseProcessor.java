package com.jimmoores.quandl.processing.tablesaw;

import java.io.IOException;
import java.io.InputStream;

import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.processing.ResponseProcessor;
import com.jimmoores.quandl.processing.TitleRequestProcessor;
import com.jimmoores.quandl.util.QuandlRuntimeException;

import tech.tablesaw.api.Table;

public class TableSawResponseProcessor implements ResponseProcessor<Table> {
  private static final String DEFAULT_NAME = "";
  private static final TitleRequestProcessor TITLE_REQUEST_PROCESSOR = new TitleRequestProcessor();
  
  public Table process(InputStream inputStream, Request request) {
    try {
      String name;
      if (request != null) {
        name = request.accept(TITLE_REQUEST_PROCESSOR);
      } else {
        name = DEFAULT_NAME;
      }
      return Table.read().csv(inputStream, name);
    } catch (IOException ioe) {
      throw new QuandlRuntimeException("Error reading input stream", ioe);
    }
  }
}

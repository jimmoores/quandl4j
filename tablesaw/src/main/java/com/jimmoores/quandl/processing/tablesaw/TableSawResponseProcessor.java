package com.jimmoores.quandl.processing.tablesaw;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jimmoores.quandl.processing.ResponseProcessor;
import com.jimmoores.quandl.util.QuandlRuntimeException;

import tech.tablesaw.api.Table;

public class TableSawResponseProcessor implements ResponseProcessor<Table> {
  private static final String DEFAULT_NAME = "";
  public Table process(InputStream inputStream) {
    try {
      return Table.createFromReader(new InputStreamReader(inputStream), DEFAULT_NAME);
    } catch (IOException ioe) {
      throw new QuandlRuntimeException("Error reading input stream", ioe);
    }
  }
}

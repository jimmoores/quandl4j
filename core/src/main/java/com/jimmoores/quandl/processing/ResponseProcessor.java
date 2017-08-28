package com.jimmoores.quandl.processing;

import java.io.InputStream;

/**
 * Interface for processing request data into appropriate object.
 * @param <T>  the type returned by the ReponseProcessor by processing the input stream
 */
public interface ResponseProcessor<T> {
  /**
   * Process the input stream into a response object.
   * @param inputStream  the input stream from the response
   * @param request  the request, which can be processed using a RequestProcessor to
   *                 e.g. obtain summary information
   * @return the response object
   */
  T process(InputStream inputStream, Request request);
}

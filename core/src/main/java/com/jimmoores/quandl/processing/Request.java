package com.jimmoores.quandl.processing;

/**
 * Interface implemented by all request classes containing visitor accept
 * method for RequestProcessors.  Also acts as a marker interface.
 */
public interface Request {
  /**
   * Process this request using the provided processor.
   * @param <T>  the type returned by the request processor
   * @param processor  the request processor
   * @return the result from the request processor
   */
  <T> T accept(RequestProcessor<T> processor);
}

package com.jimmoores.quandl.util;

/**
 * A runtime exception from the Quandl client indicating an HTTP 503 response code (Service unavailable). This generally indicates that the
 * server has a temporary problems.
 */
public class QuandlServiceUnavailableException extends QuandlRuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor when another exception is being included.
   * 
   * @param message a message describing the exception, not null
   * @param cause the cause of the expection if there is one, not null
   */
  public QuandlServiceUnavailableException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor when exception is not caused by an underlying exception.
   * 
   * @param message a message describing the exception, not null
   */
  public QuandlServiceUnavailableException(final String message) {
    super(message);
  }
}

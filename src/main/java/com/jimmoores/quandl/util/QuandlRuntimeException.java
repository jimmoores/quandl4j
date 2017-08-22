package com.jimmoores.quandl.util;

/**
 * A runtime exception from the Quandl client.
 */
public class QuandlRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor when another exception is being included.
   * 
   * @param message a message describing the exception, not null
   * @param cause the cause of the expection if there is one, not null
   */
  public QuandlRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor when exception is not caused by an underlying exception.
   * 
   * @param message a message describing the exception, not null
   */
  public QuandlRuntimeException(final String message) {
    super(message);
  }
}

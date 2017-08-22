package com.jimmoores.quandl.util;

/**
 * A runtime exception from the Quandl client indicating an HTTP 422 response code (Unprocessable Entity). This generally indicates that
 * some part of the request was nonsensical, e.g. asking for a column that doesn't exist.
 */
public class QuandlUnprocessableEntityException extends QuandlRuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor when another exception is being included.
   * 
   * @param message a message describing the exception, not null
   * @param cause the cause of the expection if there is one, not null
   */
  public QuandlUnprocessableEntityException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor when exception is not caused by an underlying exception.
   * 
   * @param message a message describing the exception, not null
   */
  public QuandlUnprocessableEntityException(final String message) {
    super(message);
  }
}

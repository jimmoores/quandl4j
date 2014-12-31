package com.jimmoores.quandl.util;

/**
 * A runtime exception from the Quandl client indicating an HTTP 429 response code (Too many requests).
 * This generally indicates that you've made more than your allowance of requests in that day.  A common
 * solution is to use a proper API auth token (available via your account login on Quandl) to pass in when
 * creating the QuandlSession.
 */
public class QuandlTooManyRequestsException extends QuandlRuntimeException {
  private static final long serialVersionUID = 1L;

  private Long _retryAfter;
  private Long _rateLimitLimit;
  private Long _rateLimitRemaining;
  /**
   * Constructor when another exception is being included.
   * @param message a message describing the exception, not null
   * @param cause the cause of the exception if there is one, not null
   */
  public QuandlTooManyRequestsException(final String message, final Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Constructor when exception is not caused by an underlying exception.
   * @param message a message describing the exception, not null
     */  
  public QuandlTooManyRequestsException(final String message) {
    super(message);
  }
  
  /**
   * Constructor when exception is not caused by an underlying exception.
   * @param message a message describing the exception, not null
   * @param retryAfter  the number of seconds the server has told the client to retry after, or null if not available
   * @param rateLimitLimit  the server reported total number of requests allowed in this session in total (presumably one day), or null if not available
   * @param rateLimitRemaining  the server reported remaining number of requests allowed in this session (presumably reset each day), or null if not available
   */  
  public QuandlTooManyRequestsException(final String message, final Long retryAfter, final Long rateLimitLimit, final Long rateLimitRemaining) {
    super(message);
    _retryAfter = retryAfter;
    _rateLimitLimit = rateLimitLimit;
    _rateLimitRemaining = rateLimitRemaining;
  }
  
  /**
   * Constructor when exception is caused by an underlying exception.
   * @param message a message describing the exception, not null
   * @param retryAfter  the number of seconds the server has told the client to retry after, or null if not available
   * @param rateLimitLimit  the server reported total number of requests allowed in this session in total (presumably one day), or null if not available
   * @param rateLimitRemaining  the server reported remaining number of requests allowed in this session (presumably reset each day), or null if not available
   * @param cause the cause of the exception if there is one, not null
   */  
  public QuandlTooManyRequestsException(final String message, final Long retryAfter, final Long rateLimitLimit, final Long rateLimitRemaining, Throwable cause) {
    super(message, cause);
    _retryAfter = retryAfter;
    _rateLimitLimit = rateLimitLimit;
    _rateLimitRemaining = rateLimitRemaining;
  }
  
  /**
   * @return the number of seconds to retry after, if provided by the server, or null if not provided.
   */
  public Long getRetryAfter() {
    return _retryAfter;
  }
  
  /**
   * @return the limit of the number of requests this client can make (presumably in a day).
   */
  public Long getRateLimitLimit() {
    return _rateLimitLimit;
  }
  
  /**
   * @return the remaining number of requests this client can make to the server (presumably reset daily).
   */
  public Long getRateLimitRemaining() {
    return _rateLimitRemaining;
  }
  
  /**
   * @return true, if the server has stated that the number of requests remaining for this client is &lt;= 0 or the server did not specify.
   */
  public boolean isDataExhausted() {
    return _rateLimitRemaining != null && _rateLimitRemaining <= 0L;
  }
}

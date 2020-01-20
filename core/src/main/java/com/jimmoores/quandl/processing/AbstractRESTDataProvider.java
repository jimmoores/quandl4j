package com.jimmoores.quandl.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.QuandlServiceUnavailableException;
import com.jimmoores.quandl.util.QuandlTooManyRequestsException;
import com.jimmoores.quandl.util.QuandlUnprocessableEntityException;

/**
 * Base class to create a specialised GenericRESTDataProvider with the 
 * invocation and reponse handling all taken care of in a protected method
 * getReponse.  The specialised methods can then concentrate on processing
 * the particular reponse type expected in each case.
 * @param <RAW_METADATA_TYPE>  the type used to hold the raw meta data.
 * @param <TABLE_TYPE>  the type used to hold tabular data.
 */
public abstract class AbstractRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE>
    implements GenericRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE> {
  private static final String RETRY_AFTER = "Retry-After";
  private static final String X_RATELIMIT_LIMIT = "X-RateLimit-Limit";
  private static final String X_RATELIMIT_REMAINING = "X-RateLimit-Remaining";

  private Long parseOptionalHeader(final Response response, final String field) {
    String value = response.getHeaderString(field);
    if (value != null) {
      try {
        return Long.valueOf(value);
      } catch (NumberFormatException nfe) {
      }
    }
    return null;
  }

  /**
   * Generic method to handle the invocation of the requests, error processing
   * and so on, leaving the specialised methods to simply invoke with the appropriate
   * ReponseProcessor to process the resulting InputStream.
   * @param <T>  the type of the response
   * @param target  the target
   * @param responseProcessor  the response processor that actually processes the target.
   * @param request  the request object (for request optional metadata) or null
   * @return the resulting response
   */
  protected <T> T getResponse(final WebTarget target, final ResponseProcessor<T> responseProcessor, final Request request) {
    Builder requestBuilder = target.request();

    try (Response response = requestBuilder.buildGet().invoke()) {
      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        InputStream inputStream = response.readEntity(InputStream.class);
        // should we be buffering this?
        try {
          T result = responseProcessor.process(inputStream, request);
          return result;
        } finally {
          try {
            inputStream.close();
          } catch (IOException ioe) {
            throw new UncheckedIOException("Problem closing input stream", ioe);
          }
        }
      } else if (response.getStatus() == UNPROCESSABLE_ENTITY) {
        String msg = "Response code to " + target.getUri() + " was " + response.getStatusInfo();
        throw new QuandlUnprocessableEntityException(msg);
      } else if (response.getStatus() == TOO_MANY_REQUESTS) {
        Long retryAfter = parseOptionalHeader(response, RETRY_AFTER);
        Long rateLimitLimit = parseOptionalHeader(response, X_RATELIMIT_LIMIT);
        Long rateLimitRemaining = parseOptionalHeader(response, X_RATELIMIT_REMAINING);
        String msg = "Response code to " + target.getUri() + " was " + response.getStatusInfo();
        throw new QuandlTooManyRequestsException(msg, retryAfter, rateLimitLimit, rateLimitRemaining);
      } else if (response.getStatus() == SERVICE_UNAVAILABLE) {
        String msg = "Response code to " + target.getUri() + " was 503 (Service Unavailable)";
        throw new QuandlServiceUnavailableException(msg);
      } else {
        String msg = "Response code to " + target.getUri() + " was " + response.getStatusInfo();
        throw new QuandlRuntimeException(msg);
      }      
    }
  }

  /**
   * This should be overriden and call getReponse with the appropriate ResponseProcessor.
   * {@inheritDoc}
   */
  public abstract RAW_METADATA_TYPE getJSONResponse(WebTarget target, Request request);
  
  /**
   * This should be overriden and call getReponse with the appropriate ResponseProcessor.
   * {@inheritDoc}
   */
  public abstract TABLE_TYPE getTabularResponse(WebTarget target, Request request);
}

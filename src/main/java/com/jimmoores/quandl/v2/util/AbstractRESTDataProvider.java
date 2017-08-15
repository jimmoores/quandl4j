package com.jimmoores.quandl.v2.util;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.QuandlServiceUnavailableException;
import com.jimmoores.quandl.util.QuandlTooManyRequestsException;
import com.jimmoores.quandl.util.QuandlUnprocessableEntityException;

public abstract class AbstractRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE>
    implements GenericRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE> {
  private static final String RETRY_AFTER = "Retry-After";
  private static final String X_RATELIMIT_LIMIT = "X-RateLimit-Limit";
  private static final String X_RATELIMIT_REMAINING = "X-RateLimit-Remaining";

  private Long parseOptionalHeader(Response response, String field) {
    String value = response.getHeaderString(field);
    if (value != null) {
      try {
        return Long.valueOf(value);
      } catch (NumberFormatException nfe) {
      }
    }
    return null;
  }

  protected <T> T getResponse(final WebTarget target, final ResponseProcessor<T> responseProcessor) {
    Builder requestBuilder = target.request();
    Response response = requestBuilder.buildGet().invoke();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      InputStream inputStream = response.readEntity(InputStream.class);
      // should we be buffering this?
      try {
        T result = responseProcessor.process(inputStream);
        response.close();
        inputStream.close();
        return result;
      } catch (IOException ex) {
        response.close();
        throw new QuandlRuntimeException("Problem closing input stream");
      } catch (RuntimeException t) {
        response.close();
        try {
          inputStream.close();
        } catch (IOException ioe) {
        }
        throw t;
      }
    } else if (response.getStatus() == UNPROCESSABLE_ENTITY) {
      String msg = "Response code to " + target.getUri() + " was " + response.getStatusInfo();
      response.close();
      throw new QuandlUnprocessableEntityException(msg);
    } else if (response.getStatus() == TOO_MANY_REQUESTS) {
      Long retryAfter = parseOptionalHeader(response, RETRY_AFTER);
      Long rateLimitLimit = parseOptionalHeader(response, X_RATELIMIT_LIMIT);
      Long rateLimitRemaining = parseOptionalHeader(response, X_RATELIMIT_REMAINING);
      String msg = "Response code to " + target.getUri() + " was " + response.getStatusInfo();
      response.close();
      throw new QuandlTooManyRequestsException(msg, retryAfter, rateLimitLimit, rateLimitRemaining);
    } else if (response.getStatus() == SERVICE_UNAVAILABLE) {
      String msg = "Response code to " + target.getUri() + " was 503 (Service Unavailable)";
      response.close();
      throw new QuandlServiceUnavailableException(msg);
    } else {
      String msg = "Response code to " + target.getUri() + " was " + response.getStatusInfo();
      response.close();
      throw new QuandlRuntimeException(msg);
    }
  }

  public abstract RAW_METADATA_TYPE getJSONResponse(WebTarget target);

  public abstract TABLE_TYPE getTabularResponse(WebTarget target);
}

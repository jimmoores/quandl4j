package com.jimmoores.quandl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.opencsv.CSVReader;

/**
 * Utility methods for fetching data from remote Quandl REST interface.
 * This is the default implementation that actually does a REST call, the other implementation is used for testing.
 */
public final class DefaultRESTDataProvider implements RESTDataProvider {
  private static final String RETRY_AFTER = "Retry-After";
  private static final String X_RATELIMIT_LIMIT = "X-RateLimit-Limit";
  private static final String X_RATELIMIT_REMAINING = "X-RateLimit-Remaining";
  
  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object.
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a CSV parsing problem or response code was unusual
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed JSON object
   */
  public JSONObject getJSONResponse(final WebTarget target) {
    Builder requestBuilder = target.request();
    Response response = requestBuilder.buildGet().invoke();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      InputStream inputStream = response.readEntity(InputStream.class);
      // should we be buffering this?
      JSONTokener tokeniser = new JSONTokener(new InputStreamReader(inputStream));
      try {
        JSONObject object = new JSONObject(tokeniser);
        response.close();
        inputStream.close();
        return object;
      } catch (JSONException jsone) {
        response.close();
        try {
           inputStream.close();
        } catch (IOException ioe) {
        }
        throw new QuandlRuntimeException("Problem parsing JSON reply", jsone);
      } catch (IOException ex) {
        response.close();
        throw new QuandlRuntimeException("Problem closing input stream");
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
  
  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV).
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a JSON parsing problem, network issue or response code was unusual
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed TabularResult
   */
  @SuppressWarnings("resource")
  public TabularResult getTabularResponse(final WebTarget target) {
    Builder requestBuilder = target.request();
    Response response = requestBuilder.buildGet().invoke();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      InputStream inputStream = response.readEntity(InputStream.class);
      // should we be buffering this?
      CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
      try {
        String[] headerRow = reader.readNext();
        if (headerRow != null) {
          HeaderDefinition headerDef = HeaderDefinition.of(Arrays.asList(headerRow));
          List<Row> rows = new ArrayList<Row>();
          String[] next = reader.readNext();
          while (next != null) {
            if (next.length > headerRow.length) {
                // This row is not the same length as the header row, record how long it is so we can patch in a longer header afterwards.
                String[] stretchedHeaderRow = new String[next.length];
                System.arraycopy(headerRow, 0,stretchedHeaderRow, 0, headerRow.length);
                for (int i = headerRow.length; i < next.length; i++) {
                    stretchedHeaderRow[i] = "Column " + i;
                }
                headerRow = stretchedHeaderRow;
                headerDef = HeaderDefinition.of(Arrays.asList(headerRow)); // create a new header with the extended column labels.
                // NOTE: we DON'T go back and patch rows that we've already created.  This is because the only case the header is used is 
                // to look up rows by name, and given those rows don't contain data for those columns, the logic in Row now just returns
                // null in that case (the case where you ask for a row that isn't present).
            }
            Row row = Row.of(headerDef, next);
            rows.add(row);
            next = reader.readNext();
          }
          reader.close();
          response.close();
          return TabularResult.of(headerDef, rows);
        } else {
          reader.close();
          response.close();
          throw new QuandlRuntimeException("No data returned");
        }
      } catch (IOException ex) {
        try {
            reader.close();
        } catch (IOException ex1) {
        }
        response.close();
        throw new QuandlRuntimeException("Problem reading result stream", ex);
      }
    }  else if (response.getStatus() == UNPROCESSABLE_ENTITY) {
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
}

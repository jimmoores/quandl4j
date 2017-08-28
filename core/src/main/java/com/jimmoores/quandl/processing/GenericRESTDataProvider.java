package com.jimmoores.quandl.processing;

import javax.ws.rs.client.WebTarget;

/**
 * An interface for abstracting the detail of getting a result object from the back-end, primarily to ease testing but also to remove code
 * duplication.
 * @param <RAW_METADATA_TYPE>  the type used to hold raw metadata
 * @param <TABLE_TYPE>  the type used to hold tabular data
 */
public interface GenericRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE> {
  /**
   * HTTP Response code returned in some cases, usually when a request is nonsensical, e.g. requesting a non-existent column.
   */
  int UNPROCESSABLE_ENTITY = 422;
  /**
   * HTTP Response code returned when the client has made too many request - it is an indication of rate limiting on the server-side.
   */
  int TOO_MANY_REQUESTS = 429; // response code returned in some cases
  /**
   * HTTP Response code returned when the service is unavailable - it is an indication of a probably temporary error or load problem on the
   * server.
   */
  int SERVICE_UNAVAILABLE = 503;

  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object. Throws a QuandlRuntimeException if there was a CSV
   * parsing problem or response code was not OK
   * 
   * @param target  the WebTarget describing the call to make, not null
   * @param request  the request object or null
   * @return the parsed JSON object
   */
  RAW_METADATA_TYPE getJSONResponse(WebTarget target, Request request);

  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV). Throws a QuandlRuntimeException if there was
   * a JSON parsing problem, network issue or response code was not OK
   * 
   * @param target  the WebTarget describing the call to make, not null
   * @param request  the request object or null
   * @return the parsed TabularResult
   */
  TABLE_TYPE getTabularResponse(WebTarget target, Request request);
}

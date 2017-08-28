package com.jimmoores.quandl.util;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.processing.AbstractRESTDataProvider;
import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.processing.classic.JSONTabularResultRESTDataProvider;

/**
 * @deprecated use JSONTabularResultRESTDataProvider for equivalent functionality, name became misleading with new data types. Utility
 *             methods for fetching data from remote Quandl REST interface. This is the default implementation that actually does a REST
 *             call, the other implementation is used for testing.
 */
public final class DefaultRESTDataProvider extends AbstractRESTDataProvider<JSONObject, TabularResult> implements RESTDataProvider {
  private JSONTabularResultRESTDataProvider _impl = new JSONTabularResultRESTDataProvider();

  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object. Throws a QuandlUnprocessableEntityException if
   * Quandl returned a response code that indicates a nonsensical request Throws a QuandlTooManyRequestsException if Quandl returned a
   * response code indicating the client had made too many requests Throws a QuandlRuntimeException if there was a CSV parsing problem or
   * response code was unusual
   * 
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed JSON object
   */
  public JSONObject getJSONResponse(final WebTarget target) {
    return _impl.getJSONResponse(target, null);
  }

  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV). Throws a QuandlUnprocessableEntityException
   * if Quandl returned a response code that indicates a nonsensical request Throws a QuandlTooManyRequestsException if Quandl returned a
   * response code indicating the client had made too many requests Throws a QuandlRuntimeException if there was a JSON parsing problem,
   * network issue or response code was unusual
   * 
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed TabularResult
   */
  public TabularResult getTabularResponse(final WebTarget target) {
    return _impl.getTabularResponse(target, null);
  }

  @Override
  public JSONObject getJSONResponse(final WebTarget target, final Request request) {
    return _impl.getJSONResponse(target, request);
  }

  @Override
  public TabularResult getTabularResponse(final WebTarget target, final Request request) {
    return _impl.getTabularResponse(target, request);
  }
}

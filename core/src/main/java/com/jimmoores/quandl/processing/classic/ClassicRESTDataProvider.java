package com.jimmoores.quandl.processing.classic;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.processing.GenericRESTDataProvider;
import com.jimmoores.quandl.processing.Request;

/**
 * Specialisation of generic rest data provider for new Classic API.
 */
public interface ClassicRESTDataProvider extends GenericRESTDataProvider<JSONObject, TabularResult> {
  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object. Throws a QuandlUnprocessableEntityException if
   * Quandl returned a response code that indicates a nonsensical request Throws a QuandlTooManyRequestsException if Quandl returned a
   * response code indicating the client had made too many requests Throws a QuandlRuntimeException if there was a CSV parsing problem or
   * response code was unusual
   * 
   * @param target the WebTarget describing the call to make, not null
   * @param request the request object or null
   * @return the parsed JSON object
   */
  JSONObject getJSONResponse(WebTarget target, Request request);

  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV). Throws a QuandlUnprocessableEntityException
   * if Quandl returned a response code that indicates a nonsensical request Throws a QuandlTooManyRequestsException if Quandl returned a
   * response code indicating the client had made too many requests Throws a QuandlRuntimeException if there was a JSON parsing problem,
   * network issue or response code was unusual
   * 
   * @param target the WebTarget describing the call to make, not null
   * @param request the request object or null
   * @return the parsed TabularResult
   */
  TabularResult getTabularResponse(WebTarget target, Request request);

}
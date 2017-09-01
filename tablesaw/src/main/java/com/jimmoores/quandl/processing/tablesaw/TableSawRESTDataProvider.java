package com.jimmoores.quandl.processing.tablesaw;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.processing.GenericRESTDataProvider;
import com.jimmoores.quandl.processing.Request;

import tech.tablesaw.api.Table;

public interface TableSawRESTDataProvider
    extends GenericRESTDataProvider<JSONObject, Table> {
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
   * Invoke a GET call on the web target and return the result as a TableSaw table. Throws a QuandlUnprocessableEntityException
   * if Quandl returned a response code that indicates a nonsensical request Throws a QuandlTooManyRequestsException if Quandl returned a
   * response code indicating the client had made too many requests Throws a QuandlRuntimeException if there was a JSON parsing problem,
   * network issue or response code was unusual
   * 
   * @param target the WebTarget describing the call to make, not null
   * @param request the request object or null
   * @return the resulting Table
   */
  Table getTabularResponse(WebTarget target, Request request);


}

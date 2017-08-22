package com.jimmoores.quandl.processing.tablesaw;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.processing.AbstractRESTDataProvider;
import com.jimmoores.quandl.processing.classic.JSONObjectResponseProcessor;

import tech.tablesaw.api.Table;

public class JSONTableSawRESTDataProvider extends AbstractRESTDataProvider<JSONObject, Table> {
  private static final JSONObjectResponseProcessor JSON_OBJECT_RESPONSE_PROCESSOR = new JSONObjectResponseProcessor();
  private static final TableSawResponseProcessor TABLE_SAW_RESPONSE_PROCESSOR = new TableSawResponseProcessor();

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
    return getResponse(target, JSON_OBJECT_RESPONSE_PROCESSOR);
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
  public Table getTabularResponse(final WebTarget target) {
    return getResponse(target, TABLE_SAW_RESPONSE_PROCESSOR);
  }
}

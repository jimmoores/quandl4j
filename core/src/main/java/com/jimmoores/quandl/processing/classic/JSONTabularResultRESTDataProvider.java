package com.jimmoores.quandl.processing.classic;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.processing.AbstractRESTDataProvider;
import com.jimmoores.quandl.processing.Request;

/**
 * Replacement for DefaultRESTDataProvider that specialises the AbstractRESTDataProvider for
 * org.json.JSONObject and TabularResult as the metadata and table types the user receives.
 * This is used by the new ClassicQuandlSession, providing the existing, familiar result types.
 */
public class JSONTabularResultRESTDataProvider extends AbstractRESTDataProvider<JSONObject, TabularResult> {
  private static final JSONObjectResponseProcessor JSON_OBJECT_RESPONSE_PROCESSOR = new JSONObjectResponseProcessor();
  private static final TabularResultResponseProcessor TABULAR_RESULT_RESPONSE_PROCESSOR = new TabularResultResponseProcessor();

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
  public JSONObject getJSONResponse(final WebTarget target, final Request request) {
    return getResponse(target, JSON_OBJECT_RESPONSE_PROCESSOR, request);
  }

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
  public TabularResult getTabularResponse(final WebTarget target, final Request request) {
    return getResponse(target, TABULAR_RESULT_RESPONSE_PROCESSOR, request);
  }

}

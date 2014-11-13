package com.jimmoores.quandl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Utility methods for fetching data from remote Quandl REST interface.
 * This is the default implementation that actually does a REST call, the other implementation is used for testing.
 */
public final class DefaultRESTDataProvider implements RESTDataProvider {
  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object.
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a CSV parsing problem or response code was unusual
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed JSON object
   */
  public JSONObject getJSONResponse(final WebResource target) {
    ClientResponse response = target.get(ClientResponse.class);//requestBuilder.buildGet().invoke();
    if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
      InputStream inputStream = response.getEntityInputStream();
      // should we be buffering this?
      JSONTokener tokeniser = new JSONTokener(new InputStreamReader(inputStream));
      try {
        JSONObject object = new JSONObject(tokeniser);
        return object;
      } catch (JSONException jsone) {
        throw new QuandlRuntimeException("Problem parsing JSON reply", jsone);
      }
    } else {
      throw new QuandlRuntimeException("Response code to " + target.getURI() + " was " + response.getStatusInfo());
    }  
  }
  
  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV).
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a JSON parsing problem, network issue or response code was unusual
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed TabularResult
   */
  public TabularResult getTabularResponse(final WebResource target) {
    ClientResponse response = target.get(ClientResponse.class);
    if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
      InputStream inputStream = response.getEntityInputStream();
      // should we be buffering this?
      CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
      try {
        String[] headerRow = reader.readNext();
        if (headerRow != null) {
          HeaderDefinition headerDef = HeaderDefinition.of(Arrays.asList(headerRow));
          List<Row> rows = new ArrayList<Row>();
          String[] next = reader.readNext();
          while (next != null) {
            Row row = Row.of(headerDef, next);
            rows.add(row);
            next = reader.readNext();
          }
          reader.close();
          return TabularResult.of(headerDef, rows);
        } else {
          reader.close();
          throw new QuandlRuntimeException("No data returned");
        }
      } catch (IOException ex) {
        throw new QuandlRuntimeException("Problem reading result stream", ex);
      }
    }  else if (response.getStatus() == UNPROCESSABLE_ENTITY) {
      throw new QuandlUnprocessableEntityException("Response code to " + target.getURI() + " was " + response.getStatusInfo());
    } else if (response.getStatus() == TOO_MANY_REQUESTS) {
      throw new QuandlTooManyRequestsException("Response code to " + target.getURI() + " was " + response.getStatusInfo());      
    } else {
      throw new QuandlRuntimeException("Response code to " + target.getURI() + " was " + response.getStatusInfo());
    }
    
  }
}

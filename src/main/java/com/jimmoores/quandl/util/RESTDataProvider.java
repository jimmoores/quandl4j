package com.jimmoores.quandl.util;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;

/**
 * An interface for abstracting the detail of getting a result object from the back-end, primarily to ease testing
 * but also to remove code duplication.
 */
public interface RESTDataProvider {
    /**
     * Invoke a GET call on the web target and return the result as a parsed JSON object.
     * Throws a QuandlRuntimeException if there was a CSV parsing problem or response code was not OK
     * @param target the WebTarget describing the call to make, not null
     * @return the parsed JSON object
     */
    JSONObject getJSONResponse(final WebTarget target);
    /**
     * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV).
     * Throws a QuandlRuntimeException if there was a JSON parsing problem, network issue or response code was not OK
     * @param target the WebTarget describing the call to make, not null
     * @return the parsed TabularResult
     */
    TabularResult getTabularResponse(final WebTarget target);
}

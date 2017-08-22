package com.jimmoores.quandl.util;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.processing.GenericRESTDataProvider;

/**
 * @deprecated use RESTDataProviderInterface with appropriate types An interface for abstracting the detail of getting a result object from
 *             the back-end, primarily to ease testing but also to remove code duplication.
 */
public interface RESTDataProvider extends GenericRESTDataProvider<JSONObject, TabularResult> {

}

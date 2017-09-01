package com.jimmoores.quandl.generic;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.SearchRequest;
/**
 * Generic top-level interface for QuandlSessions, allowing a range of types of be used
 * to return data.
 * @param <METADATA_TYPE>  the type used to wrap meta data results
 * @param <TABLE_TYPE>  the type used to hold tabular results
 * @param <SEARCH_TYPE>  the type used to wrap search meta data results
 */
public interface GenericQuandlSessionInterface<METADATA_TYPE, TABLE_TYPE, SEARCH_TYPE> {
  /**
   * the parameter name for the authorization token (aka Quandl API key).
   */
  String AUTH_TOKEN_PARAM_NAME = "auth_token";

  /**
   * Get a tabular data set from Quandl.
   * 
   * @param request the request object containing details of what is required
   * @return a table result set of type TABLE_TYPE
   */
  TABLE_TYPE getDataSet(DataSetRequest request);

  /**
   * Get meta data from Quandl about a particular quandlCode.
   * 
   * @param request the request object containing details of what is required
   * @return a meta data result wrapper of type METADATA_TYPE
   */
  METADATA_TYPE getMetaData(MetaDataRequest request);

  /**
   * Get search results from Quandl.
   * 
   * @param request the search query parameter, not null
   * @return the search result wrapper of type SEARCH_TYPE, not null
   */
  SEARCH_TYPE search(SearchRequest request);

}

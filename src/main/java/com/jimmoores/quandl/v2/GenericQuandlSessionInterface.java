package com.jimmoores.quandl.v2;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.SearchRequest;

public interface GenericQuandlSessionInterface<METADATA_TYPE, TABLE_TYPE, SEARCH_TYPE> {
  /**
   * the parameter name for the authorization token (aka Quandl API key).
   */
  String AUTH_TOKEN_PARAM_NAME = "auth_token";

  /**
   * Get a tabular data set from Quandl.
   * 
   * @param request
   *          the request object containing details of what is required
   * @return a TabularResult set
   */
  TABLE_TYPE getDataSet(DataSetRequest request);

  /**
   * Get meta data from Quandl about a particular quandlCode.
   * 
   * @param request
   *          the request object containing details of what is required
   * @return a MetaDataResult
   */
  METADATA_TYPE getMetaData(MetaDataRequest request);

  /**
   * Get search results from Quandl.
   * 
   * @param request
   *          the search query parameter, not null
   * @return the search result, not null
   */
  SEARCH_TYPE search(SearchRequest request);

}

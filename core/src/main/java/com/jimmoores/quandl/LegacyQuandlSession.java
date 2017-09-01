package com.jimmoores.quandl;

import java.util.Map;

import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;

/**
 * Interface to add deprecated methods to new GenericQuandlSessionInterface for the legacy QuandlSession
 * object, retained for backwards compatibility.
 *
 * @param <METADATA_TYPE>  the type used to wrap metadata results
 * @param <TABLE_TYPE>  the type used to hold tabular results
 * @param <SEARCH_TYPE>  the type used to wrap search results
 */
public interface LegacyQuandlSession<METADATA_TYPE, TABLE_TYPE, SEARCH_TYPE>
    extends GenericQuandlSessionInterface<METADATA_TYPE, TABLE_TYPE, SEARCH_TYPE> {
  /**
   * Get header definitions from Quandl about a range of quandlCodes returned as a Map of Quandl code to HeaderDefinition. The keys of the
   * map will retain the order of the request and are backed by an unmodifiable LinkedHashMap. Throws a QuandlRuntimeException if it can't
   * find a parsable quandl code or Date column in the result.
   * 
   * @deprecated this now uses single calls to simulate multisets to support legacy code
   * @param request the request object containing details of what is required, not null
   * @return an unmodifiable Map of Quandl codes to MetaDataResult for each code, keys ordered according to request, not null
   */
  Map<String, HeaderDefinition> getMultipleHeaderDefinition(MultiMetaDataRequest request);

  /**
   * Get a multiple data sets from quandl and return as single tabular result.
   * 
   * @param request the multi data set request object containing details of what is required
   * @return a single TabularResult set containing all requested results
   * @deprecated this call is provided for compatibility purposes and is deprecated, please use the single request mechanism
   */
  TABLE_TYPE getDataSets(MultiDataSetRequest request);

  /**
   * Get meta data from Quandl about a range of quandlCodes returned as a single MetaDataResult.
   * 
   * @param request the request object containing details of what is required
   * @return a TabularResult set
   */
  METADATA_TYPE getMetaData(MultiMetaDataRequest request);
}

package com.jimmoores.quandl.processing;

/**
 * Interface for classes that package metadata results in wrapper classes.
 * @param <METADATA_TYPE>  the type of the container used to wrap the meta data
 * @param <RAW_METADATA_TYPE>  the type used to hold the raw meta data
 * @param <SEARCH_RESULT_TYPE>  the type used to wrap search result meta data
 */
public interface MetaDataPackager<METADATA_TYPE, RAW_METADATA_TYPE, SEARCH_RESULT_TYPE> {
  /**
   * Package raw metadata in a query result wrapper.
   * @param rawData  the raw representation of the metadata
   * @return the wrapped query metadata
   */
  METADATA_TYPE ofMetaData(RAW_METADATA_TYPE rawData);

  /**
   * Package raw metadata into a search result wrapper.
   * @param rawData  the raw representation of the metadata
   * @return the wrapped search result metadata
   */
  SEARCH_RESULT_TYPE ofSearchResult(RAW_METADATA_TYPE rawData);
}

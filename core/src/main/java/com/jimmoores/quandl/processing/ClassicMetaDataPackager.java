package com.jimmoores.quandl.processing;

import org.json.JSONObject;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchResult;

/**
 * Packager class provides the classic packaging of meta-data results into *Result wrappers.
 */
public class ClassicMetaDataPackager implements MetaDataPackager<MetaDataResult, JSONObject, SearchResult> {

  /**
   * {@inheritDoc}
   */
  public MetaDataResult ofMetaData(final JSONObject rawData) {
    return MetaDataResult.of(rawData);
  }
  /**
   * {@inheritDoc}
   */
  public SearchResult ofSearchResult(final JSONObject rawData) {
    return SearchResult.of(rawData);
  }

}

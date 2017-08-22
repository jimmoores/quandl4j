package com.jimmoores.quandl.processing;

public interface MetaDataPackager<METADATA_TYPE, RAW_METADATA_TYPE, SEARCH_RESULT_TYPE> {
  METADATA_TYPE ofMetaData(RAW_METADATA_TYPE rawData);

  SEARCH_RESULT_TYPE ofSearchResult(RAW_METADATA_TYPE rawData);
}

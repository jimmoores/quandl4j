package com.jimmoores.quandl.processing;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.SearchRequest;

/**
 * This interface is used to process arbitrary request objects.  It is 
 * essentially an interface for a visitor pattern for classes implementing
 * @see com.jimmoores.quandl.processing.Request
 * The driver for creating this is to use it to extract request information
 * when processing a result.  For example it is used to create a suitable
 * title for tables in the tablesaw module using the TitleRequestProcessor.
 * @param <T> the type you want your request processor to return
 */
public interface RequestProcessor<T> {
  /**
   * Process a MetaDataRequest object and return data about it.
   * @param request  the meta data request
   * @return the processed result
   */
  T processMetaDataRequest(MetaDataRequest request);
  /**
   * Process a MultiMetaDataRequest object and return data about it.
   * @param request  the multi meta data request
   * @return the processed result
   */
  T processMultiMetaDataRequest(MultiMetaDataRequest request);
  /**
   * Process a DataSetRequest object and return data about it.
   * @param request  the meta data request
   * @return the processed result
   */
  T processDataSetRequest(DataSetRequest request);
  /**
   * Process a MultiDataSetRequest object and return data about it.
   * @param request  the multi data set request
   * @return the processed result
   */
  T processMultiDataSetRequest(MultiDataSetRequest request);
  /**
   * Process a SerachRequest object and return data about it.
   * @param request  the search request
   * @return the processed result
   */
  T processSearchRequest(SearchRequest request);
}

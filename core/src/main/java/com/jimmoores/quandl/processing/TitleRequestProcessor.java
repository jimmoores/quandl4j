package com.jimmoores.quandl.processing;

import java.util.Iterator;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.QuandlCodeRequest;
import com.jimmoores.quandl.SearchRequest;

import jersey.repackaged.com.google.common.base.Joiner;

/**
 * Implementation of RequestProcessor (visitor) that turns each request type (implementer of
 * the Request interface) into a descriptive title string.
 */
public class TitleRequestProcessor implements RequestProcessor<String> {

  /**
   * Process a metadata request into a descriptive title string.
   * @param request  the meta data request
   * @return the title string
   */
  public String processMetaDataRequest(final MetaDataRequest request) {
    return request.getQuandlCode();
  }
  /**
   * Process a multi-metadata request into a descriptive title string.
   * @param request  the multi-metadata request
   * @return the title string
   */
  public String processMultiMetaDataRequest(final MultiMetaDataRequest request) {
    return Joiner.on(",").join(request.getQuandlCodes());
  }
  /**
   * Process a data set request into a descriptive title string.
   * @param request  the data set request
   * @return the title string
   */
  public String processDataSetRequest(final DataSetRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append(request.getQuandlCode());
    if (request.getColumnIndex() != null) {
      sb.append(" column index ");
      sb.append(request.getColumnIndex());
    }
    if (request.getStartDate() != null) {
      sb.append(" from ");
      sb.append(request.getStartDate());
    }
    if (request.getEndDate() != null) {
      sb.append(" until ");
      sb.append(request.getEndDate());
    }
    if (request.getFrequency() != null) {
      sb.append(" sampled ");
      sb.append(request.getFrequency());
    }
    if (request.getSortOrder() != null) {
      sb.append(" sorted into ");
      sb.append(request.getSortOrder());
      sb.append(" order");
    }
    if (request.getTransform() != null) {
      sb.append(" transformed by ");
      sb.append(request.getTransform());
    }
    if (request.getMaxRows() != null) {
      sb.append(" with at most ");
      sb.append(request.getMaxRows());
      sb.append(" rows");
    }
    return sb.toString();
  }
  /**
   * Process a multi data set request into a descriptive title string.
   * @param request  the multi data set request
   * @return the title string
   */
  public String processMultiDataSetRequest(final MultiDataSetRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append("Quandl codes ");
    final Iterator<QuandlCodeRequest> iter = request.getQuandlCodeRequests().iterator();
    while (iter.hasNext()) {
      QuandlCodeRequest req = iter.next();
      sb.append(req.getQuandlCode());
      if (req.isSingleColumnRequest()) {
        sb.append("[");
        sb.append(req.getColumnNumber());
        sb.append("]");
      }
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    if (request.getStartDate() != null) {
      sb.append(" from ");
      sb.append(request.getStartDate());
    }
    if (request.getEndDate() != null) {
      sb.append(" until ");
      sb.append(request.getEndDate());
    }
    if (request.getFrequency() != null) {
      sb.append(" sampled ");
      sb.append(request.getFrequency());
    }
    if (request.getSortOrder() != null) {
      sb.append(" sorted into ");
      sb.append(request.getSortOrder());
      sb.append(" order");
    }
    if (request.getTransform() != null) {
      sb.append(" transformed by ");
      sb.append(request.getTransform());
    }
    if (request.getMaxRows() != null) {
      sb.append(" with at most ");
      sb.append(request.getMaxRows());
      sb.append(" rows");
    }
    return sb.toString();
  }
  /**
   * Process a search request into a descriptive title string.
   * @param request  the search request
   * @return the title string
   */
  public String processSearchRequest(final SearchRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append("Search for ");
    if (request.getDatabaseCode() != null) {
      builder.append("code ");
      builder.append(request.getDatabaseCode());
    }
    if (request.getQuery() != null) {
      builder.append(" with query ");
      builder.append(request.getQuery());
    }
    if (request.getPageNumber() != null) {
      builder.append(" page ");
      builder.append(request.getPageNumber());
    }
    if (request.getMaxPerPage() != null) {
      builder.append(" limited to ");
      builder.append(request.getMaxPerPage());
      builder.append(" per page");
    }
    return builder.toString();
  }

}

package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.client.WebTarget;

import com.jimmoores.quandl.processing.RequestProcessor;
import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * A class that packages the request for MetaData from Quandl.
 */
public final class MultiMetaDataRequest implements Request {
  private static final String COLUMNS_PARAM = "columns";
  private static final String EXTENSION = ".json";
  private static final String MULTI_SET_NAME = "multisets";
  private static final String EXCLUDE_DATA_PARAM = "start_date";
  private static final String INFINITE_FUTURE = "2100-01-01";

  private List<String> _quandlCodes;

  private MultiMetaDataRequest(final List<String> quandlCodes) {
    _quandlCodes = Collections.unmodifiableList(new ArrayList<String>(quandlCodes));
  }

  /**
   * Factory method to create a meta data request instance.
   * 
   * @param quandlCodes the list of quandl codes for the meta data required, not null
   * @return an instance of the MetaDataRequest for the given quandlCode, not null
   */
  public static MultiMetaDataRequest of(final List<String> quandlCodes) {
    ArgumentChecker.notNullOrEmpty(quandlCodes, "quandlCodes");
    return new MultiMetaDataRequest(quandlCodes);
  }

  /**
   * Factory method to create a meta data request instance.
   * 
   * @param quandlCodes the list of quandl codes for the meta data required (varargs), not null
   * @return an instance of the MetaDataRequest for the given quandlCode, not null
   */
  public static MultiMetaDataRequest of(final String... quandlCodes) {
    ArgumentChecker.notNullOrEmpty(quandlCodes, "quandlCodes");
    return new MultiMetaDataRequest(Arrays.asList(quandlCodes));
  }

  /**
   * @return the list of quandl codes contained in this request.
   */
  public List<String> getQuandlCodes() {
    return _quandlCodes;
  }

  private String buildCodeList(final List<String> quandlCodes) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> iter = quandlCodes.iterator();
    while (iter.hasNext()) {
      String quandlCode = iter.next();
      if (quandlCode == null) {
        throw new IllegalArgumentException("There was a null encountered in the argument list " + quandlCodes);
      }
      String transformedCode = quandlCode.replace('/', '.');
      sb.append(transformedCode);
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  /**
   * Append any specified parameters to the provided WebTarget.
   * 
   * @param webTarget a web target used by the Jersey Client API, not null
   * @return the WebTarget with any path and query parameters appended, not null
   */
  public WebTarget appendPathAndQueryParameters(final WebTarget webTarget) {
    ArgumentChecker.notNull(webTarget, "webTarget");
    WebTarget resultTarget = webTarget;
    resultTarget = resultTarget.path(MULTI_SET_NAME + EXTENSION);
    resultTarget = resultTarget.queryParam(COLUMNS_PARAM, buildCodeList(_quandlCodes));
    // This is a hack that stops Quandl from returning all the data as part of the query
    resultTarget = resultTarget.queryParam(EXCLUDE_DATA_PARAM, INFINITE_FUTURE);
    return resultTarget;
  }

  @Override
  public int hashCode() {
    return _quandlCodes.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MultiMetaDataRequest)) {
      return false;
    }
    MultiMetaDataRequest other = (MultiMetaDataRequest) obj;
    if (!_quandlCodes.equals(other._quandlCodes)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "MultiMetaDataRequest[" + _quandlCodes + "]";
  }
  
  /**
   * Accept a request processor in visitor pattern style.
   * @param <T> the processor result type
   * @param processor  the request processor
   * @return the request processor's result
   */
  public <T> T accept(final RequestProcessor<T> processor) {
    return processor.processMultiMetaDataRequest(this);
  }
}

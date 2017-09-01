package com.jimmoores.quandl;

import javax.ws.rs.client.WebTarget;

import com.jimmoores.quandl.processing.RequestProcessor;
import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * A class that packages the request for MetaData from Quandl.
 */
public final class MetaDataRequest implements Request {
  private static final String EXTENSION = ".json";
  private static final String DATASETS_RELATIVE_URL = "datasets";
  private static final String EXCLUDE_DATA_PARAM = "start_date";
  private static final String INFINITE_FUTURE = "2100-01-01";

  private String _quandlCode;

  private MetaDataRequest(final String quandlCode) {
    _quandlCode = quandlCode;
  }

  /**
   * Factory method to create a meta data request instance.
   * 
   * @param quandlCode the quandl code for the meta data required, not null
   * @return an instance of the MetaDataRequest for the given quandlCode
   */
  public static MetaDataRequest of(final String quandlCode) {
    ArgumentChecker.notNull(quandlCode, "quandlCode");
    return new MetaDataRequest(quandlCode);
  }

  /**
   * @return the quandl code
   */
  public String getQuandlCode() {
    return _quandlCode;
  }

  /**
   * Append any specified parameters to the provided WebTarget.
   * 
   * @param webTarget a web target used by the Jersey Client API, not null
   * @return the WebTarget with any path and query parameters appended
   */
  public WebTarget appendPathAndQueryParameters(final WebTarget webTarget) {
    ArgumentChecker.notNull(webTarget, "webTarget");
    WebTarget resultTarget = webTarget;
    resultTarget = resultTarget.path(DATASETS_RELATIVE_URL);
    resultTarget = resultTarget.path(_quandlCode + EXTENSION);
    resultTarget = resultTarget.queryParam(EXCLUDE_DATA_PARAM, INFINITE_FUTURE);
    return resultTarget;
  }

  @Override
  public int hashCode() {
    return _quandlCode.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MetaDataRequest)) {
      return false;
    }
    MetaDataRequest other = (MetaDataRequest) obj;
    return _quandlCode.equals(other._quandlCode);
  }

  @Override
  public String toString() {
    return "MetaDataRequest[quandlCode=" + _quandlCode + "]";
  }
  
  /**
   * Accept a request processor in visitor pattern style.
   * @param <T> the processor result type
   * @param processor  the request processor
   * @return the request processor's result
   */
  public <T> T accept(final RequestProcessor<T> processor) {
    return processor.processMetaDataRequest(this);
  }
}

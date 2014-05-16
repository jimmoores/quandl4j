package com.jimmoores.quandl;

import javax.ws.rs.client.WebTarget;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * A class that packages the request for search query from Quandl.
 */
public final class SearchRequest {
  private static final String EXTENSION = ".json";
  private static final String DATASETS_RELATIVE_URL = "datasets";
  private static final String QUERY_PARAM = "query";
  
  private final String _query;

  private SearchRequest(final String query) {
    _query = query;
  }
  
  /**
   * Factory method to create a meta data request instance.
   * @param query an arbitrary query string, not null
   * @return an instance of the Search for the given query string.
   */
  public static SearchRequest of(final String query) {
    ArgumentChecker.notNull(query, "query");
    return new SearchRequest(query);
  }
  
  /**
   * Append any specified parameters to the provided WebTarget.
   * @param webTarget a web target used by the Jersey Client API, not null
   * @return the WebTarget with any path and query parameters appended
   */
  public WebTarget appendPathAndQueryParameters(final WebTarget webTarget) {
    ArgumentChecker.notNull(webTarget, "webTarget");
    WebTarget resultTarget = webTarget;
    resultTarget = resultTarget.path(DATASETS_RELATIVE_URL + EXTENSION);
    resultTarget = resultTarget.queryParam(QUERY_PARAM, _query);
    return resultTarget;
  }

  @Override
  public int hashCode() {
    return _query.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SearchRequest)) {
      return false;
    }
    SearchRequest other = (SearchRequest) obj;
    return _query.equals(other._query);
  }
  
  @Override
  public String toString() {
    return "SearchRequest[query=" + _query + "]";
  }
}

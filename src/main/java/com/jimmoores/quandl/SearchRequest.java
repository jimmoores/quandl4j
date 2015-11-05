package com.jimmoores.quandl;

import javax.ws.rs.client.WebTarget;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * A class that packages the request for search query from Quandl.
 * Examples:
 * <pre>
 *   new SearchRequest.Builder().withQuery("query").withPageNumber(5).withMaxPerPage(100).build();
 *   new SearchRequest.Builder().withQuery("query").build();
 *   new SearchRequest.Builder().withDatabaseCode("databasecode").build();
 * </pre>
 */
public final class SearchRequest {
  private static final String EXTENSION = ".json";
  private static final String DATASETS_RELATIVE_URL = "datasets";
  private static final String DATABASE_CODE_PARAM = "database_code";
  private static final String QUERY_PARAM = "query";
  private static final String PAGE_PARAM = "page";
  private static final String PER_PAGE_PARAM = "per_page";

  private final String _databaseCode;
  private final String _query;
  private Integer _pageNumber;
  private Integer _maxDocsPerPage;
  
  private SearchRequest(final Builder builder) {
    _databaseCode = builder._databaseCode;
    _query = builder._query;
    _pageNumber = builder._pageNumber;
    _maxDocsPerPage = builder._maxDocsPerPage;
  }
  
  /**
   * Builder for this class.
   * Examples:
   * <pre>
   *   new SearchRequest.Builder().withQuery("query").withPageNumber(5).withMaxPerPage(100).build();
   *   new SearchRequest.Builder().withQuery("query").build();
   *   new SearchRequest.Builder().withDatabaseCode("databasecode").build();
   * </pre>
   */
  public static final class Builder {
    private String _databaseCode;
    private String _query;
    private Integer _pageNumber;
    private Integer _maxDocsPerPage;

    public Builder() {
    }

    @Deprecated
    private Builder(final String query) {
      _query = query;
    }
    
    /**
     * Factory method to create a meta data request instance.
     * Use empty string to get all documents.
     * @param query an arbitrary query string, not null (note: can be empty string to get all documents)
     * @return an instance of this Builder for the given query string.
     * @deprecated use Builder().withQuery(query) instead
     */
    @Deprecated
    public static Builder of(final String query) {
      ArgumentChecker.notNull(query, "query");
      return new Builder(query);
    }

    /**
     * Specify the database to search within. For example WIKI
     * @param databaseCode the database code to search within.
     * @return this Builder, with this database code specified
     */
    public Builder withDatabaseCode(final String databaseCode) {
      _databaseCode = databaseCode;
      return this;
    }

    /**
     * Specify the search term to use for your query. Multiple search terms can be separated by the + character.
     * @param query the query string to use
     * @return this Builder, with this query added
     */
    public Builder withQuery(final String query) {
      _query = query;
      return this;
    }
    
    /**
     * Specify the page number of the results (1-based).
     * @param pageNumber the page number of the results, 1-based.
     * @return this Builder, with the page number information added
     */
    public Builder withPageNumber(final int pageNumber) {
      _pageNumber = pageNumber;
      return this;
    }
    
    /**
     * Specify the maximum number of documents per page of results.
     * Currently, this is limited to 100, but this library does not enforce that in case the limit changes.
     * @param maxDocsPerPage the maximum number of documents per page (currently limited to 100 by API)
     * @return this Builder, with the page number information added
     */
    public Builder withMaxPerPage(final int maxDocsPerPage) {
      _maxDocsPerPage = maxDocsPerPage;
      return this;
    }
    /**
     * Build an instance of the underlying request object.
     * @return an instance of the seach request object
     */
    public SearchRequest build() {
      return new SearchRequest(this);
    }
  };

  /**
   * @return the database code, or null if not set
   */
  public String getDatabaseCode() {
    return _databaseCode;
  }

  /**
   * @return the query string, or null if not set
   */
  public String getQuery() {
    return _query;
  }
  
  /**
   * @return the page number, or null if not set
   */
  public Integer getPageNumber() {
    return _pageNumber;
  }
  
  /**
   * @return the maximum number of results to return per page
   */
  public Integer getMaxPerPage() {
    return _maxDocsPerPage;
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
    if (_databaseCode != null) {
      resultTarget = resultTarget.queryParam(DATABASE_CODE_PARAM, _databaseCode);
    }
    if (_query != null) {
      resultTarget = resultTarget.queryParam(QUERY_PARAM, _query);
    }
    if (_pageNumber != null) {
      resultTarget = resultTarget.queryParam(PAGE_PARAM, _pageNumber);
    }
    if (_maxDocsPerPage != null) {
      resultTarget = resultTarget.queryParam(PER_PAGE_PARAM, _maxDocsPerPage);
    }
    return resultTarget;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (_maxDocsPerPage != null) {
      result = prime * result + _maxDocsPerPage;
    }
    if (_pageNumber != null) {
      result = prime * result + _pageNumber;
    }
    if (_query != null) {
      result = prime * result + _query.hashCode();
    }
    if (_databaseCode != null) {
      result = prime * result + _databaseCode.hashCode();
    }
    return result;
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
    if (!_databaseCode.equals(other._databaseCode)) {
      return false;
    }
    if (!_query.equals(other._query)) {
      return false;
    }
    if (!_maxDocsPerPage.equals(other._maxDocsPerPage)) {
      return false;
    }
    if (!_pageNumber.equals(other._pageNumber)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SearchRequest[");
    if (_databaseCode != null) {
      builder.append("databaseCode=");
      builder.append(_databaseCode);
    }
    if (_query != null) {
      if (builder.charAt(builder.length() - 1) != '[') {
        builder.append(", ");
      }
      builder.append("query=");
      builder.append(_query);
    }
    if (_pageNumber != null) {
      if (builder.charAt(builder.length() - 1) != '[') {
        builder.append(", ");
      }
      builder.append("pageNumber=");
      builder.append(_pageNumber);
    }
    if (_maxDocsPerPage != null) {
      if (builder.charAt(builder.length() - 1) != '[') {
        builder.append(", ");
      }
      builder.append("maxDocsPerPage=");
      builder.append(_maxDocsPerPage);
    }
    builder.append("]");
    return builder.toString();
  }
}

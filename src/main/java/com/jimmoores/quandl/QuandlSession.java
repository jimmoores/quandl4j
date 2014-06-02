package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * Quandl session class.
 * Create an instance with either
 * <pre>
 *   QuandlSession.of(SessionOptions.withAuthToken(AUTH_TOKEN));
 * </pre>
 * to use your API authorization token string, or use the API without a token, which may 
 * have lower rate/volume limits.
 * <pre>
 *   QuandlSession.of();
 * </pre>
 * Then call one of the methods to fetch data!
 */
public final class QuandlSession {
  private static final String DATE_COLUMN = "Date";

  private static Logger s_logger = LoggerFactory.getLogger(QuandlSession.class);
  
  private SessionOptions _sessionOptions;
  private static final UriBuilder API_BASE_URL = UriBuilder.fromPath("http://quandl.com/api/v1");
  private static final String QUANDL_AUTH_TOKEN_PROPERTY_NAME = "quandl.auth.token";
  /**
   * the parameter name for the authorization token (aka Quandl API key).
   */
  public static final String AUTH_TOKEN_PARAM_NAME = "auth_token";

  private QuandlSession(final SessionOptions sessionOptions) {
    _sessionOptions = sessionOptions;
  }
  
  /**
   * Create a Quandl session to use a specific authorization token (authToken) with 
   * all requests.  Using a token means you can make more requests.  Note that this method does not
   * check the quandl.auth.token property.  Creating this object does not make any actual API 
   * requests, the token is used in subsequent requests.
   * @param authToken a Quandl API authorization token
   * @return an instance of the Quandl session with an embedded authorization token
   */
  public static QuandlSession create(final String authToken) {
    ArgumentChecker.notNull(authToken, "authToken");
    return new QuandlSession(SessionOptions.Builder.withAuthToken(authToken).build());
  }

  /**
   * Create a Quandl session without an authorization token (authToken).  An attempt will be made
   * to read the java property <em>quandl.auth.token</em> and use that if available.  Any resulting
   * SecurityException is logged at debug level, otherwise it is ignored and no auth token is used.
   * Using a token means you can make more requests.  Note creating this object does not
   * make any actual API requests, the token is used in subsequent requests.
   * @return an instance of the Quandl session, not null.
   */
  public static QuandlSession create() {
    try {
      String authToken = System.getProperty(QUANDL_AUTH_TOKEN_PROPERTY_NAME);
      if (authToken != null) {
        return new QuandlSession(SessionOptions.Builder.withAuthToken(authToken).build());
      }
    } catch (SecurityException se) {
      s_logger.debug("Error accessing system property " + QUANDL_AUTH_TOKEN_PROPERTY_NAME + ", falling back to not using an auth token", se);
    }
    return new QuandlSession(SessionOptions.Builder.withoutAuthToken().build());
  }
  
  /**
   * Create a Quandl session with detailed SessionOptions.  
   * No attempt will be made to read the java property <em>quandl.auth.token</em> even if available.  
   * Note creating this object does not make any actual API requests, the token is used in subsequent 
   * requests.
   * @param sessionOptions a user created SessionOptions instance, not null
   * @return an instance of the Quandl session, not null
   */
  public static QuandlSession create(final SessionOptions sessionOptions) {
    ArgumentChecker.notNull(sessionOptions, "sessionOptions");
    return new QuandlSession(sessionOptions);
  }
  
  /**
   * Allow the client to be overridden by a test subclass.
   * @return a Jersey Client
   */
  protected Client getClient() {
    return ClientBuilder.newClient();
  }
  
  /**
   * Add authorization token to the web target.
   * @param target the web target
   */
  private WebTarget withAuthToken(final WebTarget target) {
    if (_sessionOptions.getAuthToken() != null) {
      return target.queryParam(AUTH_TOKEN_PARAM_NAME, _sessionOptions.getAuthToken());
    } else {
      return target;
    }
  }
  
  /**
   * Get a tabular data set from Quandl.
   * @param request the request object containing details of what is required
   * @return a TabularResult set
   */
  public TabularResult getDataSet(final DataSetRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = getClient();
    WebTarget target = client.target(API_BASE_URL);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    return _sessionOptions.getRESTDataProvider().getTabularResponse(target);
  }
  
  /**
   * Get meta data from Quandl about a particular quandlCode.
   * @param request the request object containing details of what is required
   * @return a MetaDataResult 
   */
  public MetaDataResult getMetaData(final MetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    JSONObject object = _sessionOptions.getRESTDataProvider().getJSONResponse(target);
    return MetaDataResult.of(object);
  }
  

  
  /**
   * Get a multiple data sets from quandl and return as single tabular result.
   * @param request the multi data set request object containing details of what is required
   * @return a single TabularResult set containing all requested results
   */
  public TabularResult getDataSets(final MultiDataSetRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = getClient();
    WebTarget target = client.target(API_BASE_URL);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    return _sessionOptions.getRESTDataProvider().getTabularResponse(target);
  }
  
  /**
   * Get meta data from Quandl about a range of quandlCodes returned as a single MetaDataResult.
   * @param request the request object containing details of what is required
   * @return a TabularResult set
   */
  public MetaDataResult getMetaData(final MultiMetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    return MetaDataResult.of(_sessionOptions.getRESTDataProvider().getJSONResponse(target));
  }
  
  /**
   * Get header definitions from Quandl about a range of quandlCodes returned as a Map of Quandl code to HeaderDefinition.
   * The keys of the map will retain the order of the request and are backed by an unmodifiable LinkedHashMap.
   * Throws a QuandlRuntimeException if it can't find a parsable quandl code or Date column in the result.
   * @param request the request object containing details of what is required, not null
   * @return an unmodifiable Map of Quandl codes to MetaDataResult for each code, keys ordered according to request, not null
   */
  public Map<String, HeaderDefinition> getMultipleHeaderDefinition(final MultiMetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    MetaDataResult result = getMetaData(request);
    HeaderDefinition headerDefinition = result.getHeaderDefinition();
    // go through the headerDefinition of the raw request and separate out by quandl code
    Iterator<String> iter = headerDefinition.iterator();
    // make sure it has a Date column as we're expecting
    if (!iter.hasNext() || !iter.next().equals(DATE_COLUMN)) {
      s_logger.error("Expected 'Date' as first column in result {}", result);
      throw new QuandlRuntimeException("Expected Date as first column in result");
    }
    // we assume no particular ordering here, seems safer, if slightly more costly in memory allocations
    // we use a list of string here because HeaderDefinition is immutable, so we only want to build once.
    Map<String, List<String>> rawResults = new LinkedHashMap<String, List<String>>();
    while (iter.hasNext()) {
      // get raw column name of form 'QUANDL/CODE - Column Name'
      String column = iter.next();
      String[] split = column.split(" - ");
      // make sure it splits as we expect
      if (split.length != 2) {
        s_logger.error("Expected column name {} to split into two pieces", column);
        throw new QuandlRuntimeException("Expected column name to split into two pieces");
      }
      String quandlCode = split[0];
      String columnName = split[1];
      if (rawResults.containsKey(quandlCode)) {
        // we've already seen this code, so a list is already there, just add new column name
        rawResults.get(quandlCode).add(columnName);
      } else {
        // haven't seen this code before, so put an list in.
        List<String> columns = new ArrayList<String>();
        // give each set a Date column as it's first column
        // we're doing this so we can use these headers for the getMultipleDataSets() call.
        columns.add(DATE_COLUMN); 
        columns.add(columnName);
        rawResults.put(quandlCode, columns);
      }
    }
    Map<String, HeaderDefinition> results = new LinkedHashMap<String, HeaderDefinition>();
    for (String key : rawResults.keySet()) {
      List<String> columnList = rawResults.get(key);
      results.put(key, HeaderDefinition.of(columnList));
    }
    return Collections.unmodifiableMap(results);
  }
  
  /**
   * Get search results from Quandl.
   * @param request the search query parameter, not null
   * @return the search result, not null
   */
  public SearchResult search(final SearchRequest request) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    return SearchResult.of(_sessionOptions.getRESTDataProvider().getJSONResponse(target));
  }
}

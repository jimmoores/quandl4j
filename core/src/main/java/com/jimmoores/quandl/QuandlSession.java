package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.chrono.ChronoLocalDate;

import com.jimmoores.quandl.DataSetRequest.Builder;
import com.jimmoores.quandl.generic.GenericQuandlSession;
import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.QuandlRequestFailedException;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.QuandlServiceUnavailableException;
import com.jimmoores.quandl.util.QuandlTooManyRequestsException;
import com.jimmoores.quandl.util.RESTDataProvider;

/**
 * @deprecated use e.g. ClassicQuandlSession, StringQuandlSession or TableSawQuandlSession
 * 
 * Quandl session class. Create an instance with either
 * 
 * <pre>
 * QuandlSession.of(SessionOptions.withAuthToken(AUTH_TOKEN));
 * </pre>
 * 
 * to use your API authorization token string, or use the API without a token, which may have lower rate/volume limits.
 * 
 * <pre>
 * QuandlSession.of();
 * </pre>
 * 
 * Then call one of the methods to fetch data!
 *
 * You can either invoke withDefaultRetentionPolicy() on the session or withFrequency() on the DataSetRequest to hint the CacheManager
 * whether it should load over the network or retrieve data from the cache
 */
public final class QuandlSession implements LegacyQuandlSession<MetaDataResult, TabularResult, SearchResult> {
  private static final String JSON_TO_DATE_FIELD = "to_date";
  private static final String JSON_FROM_DATE_FIELD = "from_date";
  private static final String JSON_FREQUENCY_FIELD = "frequency";
  private static final String JSON_ERRORS_FIELD = "errors";
  private static final String JSON_DATA_FIELD = "data";
  private static final String JSON_COLUMNS_FIELD = "columns";
  private static final String JSON_COLUMN_NAMES_FIELD = "column_names";
  private static final String DATE_COLUMN = "Date";
  private static final UriBuilder API_BASE_URL_V3 = UriBuilder.fromPath("https://www.quandl.com/api/v3");
  private static Logger s_logger = LoggerFactory.getLogger(QuandlSession.class);

  private SessionOptions _sessionOptions;

  private QuandlSession(final SessionOptions sessionOptions) {
    _sessionOptions = sessionOptions;
    _restDataProvider = _sessionOptions.getRESTDataProvider();
  }

  /**
   * Create a Quandl session to use a specific authorization token (authToken) with all requests. Using a token means you can make more
   * requests. Note that this method does not check the quandl.auth.token property. Creating this object does not make any actual API
   * requests, the token is used in subsequent requests.
   * 
   * @param authToken a Quandl API authorization token
   * @return an instance of the Quandl session with an embedded authorization token
   */
  public static QuandlSession create(final String authToken) {
    ArgumentChecker.notNull(authToken, "authToken");
    return new QuandlSession(SessionOptions.Builder.withAuthToken(authToken).build());
  }

  /**
   * Create a Quandl session without an authorization token (authToken). An attempt will be made to read the java property
   * <em>quandl.auth.token</em> and use that if available. Any resulting SecurityException is logged at debug level, otherwise it is ignored
   * and no auth token is used. Using a token means you can make more requests. Note creating this object does not make any actual API
   * requests, the token is used in subsequent requests.
   * 
   * @return an instance of the Quandl session, not null.
   */
  public static QuandlSession create() {
    try {
      String authToken = System.getProperty(GenericQuandlSession.QUANDL_AUTH_TOKEN_PROPERTY_NAME);
      if (authToken != null) {
        return new QuandlSession(SessionOptions.Builder.withAuthToken(authToken).build());
      }
    } catch (SecurityException se) {
      s_logger.debug("Error accessing system property " + GenericQuandlSession.QUANDL_AUTH_TOKEN_PROPERTY_NAME + ", falling back to not using an auth token",
          se);
    }
    return new QuandlSession(SessionOptions.Builder.withoutAuthToken().build());
  }

  /**
   * Create a Quandl session with detailed SessionOptions. No attempt will be made to read the java property <em>quandl.auth.token</em> even
   * if available. Note creating this object does not make any actual API requests, the token is used in subsequent requests.
   * 
   * @param sessionOptions a user created SessionOptions instance, not null
   * @return an instance of the Quandl session, not null
   */
  public static QuandlSession create(final SessionOptions sessionOptions) {
    ArgumentChecker.notNull(sessionOptions, "sessionOptions");
    return new QuandlSession(sessionOptions);
  }

  /**
   * Allow the client to be overridden by a test subclass.
   * 
   * @return a Jersey Client
   */
  protected Client getClient() {
    return ClientBuilder.newClient();
  }

  /**
   * Add authorization token to the web target.
   * 
   * @param target the web target
   */
  private WebTarget withAuthToken(final WebTarget target) {
    if (_sessionOptions.getAuthToken() != null) {
      return target.queryParam(AUTH_TOKEN_PARAM_NAME, _sessionOptions.getAuthToken());
    } else {
      return target;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.jimmoores.quandl.QuandlSessionInterface#getDataSet(com.jimmoores.quandl.DataSetRequest)
   */
  public TabularResult getDataSet(final DataSetRequest request) {
    ArgumentChecker.notNull(request, "request");

    Client client = getClient();
    WebTarget target = client.target(API_BASE_URL_V3);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    TabularResult tabularResponse = null;
    int retries = 0;
    do {
      try {
        tabularResponse = _sessionOptions.getRESTDataProvider().getTabularResponse(target);
      } catch (QuandlTooManyRequestsException qtmre) {
        s_logger.debug("Quandl returned Too Many Requests, retrying if appropriate");
        if (qtmre.isDataExhausted()) {
          throw new QuandlRequestFailedException("Data request limit exceeded", qtmre);
        }
      } catch (QuandlServiceUnavailableException qsue) {
        s_logger.debug("Quandl returned Service Not Available, retrying if appropriate");
      }
      // note checkRetries always returns true or throws an exception so we won't get tabularReponse == null
    } while (tabularResponse == null && _sessionOptions.getRetryPolicy().checkRetries(retries++));

    return tabularResponse;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.jimmoores.quandl.QuandlSessionInterface#getMetaData(com.jimmoores.quandl.MetaDataRequest)
   */
  public MetaDataResult getMetaData(final MetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL_V3);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    JSONObject object = null;
    int retries = 0;
    do {
      try {
        object = _sessionOptions.getRESTDataProvider().getJSONResponse(target);
      } catch (QuandlTooManyRequestsException qtmre) {
        s_logger.debug("Quandl returned Too Many Requests, retrying if appropriate");
        if (qtmre.isDataExhausted()) {
          throw new QuandlRequestFailedException("Data request limit exceeded", qtmre);
        }
      } catch (QuandlServiceUnavailableException qsue) {
        s_logger.debug("Quandl returned Service Not Available, retrying if appropriate");
      }
      // note checkRetries always returns true or throws an exception so we won't get object == null
    } while (object == null && _sessionOptions.getRetryPolicy().checkRetries(retries++));
    return MetaDataResult.of(object);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.jimmoores.quandl.QuandlSessionInterface#getDataSets(com.jimmoores.quandl.MultiDataSetRequest)
   */
  public TabularResult getDataSets(final MultiDataSetRequest request) {
    final List<QuandlCodeRequest> quandlCodeRequests = request.getQuandlCodeRequests();
    final Map<QuandlCodeRequest, TabularResult> results = new LinkedHashMap<QuandlCodeRequest, TabularResult>();
    for (final QuandlCodeRequest quandlCodeRequest : quandlCodeRequests) {
      final Builder builder = DataSetRequest.Builder.of(quandlCodeRequest.getQuandlCode());
      if (quandlCodeRequest.isSingleColumnRequest()) {
        builder.withColumn(quandlCodeRequest.getColumnNumber());
      }
      if (request.getEndDate() != null) {
        builder.withEndDate(request.getEndDate());
      }
      if (request.getStartDate() != null) {
        builder.withStartDate(request.getStartDate());
      }
      if (request.getFrequency() != null) {
        builder.withFrequency(request.getFrequency());
      }
      if (request.getMaxRows() != null) {
        builder.withMaxRows(request.getMaxRows());
      }
      if (request.getSortOrder() != null) {
        builder.withSortOrder(request.getSortOrder());
      }
      if (request.getTransform() != null) {
        builder.withTransform(request.getTransform());
      }
      final DataSetRequest dataSetRequest = builder.build();
      TabularResult tabularResult = null;
      try {
        tabularResult = getDataSet(dataSetRequest);
      } catch (final QuandlRuntimeException qre) {
        s_logger.error("Exception processing request for {}, giving up and skipping.  Full request was {}",
            quandlCodeRequest.getQuandlCode(), dataSetRequest, qre);
        continue;
      }
      if (tabularResult != null) {
        results.put(quandlCodeRequest, tabularResult);
      } else {
        s_logger.error("Can't process request for {}, returned null.  Giving up and skipping.  Full request was {}",
            quandlCodeRequest.getQuandlCode(), dataSetRequest);
      }
    }
    return mergeTables(results, request.getSortOrder());
  }

  private TabularResult mergeTables(final Map<QuandlCodeRequest, TabularResult> results, final SortOrder sortOrder) {
    int resultTableWidth = 1; // the date!
    final Map<QuandlCodeRequest, Integer> initialOffset = new HashMap<QuandlCodeRequest, Integer>();
    final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Date");
    for (final Map.Entry<QuandlCodeRequest, TabularResult> entry : results.entrySet()) {
      final QuandlCodeRequest codeRequest = entry.getKey();
      final TabularResult table = entry.getValue();
      if (!initialOffset.containsKey(codeRequest)) {
        initialOffset.put(codeRequest, resultTableWidth); // record the offset for each table
      }
      resultTableWidth += table.getHeaderDefinition().size() - 1; // exclude the date column.
      final List<String> names = table.getHeaderDefinition().getColumnNames();
      final Iterator<String> iter = names.iterator();
      if (!iter.hasNext()) {
        throw new QuandlRuntimeException("table has no columns, expected at least date");
      }
      iter.next(); // discard date column name
      while (iter.hasNext()) {
        final String colName = iter.next();
        columnNames.add(codeRequest.getQuandlCode() + " - " + colName);
      }
    }
    final Comparator<ChronoLocalDate> comparator;
    if (sortOrder == SortOrder.ASCENDING) {
      comparator = LocalDate.timeLineOrder();
    } else {
      comparator = Collections.reverseOrder(LocalDate.timeLineOrder());
    }
    final SortedMap<LocalDate, String[]> rows = new TreeMap<LocalDate, String[]>(comparator);
    for (final Map.Entry<QuandlCodeRequest, TabularResult> mapEntry : results.entrySet()) {
      final QuandlCodeRequest codeRequest = mapEntry.getKey();
      final TabularResult table1 = mapEntry.getValue();
      final Iterator<Row> rowIter = table1.iterator();
      while (rowIter.hasNext()) {
        final Row row = rowIter.next();
        final LocalDate date = row.getLocalDate(0);
        final String dateStr = row.getString(0);
        if (date != null) {
          String[] bigRow;
          if (rows.containsKey(date)) {
            bigRow = rows.get(date);
          } else {
            bigRow = new String[resultTableWidth];
            rows.put(date, bigRow);
          }
          for (int i = 1; i < row.size(); i++) {
            bigRow[initialOffset.get(codeRequest) + (i - 1)] = row.getString(i); // (i-1 is becuase initialOffset index already includes
                                                                                 // initial 1 offset)
          }
          bigRow[0] = dateStr; // (re)write the date string at the start of the big table.
        }
      }
    }
    final List<Row> combinedRows = new ArrayList<Row>();
    final HeaderDefinition headerDefinition = HeaderDefinition.of(columnNames);
    for (final Entry<LocalDate, String[]> entry : rows.entrySet()) {
      final Row row = Row.of(headerDefinition, entry.getValue());
      combinedRows.add(row);
    }
    return TabularResult.of(headerDefinition, combinedRows);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.jimmoores.quandl.QuandlSessionInterface#getMetaData(com.jimmoores.quandl.MultiMetaDataRequest)
   */
  public MetaDataResult getMetaData(final MultiMetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    Map<String, HeaderDefinition> multipleHeaderDefinition = getMultipleHeaderDefinition(request);
    try {
      JSONObject result = new JSONObject();
      if (multipleHeaderDefinition.size() > 0) {
        result.append(JSON_COLUMN_NAMES_FIELD, DATE_COLUMN);
        result.append(JSON_COLUMNS_FIELD, DATE_COLUMN);
      }
      for (Map.Entry<String, HeaderDefinition> entry : multipleHeaderDefinition.entrySet()) {
        String quandlCode = entry.getKey();
        HeaderDefinition headerDef = entry.getValue();
        for (String columnName : headerDef.getColumnNames()) {
          if (!columnName.equals(DATE_COLUMN)) { // skip Date column for each data set.
            result.append(JSON_COLUMN_NAMES_FIELD, quandlCode + " - " + columnName);
            result.append(JSON_COLUMNS_FIELD, columnName);
          }
        }
      }
      result.put(JSON_DATA_FIELD, new JSONArray());
      result.put(JSON_ERRORS_FIELD, Collections.emptyMap());
      result.put(JSON_FREQUENCY_FIELD, (Object) null);
      result.put(JSON_FROM_DATE_FIELD, (Object) null);
      result.put(JSON_TO_DATE_FIELD, (Object) null);
      return MetaDataResult.of(result);
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Problem building JSON response", ex);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.jimmoores.quandl.QuandlSessionInterface#getMultipleHeaderDefinition(com.jimmoores.quandl.MultiMetaDataRequest)
   */
  public Map<String, HeaderDefinition> getMultipleHeaderDefinition(final MultiMetaDataRequest request) {
    final Map<String, HeaderDefinition> bulkMetaData = new LinkedHashMap<String, HeaderDefinition>();
    for (final String quandlCode : request.getQuandlCodes()) {
      try {
        MetaDataResult metaData = getMetaData(MetaDataRequest.of(quandlCode));
        bulkMetaData.put(quandlCode, metaData.getHeaderDefinition());
      } catch (final QuandlRuntimeException qre) {
        s_logger.error("There was a problem requesting metadata for {}, skipping", quandlCode, qre);
        continue;
      }
    }
    return bulkMetaData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.jimmoores.quandl.QuandlSessionInterface#search(com.jimmoores.quandl.SearchRequest)
   */
  public SearchResult search(final SearchRequest request) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL_V3);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    int retries = 0;
    JSONObject jsonResponse = null;
    do {
      try {
        jsonResponse = _sessionOptions.getRESTDataProvider().getJSONResponse(target);
      } catch (QuandlTooManyRequestsException qtmre) {
        s_logger.debug("Quandl returned Too Many Requests, retrying if appropriate");
        if (qtmre.isDataExhausted()) {
          throw new QuandlRequestFailedException("Data request limit exceeded", qtmre);
        }
      } catch (QuandlServiceUnavailableException qsue) {
        s_logger.debug("Quandl returned Service Not Available, retrying if appropriate");
      }
      // note checkRetries always returns true or throws an exception so we won't get jsonReponse == null
    } while (jsonResponse == null && _sessionOptions.getRetryPolicy().checkRetries(retries++));
    SearchResult searchResult = SearchResult.of(jsonResponse);
    return searchResult;
  }
}

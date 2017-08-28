package com.jimmoores.quandl.generic;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SessionOptions;
import com.jimmoores.quandl.processing.GenericRESTDataProvider;
import com.jimmoores.quandl.processing.MetaDataPackager;
import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.QuandlRequestFailedException;
import com.jimmoores.quandl.util.QuandlServiceUnavailableException;
import com.jimmoores.quandl.util.QuandlTooManyRequestsException;

/**
 * Generic Quandl session class. You would generally not use this directly, instead
 * <ul>
 * <li>@see com.mcleodmoores.quandl.classic.ClassicQuandlSession</li>
 * <li>@see com.mcleodmoores.quandl.tablesaw.TableSawQuandlSession</li>
 * </ul>
 * @param <METADATA_TYPE>  the wrapper type used to contain returned metadata, which may interpret it.
 * @param <RAW_METADATA_TYPE>  the type used to hold raw metadata (e.g. JSONObject).
 * @param <TABLE_TYPE>  the type used to return tabular data (e.g. TabularResult or tablesaw Table)
 * @param <SEARCH_RESULT_TYPE>  the wrapper type used to contain search result metadata.
 */
public class GenericQuandlSession<METADATA_TYPE, RAW_METADATA_TYPE, TABLE_TYPE, SEARCH_RESULT_TYPE>
    implements GenericQuandlSessionInterface<METADATA_TYPE, TABLE_TYPE, SEARCH_RESULT_TYPE> {
  private static final UriBuilder API_BASE_URL_V3 = UriBuilder.fromPath("https://www.quandl.com/api/v3");

  private static Logger s_logger = LoggerFactory.getLogger(GenericQuandlSession.class);

  private SessionOptions _sessionOptions;
  private GenericRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE> _restDataProvider;
  private MetaDataPackager<METADATA_TYPE, RAW_METADATA_TYPE, SEARCH_RESULT_TYPE> _metaDataPackager;

  /**
   * The property name used to pass Quandl auth tokens into the JVM.
   */
  public static final String QUANDL_AUTH_TOKEN_PROPERTY_NAME = "quandl.auth.token";

  /**
   * Create a generic quandl session.
   * @param sessionOptions  the session options
   * @param restDataProvider  the rest data provider, which reads and encodes data
   * @param metaDataPackager  the meta data packager, which packages meta data
   */
  public GenericQuandlSession(final SessionOptions sessionOptions,
      final GenericRESTDataProvider<RAW_METADATA_TYPE, TABLE_TYPE> restDataProvider,
      final MetaDataPackager<METADATA_TYPE, RAW_METADATA_TYPE, SEARCH_RESULT_TYPE> metaDataPackager) {
    _sessionOptions = sessionOptions;
    _restDataProvider = restDataProvider;
    _metaDataPackager = metaDataPackager;
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

  /**
   * {@inheritDoc}
   */
  public TABLE_TYPE getDataSet(final DataSetRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = getClient();
    WebTarget target = client.target(API_BASE_URL_V3);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    TABLE_TYPE tabularResponse = null;
    int retries = 0;
    do {
      try {
        tabularResponse = _restDataProvider.getTabularResponse(target, request);
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

  /**
   * {@inheritDoc}
   */
  public METADATA_TYPE getMetaData(final MetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL_V3);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    RAW_METADATA_TYPE object = null;
    int retries = 0;
    do {
      try {
        object = _restDataProvider.getJSONResponse(target, request);
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
    return _metaDataPackager.ofMetaData(object);
  }

  /**
   * {@inheritDoc}
   */
  public SEARCH_RESULT_TYPE search(final SearchRequest request) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(API_BASE_URL_V3);
    target = withAuthToken(target);
    target = request.appendPathAndQueryParameters(target);
    int retries = 0;
    RAW_METADATA_TYPE jsonResponse = null;
    do {
      try {
        jsonResponse = _restDataProvider.getJSONResponse(target, request);
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
    return _metaDataPackager.ofSearchResult(jsonResponse);
  }
}

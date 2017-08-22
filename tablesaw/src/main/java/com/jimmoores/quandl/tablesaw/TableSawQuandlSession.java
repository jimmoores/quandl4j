package com.jimmoores.quandl.tablesaw;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.SessionOptions;
import com.jimmoores.quandl.generic.GenericQuandlSession;
import com.jimmoores.quandl.processing.ClassicMetaDataPackager;
import com.jimmoores.quandl.processing.GenericRESTDataProvider;
import com.jimmoores.quandl.processing.MetaDataPackager;
import com.jimmoores.quandl.processing.tablesaw.JSONTableSawRESTDataProvider;
import com.jimmoores.quandl.util.ArgumentChecker;

import tech.tablesaw.api.Table;

public class TableSawQuandlSession extends GenericQuandlSession<MetaDataResult, JSONObject, Table, SearchResult>{
  private static final Logger LOGGER = LoggerFactory.getLogger(TableSawQuandlSession.class);
  public TableSawQuandlSession(SessionOptions sessionOptions, GenericRESTDataProvider<JSONObject, Table> restDataProvider,
      MetaDataPackager<MetaDataResult, JSONObject, SearchResult> metaDataPackager) {
    super(sessionOptions, restDataProvider, metaDataPackager);
  }
  
  /**
   * Create a Quandl session without an authorization token (authToken). An attempt will be made to read the java property
   * <em>quandl.auth.token</em> and use that if available. Any resulting SecurityException is logged at debug level, otherwise it is ignored
   * and no auth token is used. Using a token means you can make more requests. Note creating this object does not make any actual API
   * requests, the token is used in subsequent requests.
   * 
   * @return an instance of the Quandl session, not null.
   */
  public static TableSawQuandlSession create() {
    try {
      String authToken = System.getProperty(QUANDL_AUTH_TOKEN_PROPERTY_NAME);
      if (authToken != null) {
        return new TableSawQuandlSession(
            SessionOptions.Builder.withAuthToken(authToken).build(), 
            new JSONTableSawRESTDataProvider(), 
            new ClassicMetaDataPackager());
      }
    } catch (SecurityException se) {
      LOGGER.debug("Error accessing system property " + QUANDL_AUTH_TOKEN_PROPERTY_NAME + ", falling back to not using an auth token",
          se);
    }
    return new TableSawQuandlSession(SessionOptions.Builder.withoutAuthToken().build(), 
        new JSONTableSawRESTDataProvider(), new ClassicMetaDataPackager());
  }

  /**
   * Create a Quandl session with detailed SessionOptions. No attempt will be made to read the java property <em>quandl.auth.token</em> even
   * if available. Note creating this object does not make any actual API requests, the token is used in subsequent requests.
   * 
   * @param sessionOptions a user created SessionOptions instance, not null
   * @return an instance of the Quandl session, not null
   */
  public static TableSawQuandlSession create(final SessionOptions sessionOptions) {
    ArgumentChecker.notNull(sessionOptions, "sessionOptions");
    return new TableSawQuandlSession(
        sessionOptions, 
        new JSONTableSawRESTDataProvider(), 
        new ClassicMetaDataPackager());
  }
  
}

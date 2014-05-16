package com.jimmoores.quandl;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.DefaultRESTDataProvider;
import com.jimmoores.quandl.util.RESTDataProvider;

/**
 * Class for specifying any non-trivial options to a QuandlSession.
 */
public final class SessionOptions {
  private String _authToken;
  private RESTDataProvider _restDataProvider;
  
  private SessionOptions(final Builder builder) {
    _authToken = builder._authToken;
    _restDataProvider = builder._restDataProvider;
  }
  
  /**
   * Internal Builder class.
   */
  public static final class Builder {
    private String _authToken;
    private RESTDataProvider _restDataProvider = new DefaultRESTDataProvider();

    private Builder(final String authToken) {
      _authToken = authToken;
    }
    
    /**
     * Specify a specific auth token.
     * @param authToken your auth token
     * @return a Builder object for chaining, call build() to complete
     */
    public static Builder withAuthToken(final String authToken) {
      ArgumentChecker.notNull(authToken, "authToken");
      return new Builder(authToken);
    }

    /**
     * Specify no auth token.
     * @return a Builder object for chaining, call build() to complete
     */
    public static Builder withoutAuthToken() {
      return new Builder(null);
    }
    
    /**
     * Specify a custom RESTDataProvider for the session to use when sending requests, intended for testing purposes.
     * @param restDataProvider a RESTDataProvider for the session
     * @return this builder
     */
    public Builder withRESTDataProvider(final RESTDataProvider restDataProvider) {
      ArgumentChecker.notNull(restDataProvider, "restDataProvider");
      _restDataProvider = restDataProvider;
      return this;
    }
    /**
     * Build an instance of QuandlOptions using the parameters in this builder instance.
     * @return an instance of QuandlOptions
     */
    public SessionOptions build() {
      return new SessionOptions(this);
    }
  }
  
  /**
   * Get the Quandl auth token stored in this SessionOptions, or null if none was specified.
   * @return the quandl API auth token String, or null if none was specified
   */
  public String getAuthToken() {
    return _authToken;
  }
  
  /**
   * Get the REST data provider to use when sending API requests to Quandl.
   * @return the REST data provider, not null
   */
  public RESTDataProvider getRESTDataProvider() {
    return _restDataProvider;
  }
}

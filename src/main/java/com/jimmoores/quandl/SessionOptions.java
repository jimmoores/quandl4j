package com.jimmoores.quandl;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.DefaultRESTDataProvider;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.RESTDataProvider;

/**
 * Class for specifying any non-trivial options to a QuandlSession.
 */
public final class SessionOptions {
  private String _authToken;
  private RESTDataProvider _restDataProvider;
  private RetryPolicy _retryPolicy;
  
  private SessionOptions(final Builder builder) {
    _authToken = builder._authToken;
    _restDataProvider = builder._restDataProvider;
    _retryPolicy = builder._retryPolicy;
  }
  
  /**
   * Internal Builder class.
   */
  public static final class Builder {
    private static final long ONE_SECOND = 1000L;
    private static final long FIVE_SECONDS = 5000L;
    private static final long TWENTY_SECONDS = 20000L;
    private static final long SIXTY_SECONDS = 60000L;

    private String _authToken;
    private RESTDataProvider _restDataProvider = new DefaultRESTDataProvider();
    private RetryPolicy _retryPolicy = RetryPolicy.createSequenceRetryPolicy(new long[] { ONE_SECOND, FIVE_SECONDS, TWENTY_SECONDS, SIXTY_SECONDS });

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
     * Specify the number of retries to execute before giving up on a request.
     * @param retryPolicy  the policy to follow regarding retries
     * @return this builder
     */
    public Builder withRetryPolicy(final RetryPolicy retryPolicy) {
      ArgumentChecker.notNull(retryPolicy, "retryPolicy");
      _retryPolicy = retryPolicy;
      return this;
    }
    
    /**
     * Specify the length of time to wait before retrying
     */
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
  
  /**
   * Get the RetryPolicy to use in determining retry behaviour when calls to Quandl fail.
   * The default is a four stage back-off of 1 second, 5 seconds, 20 seconds and lastly 60 seconds.
   * @return the RetryPolicy
   */
  public RetryPolicy getRetryPolicy() {
    return _retryPolicy;
  }
}

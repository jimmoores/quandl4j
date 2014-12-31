package com.jimmoores.quandl;

import com.jimmoores.quandl.util.QuandlRequestFailedException;
import com.jimmoores.quandl.util.QuandlRuntimeException;

public abstract class RetryPolicy {

  public abstract boolean checkRetries(int retries);
  
  public static RetryPolicy createNoRetryPolicy() {
    return new NoRetryPolicy();
  }
  
  public static RetryPolicy createFixedRetryPolicy(int maxRetries, long backOffPeriod) {
    return new FixedRetryPolicy(maxRetries, backOffPeriod);
  }
  
  public static RetryPolicy createSequenceRetryPolicy(long[] backOffPeriods) {
    return new SequenceRetryPolicy(backOffPeriods);
  }
  
  private static class NoRetryPolicy extends RetryPolicy {
    private NoRetryPolicy() {
    }
    
    public boolean checkRetries(int retries) {
      throw new QuandlRuntimeException("Request failed, policy is no retry.");
    }
  }
  
  private static class FixedRetryPolicy extends RetryPolicy {
    private int _maxRetries;
    private long _backOffPeriod;

    private FixedRetryPolicy(int maxRetries, long backOffPeriod) {
      _maxRetries = maxRetries;
      _backOffPeriod = backOffPeriod;
    }
    
    public boolean checkRetries(int retries) {
      if (retries < _maxRetries && retries >= 0) {
        try {
          Thread.sleep(_backOffPeriod);
        } catch (InterruptedException ie) {
          throw new QuandlRequestFailedException("Giving up on request, received InterruptedException", ie);
        }
      } else {
        throw new QuandlRequestFailedException("Giving up on request after " + _maxRetries + " retries of " + _backOffPeriod + "ms each.");
      }
      return true;
    }
  }
  
  private static class SequenceRetryPolicy extends RetryPolicy {
    private int _maxRetries;
    private long[] _backOffPeriods;

    private SequenceRetryPolicy(long[] backOffPeriods) {
      _maxRetries = backOffPeriods.length;
      _backOffPeriods = backOffPeriods;
    }
    
    public boolean checkRetries(int retries) {
      if (retries < _maxRetries && retries >= 0) {
        try {
          Thread.sleep(_backOffPeriods[retries]);
        } catch (InterruptedException ie) {
          throw new QuandlRequestFailedException("Giving up on request, received InterruptedException", ie);
        }
      } else {
        throw new QuandlRequestFailedException("Giving up on request after " + _maxRetries);
      } 
      return true;
    }
  }
}

package com.jimmoores.quandl;

import com.jimmoores.quandl.util.QuandlRequestFailedException;

/**
 * Class containing multiple retry policies.
 */
public abstract class RetryPolicy {

  /**
   * Check if we should retry given current number of retries.
   * 
   * @param retries the current number of retries that have occurred
   * @return true, if the caller should retry
   */
  public abstract boolean checkRetries(int retries);

  /**
   * Create a retry policy that never retries.
   * 
   * @return the retry policy
   */
  public static RetryPolicy createNoRetryPolicy() {
    return new NoRetryPolicy();
  }

  /**
   * Create a retry policy that retries a fixed number of times with a fixed interval.
   * 
   * @param maxRetries the maximum allowable number of retries
   * @param backOffPeriod the period to wait before retrying, in milliseconds
   * @return the retry policy
   */
  public static RetryPolicy createFixedRetryPolicy(final int maxRetries, final long backOffPeriod) {
    return new FixedRetryPolicy(maxRetries, backOffPeriod);
  }

  /**
   * Create a retry policy that retries with a provided set of back off periods. This allows the user to perform an exponential backoff, for
   * example.
   * 
   * @param backOffPeriods an array of times to wait between retries, in milliseconds
   * @return the retry policy
   */
  public static RetryPolicy createSequenceRetryPolicy(final long[] backOffPeriods) {
    return new SequenceRetryPolicy(backOffPeriods);
  }

  /**
   * No Retry Policy.
   */
  private static final class NoRetryPolicy extends RetryPolicy {
    private NoRetryPolicy() {
    }

    @Override
    public boolean checkRetries(final int retries) {
      return false;
    }
  }

  /**
   * Fixed retry policy.
   */
  private static final class FixedRetryPolicy extends RetryPolicy {
    private int _maxRetries;
    private long _backOffPeriod;

    private FixedRetryPolicy(final int maxRetries, final long backOffPeriod) {
      _maxRetries = maxRetries;
      _backOffPeriod = backOffPeriod;
    }

    @Override
    public boolean checkRetries(final int retries) {
      if (retries < _maxRetries && retries >= 0) {
        try {
          Thread.sleep(_backOffPeriod);
        } catch (InterruptedException ie) {
          throw new QuandlRequestFailedException("Giving up on request, received InterruptedException", ie);
        }
      } else {
        return false;
      }
      return true;
    }
  }

  /**
   * Sequence retry policy.
   */
  private static final class SequenceRetryPolicy extends RetryPolicy {
    private int _maxRetries;
    private long[] _backOffPeriods;

    private SequenceRetryPolicy(final long[] backOffPeriods) {
      _maxRetries = backOffPeriods.length;
      _backOffPeriods = backOffPeriods;
    }

    @Override
    public boolean checkRetries(final int retries) {
      if (retries < _maxRetries && retries >= 0) {
        try {
          Thread.sleep(_backOffPeriods[retries]);
        } catch (InterruptedException ie) {
          throw new QuandlRequestFailedException("Giving up on request, received InterruptedException", ie);
        }
      } else {
        return false;
      }
      return true;
    }
  }
}

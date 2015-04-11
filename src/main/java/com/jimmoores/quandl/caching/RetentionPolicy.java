package com.jimmoores.quandl.caching;

import com.jimmoores.quandl.Frequency;

import java.util.Calendar;

/**
 * RetentionPolicy describes when we should reload data from quandl, instead of getting it from the cache.
 */
public abstract class RetentionPolicy {
  /**
   * Check if a reload should happen given the creation time of the data file.
   * @param creationTime  the date and time when the cache file was created as a Calendar object
   * @return true if based on this RetentionPolicy the cached data is too old
   */
  abstract boolean shouldReload(Calendar creationTime);

  /**
   * Create a retention policy given a frequency.
   * @param frequency  the frequency of update
   * @return a retention policy
   */
  public static RetentionPolicy create(final Frequency frequency) {
    if (frequency == null) {
      return null;
    }
    switch (frequency) {
      case NONE:
        return NEVER;
      case DAILY:
        return DAY;
      case WEEKLY:
        return WEEK;
      case MONTHLY:
        return MONTH;
      case QUARTERLY:
        return MONTH;
      case ANNUAL:
        return YEAR;
      default:
        return NEVER;
    }
  }

  /**
   * A retention policy that reloads if the same a different day.
   */
  public static final RetentionPolicy DAY = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final Calendar creationTime) {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime.get(Calendar.YEAR))
        && (now.get(Calendar.MONTH) == creationTime.get(Calendar.MONTH))
        && (now.get(Calendar.DAY_OF_MONTH) == creationTime.get(Calendar.DAY_OF_MONTH)));
    }
  };

  /**
   * A retention policy that reloads if a different week from the data.
   */
  public static final RetentionPolicy WEEK = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final Calendar creationTime) {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime.get(Calendar.YEAR))
        && (now.get(Calendar.WEEK_OF_YEAR) == creationTime.get(Calendar.WEEK_OF_YEAR))
      );
    }
  };

  /**
   * A retention policy that reloads if a different month from the data.
   */
  public static final RetentionPolicy MONTH = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final Calendar creationTime) {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime.get(Calendar.YEAR))
        && (now.get(Calendar.MONTH) == creationTime.get(Calendar.MONTH))
      );
    }
  };

  /**
   * A retention policy that reloads if a different year from the data.
   */
  public static final RetentionPolicy YEAR = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final Calendar creationTime) {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime.get(Calendar.YEAR))
      );
    }
  };

  /**
   * A retention policy that never reloads the data.
   */
  public static final RetentionPolicy NEVER = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final Calendar creationTime) {
      return true;
    }
  };
}

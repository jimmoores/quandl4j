package com.jimmoores.quandl.caching;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.Frequency;

/**
 * RetentionPolicy describes when we should reload data from quandl, instead of getting it from the cache.
 */
public abstract class RetentionPolicy {
  /**
   * Check if a reload should happen given the creation time of the data file.
   * @param creationTime  the date and time when the cache file was created as a Calendar object
   * @return true if based on this RetentionPolicy the cached data is too old
   */
  abstract boolean shouldReload(LocalDate creationTime);

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
    public boolean shouldReload(final LocalDate creationTime) {
      LocalDate now = LocalDate.now();
      return !now.equals(creationTime);
    }
  };

  private static final int ONE_WEEK = 7;

  /**
   * A retention policy that reloads if a different week from the data.
   */
  public static final RetentionPolicy WEEK = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final LocalDate creationTime) {
      LocalDate now = LocalDate.now();
      return creationTime.until(now).getDays() > ONE_WEEK;

    }
  };

  /**
   * A retention policy that reloads if a different month from the data.
   */
  public static final RetentionPolicy MONTH = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final LocalDate creationTime) {
      LocalDate now = LocalDate.now();
      return creationTime.until(now).toTotalMonths() > 1;
    }
  };

  private static final long TWELVE_MONTHS = 12;

  /**
   * A retention policy that reloads if a different year from the data.
   */
  public static final RetentionPolicy YEAR = new RetentionPolicy() {
    public boolean shouldReload(final LocalDate creationTime) {
      LocalDate now = LocalDate.now();
      return creationTime.until(now).toTotalMonths() > TWELVE_MONTHS;
    }
  };

  /**
   * A retention policy that never reloads the data.
   */
  public static final RetentionPolicy NEVER = new RetentionPolicy() {
    @Override
    public boolean shouldReload(final LocalDate creationTime) {
      return true;
    }
  };
}

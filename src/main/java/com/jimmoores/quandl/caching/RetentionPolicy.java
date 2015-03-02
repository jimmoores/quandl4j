package com.jimmoores.quandl.caching;

import com.jimmoores.quandl.Frequency;

import java.util.Calendar;

/**
 * RetentionPolicy describes when we should reload data from quandl, instead of getting it from the cache
 */
public abstract class RetentionPolicy
{
  /**
   *
   * @return true if based on this RetentionPolicy the cached data is too old
   * @param creationTime_ the date and time when the cache file was created as a Calendar object
   */
  abstract boolean shouldReload(Calendar creationTime_);

  public static RetentionPolicy create(Frequency frequency_)
  {
    if (frequency_ == null)
    {
      return null;
    }
    switch (frequency_)
    {
      case NONE:
        return Never;
      case DAILY:
        return Day;
      case WEEKLY:
        return Week;
      case MONTHLY:
        return Month;
      case QUARTERLY:
        return Month;
      case ANNUAL:
        return Year;
      default:
        return Never;
    }
  }

  public static RetentionPolicy Day = new RetentionPolicy()
  {
    @Override
    public boolean shouldReload(Calendar creationTime_)
    {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime_.get(Calendar.YEAR))
        && (now.get(Calendar.MONTH) == creationTime_.get(Calendar.MONTH))
        && (now.get(Calendar.DAY_OF_MONTH) == creationTime_.get(Calendar.DAY_OF_MONTH)));
    }
  };

  public static RetentionPolicy Week = new RetentionPolicy()
  {
    @Override
    public boolean shouldReload(Calendar creationTime_)
    {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime_.get(Calendar.YEAR))
        && (now.get(Calendar.WEEK_OF_YEAR) == creationTime_.get(Calendar.WEEK_OF_YEAR))
      );
    }
  };

  public static RetentionPolicy Month = new RetentionPolicy()
  {
    @Override
    public boolean shouldReload(Calendar creationTime_)
    {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime_.get(Calendar.YEAR))
        && (now.get(Calendar.MONTH) == creationTime_.get(Calendar.MONTH))
      );
    }
  };

  public static RetentionPolicy Year = new RetentionPolicy()
  {
    @Override
    public boolean shouldReload(Calendar creationTime_)
    {
      Calendar now = Calendar.getInstance();
      return !((now.get(Calendar.YEAR) == creationTime_.get(Calendar.YEAR))
      );
    }
  };

  public static RetentionPolicy Never = new RetentionPolicy()
  {
    @Override
    public boolean shouldReload(Calendar creationTime_)
    {
      return true;
    }
  };
}

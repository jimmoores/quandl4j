package com.jimmoores.quandl;

import javax.ws.rs.client.WebTarget;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * Class for a tabular data set request to Quandl.  
 * Start by calling the static of() method on the inner Builder class and build up the request using other
 * methods as necessary.  An example:
 * <pre>
 *   DataSetRequest.Builder.of("WIKI/APPL")
 *                         .withColumn(4)
 *                         .withFrequency(Frequency.ANNUAL)
 *                         .withStartDate(LocalDate.of(2000, 1, 1))
 *                         .withEndDate(LocalDate.of(2010, 1, 1)
 *                         .withSortOrder(SortOrder.ASCENDING)
 *                         .withTransform(Transform.RDIFF)
 *                         .build();
 * </pre>
 * The resulting object should be passed into one of the methods in the QuandlConnector class.  If anything is
 * not specified, it will not be included in the request and so the results will reflect the default
 * Quandl behavior (e.g. all columns, no row limits, etc).
 */
public final class DataSetRequest {
  private static final String START_DATE_PARAM = "trim_start";
  private static final String END_DATE_PARAM = "trim_end";
  private static final String COLUMN_INDEX_PARAM = "column";
  private static final String FREQUENCY_PARAM = "collapse";
  private static final String MAX_ROWS_PARAM = "rows";
  private static final String TRANSFORM_PARAM = "transformation";
  private static final String SORT_ORDER_PARAM = "sort_order";
  
  private static final String EXTENSION = ".csv";
  private static final String DATASETS_RELATIVE_URL = "datasets";

  private final String _quandlCode;
  private LocalDate _startDate;
  private LocalDate _endDate;
  private Integer _columnIndex;
  private Frequency _frequency;
  private Integer _maxRows;
  private Transform _transform;
  private SortOrder _sortOrder;

  private DataSetRequest(final Builder builder) {
    // Note the builder may contain nulls for some fields and that's fine, means 'not specified'.
    _quandlCode = builder._quandlCode;
    _startDate = builder._startDate;
    _endDate = builder._endDate;
    _columnIndex = builder._columnIndex;
    _frequency = builder._frequency;
    _maxRows = builder._maxRows;
    _transform = builder._transform;
    _sortOrder = builder._sortOrder;
  }

  /**
   *
   * @return the quandl code corresponding with the request
   */
  public String getQuandlCode()
  {
    return _quandlCode;
  }

  /**
   *
   * @return the Frequency that was set
   */
  public Frequency getFrequency()
  {
    return _frequency;
  }
  /**
   * Inner builder class.  Create an instance using of("QUANDL/CODE"), call any other
   * methods you need, and finish by calling build().
   */
  public static final class Builder {
    private final String _quandlCode;
    private LocalDate _startDate;
    private LocalDate _endDate;
    private Integer _columnIndex;
    private Frequency _frequency;
    private Integer _maxRows;
    private Transform _transform;
    private SortOrder _sortOrder;

    private Builder(final String quandlCode) {
      _quandlCode = quandlCode;
    }

    /**
     * Create the base DataSetRequest object passing in the Quandl code.
     * @param quandlCode the quandl code for the data you're interested in, not null
     * @return a Builder instance, not null
     */
    public static Builder of(final String quandlCode) {
      ArgumentChecker.notNull(quandlCode, "quandlCode");
      return new Builder(quandlCode);
    }

    /**
     * Optionally specify a start date cut-off for the request.
     * @param startDate the start date of the request (inclusive), not null
     * @return a Builder instance, not null
     */
    public Builder withStartDate(final LocalDate startDate) {
      ArgumentChecker.notNull(startDate, "startDate");
      _startDate = startDate;
      return this;
    }

    /**
     * Optionally specify an end date cut-off for the request.
     * @param endDate the end date of the request (inclusive), not null
     * @return a Builder instance, not null
     */
    public Builder withEndDate(final LocalDate endDate) {
      ArgumentChecker.notNull(endDate, "endDate");
      _endDate = endDate;
      return this;
    }

    /**
     * Optionally specify a specific column for the request.  This can only be used once per request.
     * @param columnIndex the zero-based column index being requested
     * @return a Builder instance, not null
     */
    public Builder withColumn(final int columnIndex) {
      _columnIndex = columnIndex;
      return this;
    }

    /**
     * Optionally specify the sampling frequency for the request.
     * @param frequency the sampling frequency, not null
     * @return a Builder instance, not null
     */
    public Builder withFrequency(final Frequency frequency) {
      ArgumentChecker.notNull(frequency, "frequency");
      _frequency = frequency;
      return this;
    }

    /**
     * Optionally specify the maximum number of rows that should be returns for the request.
     * @param maxRows the maximum number of rows that the server should return from the request
     * @return a Builder instance, not null
     */
    public Builder withMaxRows(final int maxRows) {
      _maxRows = maxRows;
      return this;
    }

    /**
     * Optionally specify a data transformation function for the request.
     * @param transform the data transformation method that the server should pre-process the data with
     * @return a Builder instance, not null
     */
    public Builder withTransform(final Transform transform) {
      ArgumentChecker.notNull(transform, "transform");
      _transform = transform;
      return this;
    }

    /**
     * Optionally specify the sort order of the results.
     * @param sortOrder the sort order of the results
     * @return a Builder object onto which you can chain additional calls
     */
    public Builder withSortOrder(final SortOrder sortOrder) {
      ArgumentChecker.notNull(sortOrder, "sortOrder");
      _sortOrder = sortOrder;
      return this;
    }
    
    /**
     * Build the request object.
     * @return the immutable build object
     */
    public DataSetRequest build() {
      return new DataSetRequest(this);
    }
  }

  /**
   * Append any specified parameters to the provided WebTarget.
   * @param webTarget a web target used by the Jersey Client API
   * @return the WebTarget with any path and query parameters appended
   */
  public WebTarget appendPathAndQueryParameters(final WebTarget webTarget) {
    ArgumentChecker.notNull(webTarget, "webTarget");
    WebTarget resultTarget = webTarget;
    resultTarget = resultTarget.path(DATASETS_RELATIVE_URL);
    resultTarget = resultTarget.path(_quandlCode + EXTENSION);
    if (_startDate != null) {
      resultTarget = resultTarget.queryParam(START_DATE_PARAM, _startDate.toString());
    }
    if (_endDate != null) {
      resultTarget = resultTarget.queryParam(END_DATE_PARAM, _endDate.toString());
    }
    if (_columnIndex != null) {
      resultTarget = resultTarget.queryParam(COLUMN_INDEX_PARAM, _columnIndex);
    }
    if (_frequency != null) {
      resultTarget = resultTarget.queryParam(FREQUENCY_PARAM, _frequency.getQuandlString());
    }
    if (_maxRows != null) {
      resultTarget = resultTarget.queryParam(MAX_ROWS_PARAM, _maxRows);
    }
    if (_transform != null) {
      resultTarget = resultTarget.queryParam(TRANSFORM_PARAM, _transform.getQuandlString());
    }
    if (_sortOrder != null) {
      resultTarget = resultTarget.queryParam(SORT_ORDER_PARAM, _sortOrder.getQuandlString());
    }
    return resultTarget;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (_frequency != null) {
      result = (prime * result) + _frequency.hashCode();
    }
    if (_columnIndex != null) {
      result = (prime * result) + _columnIndex.hashCode();
    }
    if (_maxRows != null) {
      result = (prime * result) + _maxRows.hashCode();
    }
    if (_transform != null) {
      result = (prime * result) + _transform.hashCode();
    }
    if (_sortOrder != null) {
      result = (prime * result) + _sortOrder.hashCode();
    }
    if (_endDate != null) {
      result = (prime * result) + _endDate.hashCode();
    }
    if (_startDate != null) {
      result = (prime * result) + _startDate.hashCode();
    }
    // make sure most important fields in low order bits in case of integer overflow
    if (_quandlCode != null) { 
      result = (prime * result) + _quandlCode.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DataSetRequest)) {
      return false;
    }
    DataSetRequest other = (DataSetRequest) obj;
    if (_quandlCode == null) {
      if (other._quandlCode != null) {
        return false;
      }
    } else if (!_quandlCode.equals(other._quandlCode)) {
      return false;
    }
    if (_columnIndex == null) {
      if (other._columnIndex != null) {
        return false;
      }
    } else if (!_columnIndex.equals(other._columnIndex)) {
      return false;
    }
    if (_endDate == null) {
      if (other._endDate != null) {
        return false;
      }
    } else if (!_endDate.equals(other._endDate)) {
      return false;
    }
    if (_frequency != other._frequency) {
      return false;
    }
    if (_maxRows == null) {
      if (other._maxRows != null) {
        return false;
      }
    } else if (!_maxRows.equals(other._maxRows)) {
      return false;
    }

    if (_sortOrder != other._sortOrder) {
      return false;
    }
    if (_startDate == null) {
      if (other._startDate != null) {
        return false;
      }
    } else if (!_startDate.equals(other._startDate)) {
      return false;
    }
    if (_transform != other._transform) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DataSetRequest[quandlCode=");
    builder.append(_quandlCode);
    builder.append(", startDate=");
    builder.append(_startDate);
    builder.append(", endDate=");
    builder.append(_endDate);
    builder.append(", columnIndex=");
    builder.append(_columnIndex);
    builder.append(", frequency=");
    builder.append(_frequency);
    builder.append(", maxRows=");
    builder.append(_maxRows);
    builder.append(", transform=");
    builder.append(_transform);
    builder.append(", sortOrder=");
    builder.append(_sortOrder);
    builder.append("]");
    return builder.toString();
  }  
}

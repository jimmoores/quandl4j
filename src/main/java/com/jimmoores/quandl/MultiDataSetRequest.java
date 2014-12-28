package com.jimmoores.quandl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.client.WebTarget;

import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * Builder class for a tabular data set request to Quandl.  
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
public final class MultiDataSetRequest {
  private static final String COLUMNS_PARAM = "columns";
  private static final String START_DATE_PARAM = "trim_start";
  private static final String END_DATE_PARAM = "trim_end";
  private static final String FREQUENCY_PARAM = "collapse";
  private static final String MAX_ROWS_PARAM = "rows";
  private static final String TRANSFORM_PARAM = "transformation";
  private static final String SORT_ORDER_PARAM = "sort_order";
 
  private static final String MULTI_SET_NAME = "multisets";
  private static final String EXTENSION = ".csv";

  private final List<QuandlCodeRequest> _quandlCodeRequests;
  private final LocalDate _startDate;
  private final LocalDate _endDate;
  private final Frequency _frequency;
  private final Integer _maxRows;
  private final Transform _transform;
  private final SortOrder _sortOrder;

  private MultiDataSetRequest(final Builder builder) {
    _quandlCodeRequests = builder._quandlCodeRequests;
    _startDate = builder._startDate;
    _endDate = builder._endDate;
    _frequency = builder._frequency;
    _maxRows = builder._maxRows;
    _transform = builder._transform;
    _sortOrder = builder._sortOrder;
  }

  /**
   * Inner builder class.  Create an instance using of("QUANDL/CODE"), call any other
   * methods you need, and finish by calling build().  Note you are be able to use a
   * slash as opposed to the period separator as specified in the Quandl REST API docs, 
   * but either will work here.
   */
  public static final class Builder {
    private final List<QuandlCodeRequest> _quandlCodeRequests;
    private LocalDate _startDate;
    private LocalDate _endDate;
    private Frequency _frequency;
    private Integer _maxRows;
    private Transform _transform;
    private SortOrder _sortOrder;

    private Builder(final List<QuandlCodeRequest> quandlCodeRequests) {
      _quandlCodeRequests = quandlCodeRequests;
    }

    /**
     * Create the base DataSetRequest object passing in the Quandl code.
     * @param quandlCodeRequests the list of quandl codes/columns you're interested in, not null
     * @return a Builder instance, not null
     */
    public static Builder of(final List<QuandlCodeRequest> quandlCodeRequests) {
      ArgumentChecker.notNullOrEmpty(quandlCodeRequests, "quandlCodeRequests");
      return new Builder(quandlCodeRequests);
    }
    
    /**
     * Create the base DataSetRequest object passing in the Quandl code.
     * @param quandlCodeRequests the quandl codes/columns (VarArgs) you're interested in, not null
     * @return a Builder instance, not null
     */
    public static Builder of(final QuandlCodeRequest... quandlCodeRequests) {
      ArgumentChecker.notNullOrEmpty(quandlCodeRequests, "quandlCodeRequests");
      return new Builder(Arrays.asList(quandlCodeRequests));
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
    public MultiDataSetRequest build() {
      return new MultiDataSetRequest(this);
    }
  }
  /**
   * @return the list of quandl code requests (code + column)
   */
  public List<QuandlCodeRequest> getQuandlCodeRequests() {
  	return _quandlCodeRequests;
  }
  
  /**
   * @return the start date, or null if not set
   */
  public LocalDate getStartDate() {
  	return _startDate;
  }
  
  /**
   * @return the end date, or null if not set
   */
  public LocalDate getEndDate() {
  	return _endDate;
  }
  
  /**
   * @return the frequency, or null if not set
   */
  public Frequency getFrequency() {
  	return _frequency;
  }
  
  /**
   * @return the maximum number of rows to be returned, or null if not set
   */
  public Integer getMaxRows() {
  	return _maxRows;
  }
  
  /**
   * @return the transform requested, or null if not set
   */
  public Transform getTransform() {
  	return _transform;
  }
  
  /**
   * @return the sort order, or null if not set
   */
  public SortOrder getSortOrder() {
  	return _sortOrder;
  }

  private String buildCodeList(final List<QuandlCodeRequest> quandlCodeRequests) {
    StringBuilder sb = new StringBuilder();
    Iterator<QuandlCodeRequest> iter = quandlCodeRequests.iterator();
    while (iter.hasNext()) {
      QuandlCodeRequest req = iter.next();
      sb.append(req.getQuandlCode().replace('/', '.')); // allow user to use WIKI/AAPL as all other calls rather than WIKI.AAPL.
      if (req.isSingleColumnRequest()) { // append the column number.
        sb.append(".");
        sb.append(req.getColumnNumber());
      }
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  /**
   * Append any specified parameters to the provided WebTarget.
   * @param webTarget a web target used by the Jersey Client API, not null
   * @return the WebTarget with any path and query parameters appended, not null
   */
  public WebTarget appendPathAndQueryParameters(final WebTarget webTarget) {
    ArgumentChecker.notNull(webTarget, "webTarget");
    WebTarget resultTarget = webTarget;
    resultTarget = resultTarget.path(MULTI_SET_NAME + EXTENSION);
    resultTarget = resultTarget.queryParam(COLUMNS_PARAM, buildCodeList(_quandlCodeRequests));
    if (_startDate != null) {
      resultTarget = resultTarget.queryParam(START_DATE_PARAM, _startDate.toString());
    }
    if (_endDate != null) {
      resultTarget = resultTarget.queryParam(END_DATE_PARAM, _endDate.toString());
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
    if (_maxRows != null) {
      result = (prime * result) + _maxRows.hashCode();
    }
    if (_transform != null) {
      result = (prime * result) + _transform.hashCode();
    }
    if (_sortOrder != null) {
      result = (prime * result) + _sortOrder.hashCode();
    }
    if (_startDate != null) {
      result = (prime * result) + _startDate.hashCode();
    }
    if (_endDate != null) {
      result = (prime * result) + _endDate.hashCode();
    }
    // this can't be null, so don't need to test.
    result = (prime * result) + _quandlCodeRequests.hashCode();
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
    if (!(obj instanceof MultiDataSetRequest)) {
      return false;
    }
    MultiDataSetRequest other = (MultiDataSetRequest) obj;
    if (!_quandlCodeRequests.equals(other._quandlCodeRequests)) {
      return false;
    }
    if (_startDate == null) {
      if (other._startDate != null) {
        return false;
      }
    } else if (!_startDate.equals(other._startDate)) {
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

    if (_transform != other._transform) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MultiDataSetRequest[quandlCodeRequests=");
    builder.append(_quandlCodeRequests);
    builder.append(", startDate=");
    builder.append(_startDate);
    builder.append(", endDate=");
    builder.append(_endDate);
    builder.append(", maxRows=");
    builder.append(_maxRows);
    builder.append(", frequency=");
    builder.append(_frequency);
    builder.append(", transform=");
    builder.append(_transform);
    builder.append(", sortOrder=");
    builder.append(_sortOrder);
    builder.append("]");
    return builder.toString();
  }
}

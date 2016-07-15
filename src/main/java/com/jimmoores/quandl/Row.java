package com.jimmoores.quandl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.QuandlRuntimeException;


/**
 * Class to represent a single Row of data.
 */
public final class Row {
  private static Logger s_logger = LoggerFactory.getLogger(Row.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  
  private final HeaderDefinition _headerDefinition;
  private final String[] _values;

  private Row(final HeaderDefinition headerDefinition, final String[] values) {
    if (headerDefinition.size() != values.length) {
      s_logger.error("Attempt to create a Row with a header definition containing {} columns and a values array containing {} values", headerDefinition.size(), values.length);
      throw new QuandlRuntimeException("headerDefinition and values array are of differing length");
    }
    _headerDefinition = headerDefinition;
    _values = values;
  }
  
  /**
   * Create a Row.
   * Items in the values array can be null although the array cannot.
   * If the headerDefinition has a different number of columns than the length of the values array
   * then an IllegalArgumentException will be thrown.
   * @param headerDefinition the row definition, not null
   * @param values the actual values in the row, not null
   * @return a row instance
   */
  public static Row of(final HeaderDefinition headerDefinition, final String[] values) {
    ArgumentChecker.notNull(headerDefinition, "headerDefinition");
    ArgumentChecker.notNull(values, "values");
    return new Row(headerDefinition, values);
  }
  
  /**
   * Returns an entry from a given column index as a String (possibly null).
   * Throws an ArrayIndexOutOfBoundsException if the index is greater than the number 
   * of columns in the header definition
   * @param index the index of the entry, zero-based.
   * @return the entry, can be null
   */
  public String getString(final int index) {
    return _values[index];
  }
 
  /**
   * Returns an entry from a given column name as a String (possibly null).
   * Throws an IllegalArgumentException if the name is not defined in the 
   * header definition.  Empty is returned as the empty String rather than null.
   * @param column the column name of the entry, not null
   * @return the entry, can be null
   */  
  public String getString(final String column) {
    return _values[_headerDefinition.columnIndex(column)];
  }
  
  /**
   * Returns an entry from a given column index as a LocalDate (possibly null).
   * Throws an ArrayIndexOutOfBoundsException if the index is greater than the number of columns in the header definition.
   * Throws a DateTimeParseException if the underlying data is not a date in ISO local date format (YYYY-MM-DD).
   * @param index the index of the entry, zero-based.
   * @return the entry, can be null if the value is null or empty
   */
  public LocalDate getLocalDate(final int index) {
    if (_values[index] == null || _values[index].isEmpty()) {
      return null;
    } else {
      return LocalDate.parse(_values[index], DATE_FORMATTER);      
    }
  }
  
  /**
   * Returns an entry from a given column name as a LocalDate (possibly null).
   * Throws an IllegalArgumentException if the name is not defined in the header definition.
   * Throws a DateTimeParseException if the underlying data is not a date in ISO local date format (YYYY-MM-DD).
   * @param column the column name of the entry, not null
   * @return the entry, can be null if the value is null or empty
   */
  public LocalDate getLocalDate(final String column) {
    int index = _headerDefinition.columnIndex(column);
    return getLocalDate(index);
  }
  
  /**
   * Returns an entry from a given column index as a Double (possibly null).
   * Throws as ArrayIndexOutOfBoundsException if the index is greater than the number 
   * of columns in the header definition.
   * Throws a NumberFormatException if the underlying data can not be parsed as a double precision floating point number.
   * @param index the index of the entry, zero-based
   * @return the value or null if the value is null or empty
   */
  public Double getDouble(final int index) {
    if (_values[index] == null || _values[index].isEmpty()) {
      return null;
    } else {
      return Double.parseDouble(_values[index]);
    }
  }
  
  /**
   * Returns an entry from a given column name as a Double (possibly null).
   * Throws an IllegalArgumentException if the name is not defined in the header definition.
   * Throws a NumberFormatException if the underlying data cannot be parsed as a double precision floating point number.
   * @param column the column name of the entry, not null
   * @return the entry, can be null if the value is null or empty
   */
  public Double getDouble(final String column) {
    int index = _headerDefinition.columnIndex(column);
    return getDouble(index);
  }
  
  /**
   * Get the number of columns in this row.
   * @return the number of columns in this row
   */
  public int size() {
    return _values.length;
  }
  
  /**
   * Create row with new header.
   */
  public Row withPaddedHeader(final HeaderDefinition headerDefinition) {
    if (_headerDefinition != headerDefinition) {
      String[] values = new String[headerDefinition.size()];
      System.arraycopy(_values, 0, values, 0, _values.length);
      return Row.of(headerDefinition, values);
    } else {
      return this;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _headerDefinition.hashCode();
    result = prime * result + Arrays.hashCode(_values);
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
    if (!(obj instanceof Row)) {
      return false;
    }
    Row other = (Row) obj;
    if (!_headerDefinition.equals(other._headerDefinition)) {
      return false;
    }
    if (!Arrays.equals(_values, other._values)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Row[headerDefinition=" + _headerDefinition + ", values=" + Arrays.toString(_values) + "]";
  }
  
}

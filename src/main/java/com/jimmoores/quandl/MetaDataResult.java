package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.PrettyPrinter;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * Class to hold meta-data for a single Quandl code.
 */
public final class MetaDataResult {
  private static Logger s_logger = LoggerFactory.getLogger(MetaDataResult.class);
  private static final String COLUMN_NAMES_FIELD = "column_names";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  private static final String DATA_SOURCE_FIELD = "database_code";
  private static final String CODE_FIELD = "dataset_code";
  private JSONObject _jsonObject;

  private MetaDataResult(final JSONObject jsonObject) {
    _jsonObject = jsonObject;
  }
  
  /**
   * Factory method for creating and instance of a MetaDataResult.
   * @param jsonObject the JSON object returned by Quandl, not null
   * @return a MetaDataResult instance, not null
   */
  public static MetaDataResult of(final JSONObject jsonObject) {
    ArgumentChecker.notNull(jsonObject, "jsonObject");
    return new MetaDataResult(jsonObject);
  }
  
  /**
   * Extract a HeaderDefinition from the meta data.
   * Throws a QuandlRuntimeException if it cannot construct a valid HeaderDefinition
   * @return the header definition, not null
   */
  public HeaderDefinition getHeaderDefinition() {
    JSONArray jsonArray = null;
    try {
      jsonArray = _jsonObject.getJSONArray(COLUMN_NAMES_FIELD);
      List<String> columnNames = new ArrayList<String>(jsonArray.length()); 
      for (int i = 0; i < jsonArray.length(); i++) {
        columnNames.add(jsonArray.getString(i));
      }
      return HeaderDefinition.of(columnNames);
    } catch (JSONException ex) {
      s_logger.error("Metadata had unexpected structure - could not extract column_names field. Was:\n{}", _jsonObject.toString());
      throw new QuandlRuntimeException("Metadata had unexpected structure", ex);
    }
  }
  
  /**
   * Get the Quandl code associated with this metadata.
   * @return the quandl code (DATASOURCE/CODE form), null if not present.
   */
  public String getQuandlCode() {
    try {
      String dataSourceField = getString(DATA_SOURCE_FIELD);
      String codeField = getString(CODE_FIELD);
      // if either not present, will throw an exception.
      return dataSourceField + "/" + codeField;
    } catch (RuntimeException ex) {
      return null;
    }
  }
  /**
   * Get a String field.
   * Throws a QuandlRuntimeException if it cannot find the field
   * @param fieldName the name of the field
   * @return the field value, or null if the field is null
   */
  public String getString(final String fieldName) {
    try {
      return _jsonObject.getString(fieldName);
    } catch (JSONException ex) {
      throw new RuntimeException("Cannot find field", ex);
    }
  }
  
  /**
   * Get a LocalDate field (converted from a String internally).
   * Throws a QuandlRuntimeException if it cannot find the field
   * @param fieldName the name of the field
   * @return the field value, or null if the field is null
   */
  public LocalDate getLocalDate(final String fieldName) {
    try {
      if (_jsonObject.isNull(fieldName)) {
        return null;
      } else {
        return LocalDate.parse(_jsonObject.getString(fieldName), DATE_FORMATTER);
      }
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Cannot find field", ex);
    }
  }
  
  /**
   * Get a LocalDate field (converted from a String internally).
   * Throws a QuandlRuntimeException if it cannot find the field
   * @param fieldName the name of the field
   * @return the field value, or null if the field is null
   */
  public OffsetDateTime getOffsetDateTime(final String fieldName) {
    try {
      if (_jsonObject.isNull(fieldName)) {
        return null;
      } else {
        return OffsetDateTime.parse(_jsonObject.getString(fieldName), DATE_TIME_FORMATTER);
      }
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Cannot find field", ex);
    }
  }
  
  /**
   * Return whether a field is present.
   * @param fieldName the name of the field
   * @return true, if the field is present
   */
  public boolean has(final String fieldName) {
    return _jsonObject.has(fieldName);
  }
  
  /**
   * Get a Double field.  This attempts to work around the stupid NaN is null behavior by
   * explicitly testing for null.
   * Throws a QuandlRuntimeException if it cannot find the field
   * @param fieldName the name of the field
   * @return the field value, or null if the field is null
   */
  public Double getDouble(final String fieldName) {
    try {
      if (_jsonObject.isNull(fieldName)) {
        return null;
      } else {
        return _jsonObject.getDouble(fieldName);
      }
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Cannot find field", ex);
    }
  }
  
  /**
   * An iterator over the string field names.
   * @return the iterator
   */
  @SuppressWarnings("unchecked")
  public Iterator<String> iterator() {
    // interface predates generics so need a suppress warnings here, but should be safe.
    return (Iterator<String>) _jsonObject.keys();
  }
  
  /**
   * Get the underlying JSON object, useful if the data structure has changed since release.
   * @return the underlying JSON object
   */
  public JSONObject getRawJSON() {
    return _jsonObject;
  }
  
  /**
   * Use the underlying JSON toString() to show full data structure.
   * This means data can be seen even if it isn't in a flat structure.
   * To get a pretty printed version, use toPrettyPrintedString()
   * @return a string representation of the meta-data laid out as a JSON message (single line).
   */
  @Override
public String toString() {
    return _jsonObject.toString();
  }
  
  /**
   * Print a nicely formatted representation of this object.  Currently prints a nicely formatted JSON representation.
   * @return a string containing a multi-line description of this object
   */
  public String toPrettyPrintedString() {
    return PrettyPrinter.toPrettyPrintedString(this);
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MetaDataResult)) {
      return false;
    }
    final MetaDataResult other = (MetaDataResult) obj;
    // This is a work-around because JSONObject doesn't override equals().
    // it will be quite expensive, but should suffice.
    return getRawJSON().toString().equals(other.getRawJSON().toString());
  }
  
  @Override
  public int hashCode() {
    // This is a work-around because JSONObject doesn't override hashCode().
    // it will be quite expensive, but should suffice.
    return getRawJSON().toString().hashCode();
  }
}

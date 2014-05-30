package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.PrettyPrinter;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * Class to hold meta-data for a single Quandl code.
 */
public final class SearchResult {
  private static Logger s_logger = LoggerFactory.getLogger(SearchResult.class);
  private static final String DOCUMENTS_ARRAY_FIELD = "docs";
  private JSONObject _jsonObject;

  private SearchResult(final JSONObject jsonObject) {
    _jsonObject = jsonObject;
  }
  
  /**
   * Factory method for creating and instance of a MetaDataResult.
   * @param jsonObject the JSON object returned by Quandl, not null
   * @return a MetaDataResult instance, not null
   */
  public static SearchResult of(final JSONObject jsonObject) {
    ArgumentChecker.notNull(jsonObject, "jsonObject");
    return new SearchResult(jsonObject);
  }
  
  /**
   * Get the total number of documents in this result set.
   * Throws a QuandlRuntimeException if Quandl response doesn't contain this field, although it should.
   * @return the total number of documents in this result set
   */
  public int getTotalDocuments() {
    try {
      final int totalDocs = _jsonObject.getInt("total_count");
      return totalDocs;
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Could not find total_count field in results from Quandl", ex);
    }
  }
  
  /**
   * Get the number of documents per page in this result set.
   * Throws a QuandlRuntimeException if Quandl response doesn't contain this field, although it should.
   * @return the number of documents per page in this result set
   */
  public int getDocumentsPerPage() {
    try {
      final int totalDocs = _jsonObject.getInt("total_count");
      return totalDocs;
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Could not find total_count field in results from Quandl", ex);
    }
  }
  
  /**
   * Get the current page of this result set.  You can specify the page number in the request.
   * Throws a QuandlRuntimeException if Quandl response doesn't contain this field, although it should.
   * @return the page number of this result set
   */
  public int getCurrentPage() {
    try {
      final int totalDocs = _jsonObject.getInt("current_page");
      return totalDocs;
    } catch (JSONException ex) {
      throw new QuandlRuntimeException("Could not find total_count field in results from Quandl", ex);
    }
  }
  
  /**
   * Extract a list of MetaDataResult objects, each one representing a match.
   * Throws a QuandlRuntimeException if it cannot construct a valid HeaderDefinition
   * @return the header definition, not null
   */
  public List<MetaDataResult> getMetaDataResultList() {
    JSONArray jsonArray = null;
    try {
      jsonArray = _jsonObject.getJSONArray(DOCUMENTS_ARRAY_FIELD);
      List<MetaDataResult> metaDataResults = new ArrayList<MetaDataResult>(jsonArray.length()); 
      for (int i = 0; i < jsonArray.length(); i++) {
        metaDataResults.add(MetaDataResult.of(jsonArray.getJSONObject(i)));
      }
      return metaDataResults;
    } catch (JSONException ex) {
      s_logger.error("Metadata had unexpected structure - could not extract docs field, was:\n{}", _jsonObject.toString());
      throw new QuandlRuntimeException("Metadata had unexpected structure", ex);
    }
  }
  
  /**
   * An iterator over the string field names.
   * @return the iterator
   */
  public Iterator<MetaDataResult> iterator() {
    return getMetaDataResultList().iterator();
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
   * To get a pretty printed version, use getRawJSON().toString(indent)
   * @return a string representation of the meta-data laid out as a JSON message (single line).
   */
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
    if (!(obj instanceof SearchResult)) {
      return false;
    }
    final SearchResult other = (SearchResult) obj;
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

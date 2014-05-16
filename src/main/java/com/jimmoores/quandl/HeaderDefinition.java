package com.jimmoores.quandl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jimmoores.quandl.util.ArgumentChecker;

import jersey.repackaged.com.google.common.collect.Lists;

/**
 * The definition of the names of columns in a Row.
 */
public final class HeaderDefinition {
  private final Map<String, Integer> _columnNamesToIndices = new LinkedHashMap<String, Integer>();

  private HeaderDefinition(final List<String> columnNames) {
    int i = 0;
    for (String columnName : columnNames) {
      _columnNamesToIndices.put(columnName, i++);
    }
  }
  
  /**
   * Create a RowDefinition when no type information is available.
   * @param columnNames a list of strings, each naming a column
   * @return the instance
   */
  public static HeaderDefinition of(final List<String> columnNames) {
    ArgumentChecker.notNull(columnNames, "columnNames");
    return new HeaderDefinition(columnNames);
  }  
  
  /**
   * Create a RowDefinition when no type information is available.
   * @param columnNames a vararg array of strings, each naming a column
   * @return the instance
   */
  public static HeaderDefinition of(final String... columnNames) {
    ArgumentChecker.notNull(columnNames, "columnNames");
    return new HeaderDefinition(Arrays.asList(columnNames));
  }  
  
  /**
   * Get the column index of the named column (zero-based).  Throws IllegalArgumentException if column of provided name is not found.
   * @param columnName the name of the column
   * @return the column index
   * 
   */
  public int columnIndex(final String columnName) {
    Integer index = _columnNamesToIndices.get(columnName);
    if (index == null) {
      throw new IllegalArgumentException("Could not find index for column " + columnName);
    }
    return index;
  }
  
  /**
   * Get the number of columns in this header definition.
   * @return the number of columns
   */
  public int size() {
    return _columnNamesToIndices.size();
  }
  
  /**
   * Get an iterator to return the names of all the columns in order.
   * @return the iterator
   */
  public Iterator<String> iterator() {
    return getColumnNames().iterator();
  }
  
  /**
   * Get an immutable copy of the list of column names.
   * @return an immutable copy of the list of column names
   */
  public List<String> getColumnNames() {
    // TODO: make this less ugly
    return Collections.unmodifiableList(Lists.newArrayList(_columnNamesToIndices.keySet()));
  }

  @Override
  public int hashCode() {
    return _columnNamesToIndices.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof HeaderDefinition)) {
      return false;
    }
    HeaderDefinition other = (HeaderDefinition) obj;
    if (_columnNamesToIndices == null) {
      if (other._columnNamesToIndices != null) {
        return false;
      }
    } else if (!_columnNamesToIndices.equals(other._columnNamesToIndices)) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HeaderDefinition[");
    Iterator<String> iter = _columnNamesToIndices.keySet().iterator();
    while (iter.hasNext()) {
      sb.append(iter.next());
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    sb.append("]");
    return sb.toString();
  } 
}

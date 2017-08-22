package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * The definition of the names of columns in a Row.
 */
public final class HeaderDefinition {
  private static Logger s_logger = LoggerFactory.getLogger(HeaderDefinition.class);
  private final Map<String, Integer> _columnNamesToIndices = new LinkedHashMap<String, Integer>();
  private final List<String> _columnNames;

  private HeaderDefinition(final List<String> columnNames) {
    _columnNames = new ArrayList<String>(columnNames);
    int i = 0;
    Map<String, Integer> duplicateCounters = new HashMap<String, Integer>();
    for (String columnName : columnNames) {
      if (_columnNamesToIndices.containsKey(columnName)) {
        int count;
        if (duplicateCounters.containsKey(columnName)) {
          count = duplicateCounters.get(columnName);
          duplicateCounters.put(columnName, count + 1);
        } else {
          duplicateCounters.put(columnName, 1);
          count = 1;
        }
        String arrayColumnName = columnName + "." + count;
        s_logger.warn("Duplicate column name {} enountered, renaming {}", columnName, arrayColumnName);
        _columnNamesToIndices.put(arrayColumnName, i);
        _columnNames.set(i, arrayColumnName);
        i++;
      } else {
        _columnNamesToIndices.put(columnName, i++);
      }
    }

  }

  /**
   * Create a RowDefinition. In rare situations, duplicates can occur. These will be renamed so:
   * 
   * <pre>
   *   column, column, column, ...
   * </pre>
   * 
   * becomes:
   * 
   * <pre>
   *   column, column.1, column.2, ...
   * </pre>
   * 
   * for lookup and display purposes. A warning will be printed to the logger.
   * 
   * @param columnNames a list of strings, each naming a column
   * @return the instance
   */
  public static HeaderDefinition of(final List<String> columnNames) {
    ArgumentChecker.notNull(columnNames, "columnNames");
    return new HeaderDefinition(columnNames);
  }

  /**
   * Create a RowDefinition when no type information is available. In rare situations, duplicates can occur. These will be renamed so:
   * 
   * <pre>
   *   column, column, column, ...
   * </pre>
   * 
   * becomes:
   * 
   * <pre>
   *   column, column.1, column.2, ...
   * </pre>
   * 
   * for lookup purposes. A warning will be printed to the logger.
   * 
   * @param columnNames a vararg array of strings, each naming a column
   * @return the instance
   */
  public static HeaderDefinition of(final String... columnNames) {
    ArgumentChecker.notNull(columnNames, "columnNames");
    return new HeaderDefinition(Arrays.asList(columnNames));
  }

  /**
   * Get the column index of the named column (zero-based). Throws IllegalArgumentException if column of provided name is not found.
   * Repeated column names can be indexed as column, column.1, column.2, etc.
   * 
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
   * 
   * @return the number of columns
   */
  public int size() {
    return _columnNames.size();
  }

  /**
   * Get an iterator to return the names of all the columns in order.
   * 
   * @return the iterator
   */
  public Iterator<String> iterator() {
    return getColumnNames().iterator();
  }

  /**
   * Get an immutable copy of the list of column names.
   * 
   * @return an immutable copy of the list of column names
   */
  public List<String> getColumnNames() {
    return Collections.unmodifiableList(_columnNames);
  }

  @Override
  public int hashCode() {
    return _columnNames.hashCode();
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
    if (_columnNames == null) {
      if (other._columnNames != null) {
        return false;
      }
    } else if (!_columnNames.equals(other._columnNames)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HeaderDefinition[");
    Iterator<String> iter = _columnNames.iterator();
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

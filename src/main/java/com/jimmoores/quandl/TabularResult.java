package com.jimmoores.quandl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jimmoores.quandl.util.ArgumentChecker;

/**
 * Represents a result in tabular form.
 */
public final class TabularResult {
  /** definition of each column. */
  private HeaderDefinition _headerDefinition;
  /** list of rows. */
  private List<Row> _rows;

  /**
   * Private constructor, use static factory of().
   * @param headerDefinition a list of the column headers
   * @param rows a list of rows
   */
  private TabularResult(final HeaderDefinition headerDefinition, final List<Row> rows) {
    _headerDefinition = headerDefinition;
    _rows = Collections.unmodifiableList(rows);
  }

  /**
   * Create a tabular result set from a header definition and an ordered list of rows.
   * @param headerDefinition the definition of the header row, not null
   * @param rows a list of rows, not null
   * @return the TabularResult instance
   */
  public static TabularResult of(final HeaderDefinition headerDefinition, final List<Row> rows) {
    ArgumentChecker.notNull(headerDefinition, "headerDefinition");
    ArgumentChecker.notNull(rows, "rows");
    return new TabularResult(headerDefinition, rows);
  }

  /**
   * Get the header definition.
   * @return the header definition, not null
   */
  public HeaderDefinition getHeaderDefinition() {
    return _headerDefinition;
  }
  
  /**
   * Get a row, indexed from zero (excluding the header).
   * Throws an IndexOutOfBoundsException if index >= size()
   * @param index the index of the row
   * @return the row, not null
   */
  public Row get(final int index) {
    return _rows.get(index);
  }
  
  /**
   * Gets a row iterator.
   * @return an iterator over the rows
   */
  public Iterator<Row> iterator() {
    return _rows.iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _headerDefinition.hashCode();
    result = prime * result + _rows.hashCode();
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
    if (!(obj instanceof TabularResult)) {
      return false;
    }
    TabularResult other = (TabularResult) obj;
    if (!_headerDefinition.equals(other._headerDefinition)) {
      return false;
    }
    if (!_rows.equals(other._rows)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TabularResult[headerDefinition=");
    builder.append(_headerDefinition);
    builder.append(", rows=");
    builder.append(_rows);
    builder.append("]");
    return builder.toString();
  }
}

package com.jimmoores.quandl.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.TabularResult;

/**
 * Static utility class to PrettyPrint structures.
 */
public final class PrettyPrinter {
  private static Logger s_logger = LoggerFactory.getLogger(PrettyPrinter.class);
  private static final String NULL = "null";
  private static final int NULL_SIZE = NULL.length();
  private static final int JSON_INDENT = 2;
  private static final String LINE_SEPARATOR = "\n";

  private PrettyPrinter() {
  }

  /**
   * Pretty print a SearchResult as an indented piece of JSON code. Throws a QuandlRuntimeException if it can't render the nested JSONObject
   * to a String.
   * 
   * @param searchResult the searchResult, not null
   * @return a String representation of the object, probably multi-line.
   */
  public static String toPrettyPrintedString(final SearchResult searchResult) {
    return toPrettyPrintedString(searchResult.getRawJSON());
  }

  /**
   * Pretty print a MetaDataResult as an indented piece of JSON code. Throws a QuandlRuntimeException if it can't render the nested
   * JSONObject to a String.
   * 
   * @param metaDataResult the metaDataResult, not null
   * @return a String representation of the object, probably multi-line.
   */
  public static String toPrettyPrintedString(final MetaDataResult metaDataResult) {
    return toPrettyPrintedString(metaDataResult.getRawJSON());
  }

  /**
   * Pretty print a JSONObject as an indented piece of JSON code. Throws a QuandlRuntimeException if it can't render the JSONObject to a
   * String.
   * 
   * @param jsonObject the pre-parsed JSON object to pretty-print, not null
   * @return a String representation of the object, probably multi-line.
   */
  public static String toPrettyPrintedString(final JSONObject jsonObject) {
    ArgumentChecker.notNull(jsonObject, "jsonObject");
    try {
      return jsonObject.toString(JSON_INDENT) + LINE_SEPARATOR;
    } catch (JSONException ex) {
      s_logger.error("Problem converting JSONObject to String", ex);
      throw new QuandlRuntimeException("Problem converting JSONObject to String", ex);
    }
  }

  /**
   * Pretty print a map of String to HeaderDefinition (see QuandlSession.getMultipleHeaderDefinition) Throws a QuandlRuntimeException if it
   * can't render the JSONObject to a String.
   * 
   * @param multiHeaderDefinitionResult the pre-parsed JSON object to pretty-print, not null
   * @return a String representation of the object, probably multi-line.
   */
  public static String toPrettyPrintedString(final Map<String, HeaderDefinition> multiHeaderDefinitionResult) {
    ArgumentChecker.notNull(multiHeaderDefinitionResult, "multiHeaderDefinitionResult");
    StringBuilder sb = new StringBuilder();
    int max = 0;
    for (String each : multiHeaderDefinitionResult.keySet()) {
      max = Math.max(max, each.length());
    }
    for (Map.Entry<String, HeaderDefinition> entry : multiHeaderDefinitionResult.entrySet()) {
      String quandlCode = entry.getKey();
      HeaderDefinition headerDefinition = entry.getValue();
      sb.append(quandlCode);
      sb.append(repeat(max - quandlCode.length(), ' ')); // indent
      sb.append(" => ");
      Iterator<String> iterator = headerDefinition.iterator();
      while (iterator.hasNext()) {
        sb.append(iterator.next());
        if (iterator.hasNext()) {
          sb.append(", ");
        }
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Pretty print a TabularResult in a text-based table format.
   * 
   * @param result a TabularResult, not null
   * @return a String containing the table to be printed, not null.
   */
  public static String toPrettyPrintedString(final TabularResult result) {
    ArgumentChecker.notNull(result, "result");
    StringBuilder sb = new StringBuilder();
    int[] maxWidths = maximumWidths(result);
    separator(sb, maxWidths);
    header(sb, maxWidths, result.getHeaderDefinition());
    separator(sb, maxWidths);
    for (final Row row : result) {
      row(sb, maxWidths, row);
    }
    separator(sb, maxWidths);
    return sb.toString();
  }

  private static void separator(final StringBuilder sb, final int[] maxWidths) {
    for (int width : maxWidths) {
      sb.append("+");
      sb.append(repeat(width + 2, '-'));
    }
    sb.append("+");
    sb.append("\n");
  }

  private static void row(final StringBuilder sb, final int[] maxWidths, final Row row) {
    int i = 0;
    for (int width : maxWidths) {
      String value = row.getString(i++);
      sb.append("| ");
      if (value != null) {
        sb.append(value);
        sb.append(repeat(width - value.length(), ' '));
      } else {
        sb.append(NULL);
        sb.append(repeat(width - NULL_SIZE, ' '));
      }
      sb.append(" ");
    }
    sb.append("|");
    sb.append("\n");
  }

  private static void header(final StringBuilder sb, final int[] maxWidths, final HeaderDefinition headerDefinition) {
    Iterator<String> iterator = headerDefinition.iterator();
    for (int width : maxWidths) {
      String value = iterator.next();
      sb.append("| ");
      if (value != null) {
        sb.append(value);
        sb.append(repeat(width - value.length(), ' '));
      } else {
        sb.append(NULL);
        sb.append(repeat(width - NULL_SIZE, ' '));
      }
      sb.append(" ");
    }
    sb.append("|");
    sb.append("\n");
  }

  private static String repeat(final int n, final char v) {
    char[] repeated = new char[n];
    Arrays.fill(repeated, v);
    return new String(repeated); // this removes the need for commons lang StringUtils.repeat dependency
  }

  private static int[] maximumWidths(final TabularResult result) {
    HeaderDefinition headerDefinition = result.getHeaderDefinition();
    int[] maxWidths = new int[headerDefinition.size()];
    int count = 0;
    for (String columnName : headerDefinition.getColumnNames()) {
      maxWidths[count++] = columnName.length();
    }
    Iterator<Row> iterator = result.iterator();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      for (int i = 0; i < row.size(); i++) {
        String columnValue = row.getString(i);
        if (columnValue != null) {
          maxWidths[i] = Math.max(maxWidths[i], columnValue.length());
        } else {
          maxWidths[i] = Math.max(maxWidths[i], NULL_SIZE);
        }
      }
    }
    return maxWidths;
  }
}

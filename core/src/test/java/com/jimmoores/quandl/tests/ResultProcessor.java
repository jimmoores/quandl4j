package com.jimmoores.quandl.tests;

import java.util.Map;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.TabularResult;


/**
 * Interface for processing results.  In particular, the implementation either saves the result to a file or asserts 
 * that the provided data is the same as that in a previously saved file.  Used for regression testings.
 */
public interface ResultProcessor {
  /**
   * The file name stub for tabular results.
   */
  String TABULAR = "TabularResult";
  /**
   * The file name stub for metadata results.
   */
  String METADATA = "MetaData";
  /**
   * The file name extension for textual results (tabular).
   */
  String TXT = ".txt";
  /**
   * The file name extension for JSON results (metadata).
   */
  String JSON = ".json";
  /**
   * Process a TabularResult, either saving or checking against a saved file.
   * @param tabularResult the tabular result to save or check
   */
  void processResult(TabularResult tabularResult);
  /**
   * Process a MetaDataResult, either saving or checking against a saved file.
   * @param metaDataResult the metaDataResult object to save or check
   */
  void processResult(MetaDataResult metaDataResult);
  /**
   * Process a map of Quandl code to HeaderDefinition, either saving or checking against a saved file.
   * @param multiHeaderDefinitionResult the map of Quandl code to HeaderDefinition to save or check
   */
  void processResult(Map<String, HeaderDefinition> multiHeaderDefinitionResult);
  /**
   * Process a SearchResult, either saving or checking against a saved file.
   * @param searchResult the JSON object to save or check
   */
  void processResult(SearchResult searchResult);
}

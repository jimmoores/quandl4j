package com.jimmoores.quandl.classic.tests;

import java.util.Map;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.TabularResult;

/**
 * A result processor that does nothing.
 */
public class NoOpResultProcessor implements ResultProcessor {

  /**
   * Do nothing.
   * @param tabularResult the tabular result to do nothing with
   */
  public void processResult(final TabularResult tabularResult) {
  }

  /**
   * Do nothing.
   * @param metaDataResult the meta data result to do nothing with
   */
  public void processResult(final MetaDataResult metaDataResult) {
  }

  /**
   * Do nothing.
   * @param searchResult the search result to do nothing with
   */
  public void processResult(final SearchResult searchResult) {
  }

  /**
   * Do nothing.
   * @param multiHeaderDefinitionResult the multi-header definition result to do nothing with
   */
  public void processResult(final Map<String, HeaderDefinition> multiHeaderDefinitionResult) {
  }

}

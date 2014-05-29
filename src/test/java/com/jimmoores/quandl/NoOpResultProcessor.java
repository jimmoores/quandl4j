package com.jimmoores.quandl;

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

}

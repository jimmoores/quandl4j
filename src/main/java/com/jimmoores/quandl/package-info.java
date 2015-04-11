/**
 * <h1>The core Quandl wrapper classes</h1>
 * <p>The core class is {@link com.jimmoores.quandl.QuandlSession}.  This can be initialized with or without {@link com.jimmoores.quandl.SessionOptions} that specify things
 * like the Quandl API Token (optional, but strongly recommended - they are free).  The general pattern is that there is a Request
 * and Response class.  The Request class often uses the Builder pattern to allow requests to be built in a fluent style.</p>
 * <pre>
 *   QuandlSession session = QuandlSession.create();
 *   TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL"));
 *   System.out.println(tabularResult.toPrettyPrintedString());
 * </pre>
 * <p>The types of request are:</p>
 * <ul>
 *   <li>{@link com.jimmoores.quandl.DataSetRequest} - request tabular data set for a single Quandl code, builder has a range of optional parameters.</li>
 *   <li>{@link com.jimmoores.quandl.MultiDataSetRequest} - request tabular data across multiple Quandl codes, builder has a range of optional parameters.</li>
 *   <li>{@link com.jimmoores.quandl.MetaDataRequest} - request meta data for a single Quandl code.</li>
 *   <li>{@link com.jimmoores.quandl.MultiMetaDataRequest} - request meta data for multiple Quandl codes.</li>
 *   <li>{@link com.jimmoores.quandl.SearchRequest} - perform text-based searches, large result sets are paged.</li>
 * </ul>
 * <p>The core methods on the session are:</p>
 * <ul>
 *   <li>{@link com.jimmoores.quandl.QuandlSession#getDataSet(DataSetRequest) getDataSet(DataSetRequest)} - request tabular data for single Quandl code.</li>
 *   <li>{@link com.jimmoores.quandl.QuandlSession#getDataSets(MultiDataSetRequest) getDataSets(MultiDataSetRequest)} - request tabular data for multiple Quandl codes/columns.</li>
 *   <li>{@link com.jimmoores.quandl.QuandlSession#getMetaData(MetaDataRequest) getMetaData(MetaDataRequest)} - request meta data for a single Quandl code.</li>
 *   <li>{@link com.jimmoores.quandl.QuandlSession#getMetaData(MultiMetaDataRequest) getMetaData(MultiMetaDataRequest)} - request meta data for multiple Quandl codes.</li>
 *   <li>{@link com.jimmoores.quandl.QuandlSession#getMultipleHeaderDefinition(MultiMetaDataRequest) getMultipleHeaderDefinition(MultiMetaDataRequest)} - request 
 *   column header definitions for multiple Quandl codes.</li>
 *   <li>{@link com.jimmoores.quandl.QuandlSession#search(SearchRequest) search(SearchRequest)} - free text search on available data.</li>
 * </ul>
 * <p>The data types returned by these methods are:</p>
 * <ul>
 *   <li>{@link com.jimmoores.quandl.TabularResult} - made up of a {@link com.jimmoores.quandl.HeaderDefinition} and a list of {@link com.jimmoores.quandl.Row}s.</li>
 *   <li>{@link com.jimmoores.quandl.MetaDataResult} - Wrapper around JSON object returned.  Contains various typed accessor methods for convenience.</li>
 *   <li>{@link com.jimmoores.quandl.SearchResult} - Wrapper around JSON object, allows easy iteration through resulting matches.</li>
 * </ul>
 * <p>With the {@code MetaDataResult} and {@code SearchResult} the underlying raw JSON message is also available for query.
 * Note that the multiple data set/meta data set calls return data in the same format as single requests, just with more
 * columns and/or meta data.  The only current exception is {@code getMultipleHeaderDefinition} which separates out the
 * header information for each requested Quandl code.  Future versions are intended to provide methods to split out multiple
 * requests automatically for tabular data and search results (although for the meantime, the {@code SearchResult} class
 * contains functionality to separate out the search documents.</p>
 * <p>Unlike the underlying Quandl REST API, you can lookup columns in a {@code Row} object using the column name as well
 * as the index of the column.  In some corner cases, duplicate column names can be returned from Quandl.  In these cases,
 * the later duplicates are renamed to 'ColumnName.1', 'ColumnName.2', and so on.  This allows the consistent use of column
 * names for lookup.</p>
 * <h2>Gotchas and points of note</h2>
 * <ul>
 *   <li>It's common with the builder pattern to forget to call {@code build()} and be stumped as to why your IDE won't let
 *   you pass your request.</li>
 *   <li>Using column indices of less than 1 leads to unpredictable behavior.</li>
 *   <li>Search result page size is currently limited to 100 by Quandl.</li>
 *   <li>Search results with a high current page can be slow.</li>
 *   <li>Some requests throw unusual exceptions (e.g. {@code QuandlUnprocessableEntityException},
 *   {@code QuandlTooManyRequestsException}.  It's suggested all session calls should handle {@code QuandlRuntimeException} and it's
 *   sub-classes.</li>
 *   <li>Most of the data objects have {@code toPrettyPrintedString()} which produce nice output.  These call into the
 *   {@code PrettyPrinter} class, which can also handle {@code Map<String, HeaderDefinition>}.</li>
 *   <li>Comparing meta data objects may be slower than you might expect because the underlying JSON library doesn't implement
 *   {@code equals()} or {@code hashCode()}.  The workaround is to call {@code equals()} and {@code hashCode()} on the
 *   {@code toString()} output.</li>
 *   <li>The {@code getMetaData(MultiMetaDataRequest)} method is not especially useful as the only metadata returned for each
 *   request are the column names.  This is why it's almost always more convenient to use
 *   {@link com.jimmoores.quandl.QuandlSession#getMultipleHeaderDefinition(MultiMetaDataRequest) getMultipleHeaderDefinition(MultiMetaDataRequest)}.</li>
 * </ul>
 */
package com.jimmoores.quandl;


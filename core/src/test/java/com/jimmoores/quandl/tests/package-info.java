/**
 * <h1>Quandl wrapper test classes.</h1>
 * <p>These include both unit and regression tests.  Low level unit tests are present for:</p>
 * <ul>
 *   <li>Correct {@code equals()} and {@code hashCode()}</li>
 *   <li>Null handling - testing that nulls passed inappropriately cause exceptions</li>
 *   <li>URL Generation - testing that the library produces the right URLs for the right options</li>
 *   <li>Regression tests - RegressionTests is combined command line tool for saving test data sets
 *   and also a TestNG test that uses the current data set</li>
 * </ul>
 * <p>The URL generation and regression tests work by injecting special {@code RESTDataProvider}
 * implementations that either save the responses from Quandl in the file system (index file + data file)
 * and implementations that read data from previously saved index/data files to simulate a REST 
 * connection.</p>
 * <p>These RESTDataProviders can be reused by your own application specific tests.  They are passed into
 * the {@code QuandlSession} via the {@code create(SessionOptions)} method.</p>
 */
package com.jimmoores.quandl.tests;


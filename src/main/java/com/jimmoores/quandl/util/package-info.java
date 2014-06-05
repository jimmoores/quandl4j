/**
 * <h1>Quandl wrapper utility classes.</h1>
 * <p>These include:</p>
 * <ul>
 *   <li>{link ArgumentChecker} - for validating method arguments</li>
 *   <li>{link PrettyPrinter} - for nicely tabulated ASCII output</li>
 *   <li>{link RESTDataProvider} - interface for fetching documents.  Default implementation does Jersey HTTP Client REST call.
 *   Other implementations are used for testing (saving responses in files and replaying responses.</li>
 *   <li>{code Quandl*Exception} - Exceptions, extending from {link QuandlRuntimeException} for generic errors.</li>
 * </ul>
 */
package com.jimmoores.quandl.util;


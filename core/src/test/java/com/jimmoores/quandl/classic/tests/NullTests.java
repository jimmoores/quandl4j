package com.jimmoores.quandl.classic.tests;

import java.util.List;

import org.testng.annotations.Test;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * Unit tests testing null checking.
 */
@Test(groups = { "unit" })
public class NullTests {
  // CHECKSTYLE:OFF
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testDataSetRequest() {
    DataSetRequest.Builder.of(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testDataSetRequestStartDate() {
    DataSetRequest.Builder.of("Hello").withStartDate(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testDataSetRequestEndDate() {
    DataSetRequest.Builder.of("Hello").withEndDate(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testDataSetRequestFrequency() {
    DataSetRequest.Builder.of("Hello").withFrequency(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testDataSetRequestSortOrder() {
    DataSetRequest.Builder.of("Hello").withSortOrder(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testDataSetRequestTransform() {
    DataSetRequest.Builder.of("Hello").withTransform(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testHeaderDefinition() {
    HeaderDefinition.of((List<String>) null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testHeaderDefinitionVarArgs() {
    HeaderDefinition.of((String[]) null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMetaDataRequest() {
    MetaDataRequest.of(null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMetaDataResult() {
    MetaDataResult.of(null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testRowBothNull() {
    Row.of(null, null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testRowValuesNull() {
    Row.of(HeaderDefinition.of("one", "two"), null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testRowHeaderNull() {
    Row.of(null, new String[] { "one", "two"});
  }

  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testSearchRequestOfDatabaseCode() {
    SearchRequest.Builder.ofDatabaseCode(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testSearchRequestOfQuery() {
    SearchRequest.Builder.ofQuery(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testSearchResult() {
    SearchResult.of(null);
  }
  // CHECKSTYLE:ON
}

package com.jimmoores.quandl;

import org.testng.annotations.Test;

import com.jimmoores.quandl.util.QuandlRuntimeException;

import java.util.List;

/**
 * Unit tests testing null checking.
 */
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
  public final void testMultiDataSetRequest() {
    MultiDataSetRequest.Builder.of((List<QuandlCodeRequest>) null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiDataSetRequestVarArgs() {
    MultiDataSetRequest.Builder.of((QuandlCodeRequest[]) null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiDataSetRequestStartDate() {
    MultiDataSetRequest.Builder.of(QuandlCodeRequest.allColumns("Hello")).withStartDate(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiDataSetRequestEndDate() {
    MultiDataSetRequest.Builder.of(QuandlCodeRequest.allColumns("Hello")).withEndDate(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiDataSetRequestFrequency() {
    MultiDataSetRequest.Builder.of(QuandlCodeRequest.allColumns("Hello")).withFrequency(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiDataSetRequestSortOrder() {
    MultiDataSetRequest.Builder.of(QuandlCodeRequest.allColumns("Hello")).withSortOrder(null).build();
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiDataSetRequestTransform() {
    MultiDataSetRequest.Builder.of(QuandlCodeRequest.allColumns("Hello")).withTransform(null).build();
  }

  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiMetaDataRequest() {
    MultiMetaDataRequest.of((List<String>) null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testMultiMetaDataRequestVarArgs() {
    MultiMetaDataRequest.of((String[]) null);
  }

  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testQuandlCodeRequestAll() {
    QuandlCodeRequest.allColumns(null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testQuandlCodeRequestSingle() {
    QuandlCodeRequest.singleColumn(null, 1);
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
  public final void testSearchRequest() {
    SearchRequest.of(null);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public final void testSearchResult() {
    SearchResult.of(null);
  }
  // CHECKSTYLE:ON
}

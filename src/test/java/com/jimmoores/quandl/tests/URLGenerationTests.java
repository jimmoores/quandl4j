package com.jimmoores.quandl.tests;

import java.net.URI;
import java.util.Collections;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.QuandlCodeRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.SessionOptions;
import com.jimmoores.quandl.SortOrder;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.Transform;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.RESTDataProvider;
import com.sun.jersey.api.client.WebResource;

/**
 * Tests to test the generation of URLs.
 */
@Test(groups = { "unit" })
public class URLGenerationTests {
  
  private static final int CLOSE_COLUMN = 4;
  private static final int PRICE_COLUMN = 1;
  private static final HeaderDefinition TEST_HEADER_DEFINITION = HeaderDefinition.of("Date", "Close");
  private static final TabularResult TEST_TABULAR_RESULT = TabularResult.of(TEST_HEADER_DEFINITION, Collections.singletonList(Row.of(TEST_HEADER_DEFINITION, new String[] { "Value1", "Value2"})));
  
  // CHECKSTYLE:OFF
  
  private class TestRESTDataProvider implements RESTDataProvider {

    private String _expectedURL;

    public TestRESTDataProvider(final String expectedURL) {
      _expectedURL = expectedURL;
    }
    
    public JSONObject getJSONResponse(final WebResource target) {
      URI uri = target.getUriBuilder().build();
      Assert.assertEquals(_expectedURL, uri.toString());
      return new JSONObject();
    }

    public TabularResult getTabularResponse(final WebResource target) {
      URI uri = target.getUriBuilder().build();
      Assert.assertEquals(_expectedURL, uri.toString());
      return TEST_TABULAR_RESULT;
    }
  };
  
  private QuandlSession getTestSession(final String expectedURL) {
    return QuandlSession.create(
             SessionOptions.Builder.withoutAuthToken()
                                   .withRESTDataProvider(
                                       new TestRESTDataProvider(expectedURL))
                                   .build());
  }

  @Test
  public void testSimpleGetDataSet() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets/WIKI/MSFT.csv");
    TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/MSFT").build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testMoreComplexGetDataSet() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets/WIKI/AAPL.csv?column=4&collapse=quarterly&transformation=normalize");
    TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL")
                                                                           .withFrequency(Frequency.QUARTERLY)
                                                                           .withColumn(CLOSE_COLUMN)
                                                                           .withTransform(Transform.NORMALIZE)
                                                                           .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testMostComplexGetDataSet() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets/WIKI/AAPL.csv?trim_start=2009-01-01&trim_end=2010-12-31&column=4&collapse=quarterly&rows=10&transformation=normalize");
    TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL")
                                                                           .withStartDate(LocalDate.of(2009, 1, 1))
                                                                           .withEndDate(LocalDate.of(2010, 12, 31))
                                                                           .withMaxRows(10)
                                                                           .withFrequency(Frequency.QUARTERLY)
                                                                           .withColumn(CLOSE_COLUMN)
                                                                           .withTransform(Transform.NORMALIZE)
                                                                           .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testMostComplexGetDataSetDifferentOrder() {
    // now try a different order
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets/WIKI/AAPL.csv?trim_start=2009-01-01&trim_end=2010-12-31&column=4&collapse=quarterly&rows=10&transformation=normalize");
    TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL")
                                                                            .withFrequency(Frequency.QUARTERLY)
                                                                            .withColumn(CLOSE_COLUMN)
                                                                            .withStartDate(LocalDate.of(2009, 1, 1))
                                                                            .withEndDate(LocalDate.of(2010, 12, 31))
                                                                            .withMaxRows(10)
                                                                            .withTransform(Transform.NORMALIZE)
                                                                            .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  // test getDataSets URL gen
  @Test
  public void testLargeMultiDataSetRequest() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/multisets.csv?columns=OFDP.FUTURE_CL1,WIKI.LIFE.4,BUNDESBANK.BBK01_WT5511.1");
    TabularResult tabularResult = session.getDataSets(MultiDataSetRequest.Builder.of(QuandlCodeRequest.allColumns("OFDP/FUTURE_CL1"), QuandlCodeRequest.singleColumn("WIKI/LIFE", CLOSE_COLUMN), QuandlCodeRequest.singleColumn("BUNDESBANK/BBK01_WT5511", PRICE_COLUMN)).build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testLargeMultiDataSetRequestWithExtraFiltersAndTransforms() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/multisets.csv?columns=OFDP.FUTURE_CL1,WIKI.LIFE.4,BUNDESBANK.BBK01_WT5511.1&collapse=monthly&transformation=normalize");
    TabularResult tabularResult = session.getDataSets(
        MultiDataSetRequest.Builder.of(
          QuandlCodeRequest.allColumns("OFDP/FUTURE_CL1"), 
          QuandlCodeRequest.singleColumn("WIKI/LIFE", CLOSE_COLUMN), 
          QuandlCodeRequest.singleColumn("BUNDESBANK/BBK01_WT5511", PRICE_COLUMN)
        )
        .withFrequency(Frequency.MONTHLY)
        .withTransform(Transform.NORMALIZE)
        .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public void testLargeMultiDataSetRequestWithEmpty() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/multisets.csv?columns=&collapse=monthly&transformation=normalize");
    TabularResult tabularResult = session.getDataSets( // expect an exception here because list is empty
        MultiDataSetRequest.Builder.of()
        .withFrequency(Frequency.MONTHLY)
        .withTransform(Transform.NORMALIZE)
        .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testMultiItemQueryWithAllOptions() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/multisets.csv?columns=OFDP.FUTURE_CL1,WIKI.LIFE.4,BUNDESBANK.BBK01_WT5511.1&trim_start=2009-01-01&trim_end=2011-01-01&collapse=monthly&rows=5&transformation=normalize&sort_order=desc");
    TabularResult tabularResult = session.getDataSets(
        MultiDataSetRequest.Builder.of(
          QuandlCodeRequest.allColumns("OFDP/FUTURE_CL1"), 
          QuandlCodeRequest.singleColumn("WIKI/LIFE", CLOSE_COLUMN), 
          QuandlCodeRequest.singleColumn("BUNDESBANK/BBK01_WT5511", PRICE_COLUMN)
        )
        .withFrequency(Frequency.MONTHLY)
        .withStartDate(LocalDate.of(2009, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .withSortOrder(SortOrder.DESCENDING)
        .withMaxRows(5)
        .withTransform(Transform.NORMALIZE)
        .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }

  // test getMetaData
  @Test
  public void testSimpleGetMetaData() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets/WIKI/MSFT.json?exclude_data=true");
    MetaDataResult result = session.getMetaData(MetaDataRequest.of("WIKI/MSFT"));
    Assert.assertEquals(result, MetaDataResult.of(new JSONObject()));
  }
  
  // test getMetaData(multiple data sets)
  @Test
  public void testSimpleGetMultiMetaData() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/multisets.json?columns=WIKI.MSFT,OFDP.FUTURE_CL1,WIKI.LIFE&trim_start=2100-01-01");
    MetaDataResult result = session.getMetaData(MultiMetaDataRequest.of("WIKI/MSFT", "OFDP/FUTURE_CL1", "WIKI/LIFE"));
    Assert.assertEquals(result, MetaDataResult.of(new JSONObject()));
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public void testEmptyGetMultiMetaData() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/multisets.json?columns=WIKI.MSFT,OFDP.FUTURE_CL1,WIKI.LIFE&trim_start=2100-01-01");
    MetaDataResult result = session.getMetaData(MultiMetaDataRequest.of()); // expect this to throw an exception
    Assert.assertEquals(result, MetaDataResult.of(new JSONObject()));
  }

  // test search
  @Test
  public void testSimpleSearch() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets.json?query=Crude+Oil");
    SearchResult result = session.search(SearchRequest.Builder.of("Crude Oil").build());
    Assert.assertEquals(result, SearchResult.of(new JSONObject()));
  }
  
  @Test
  public void testPagedSearch() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets.json?query=Crude+Oil&page=2");
    SearchResult result = session.search(SearchRequest.Builder.of("Crude Oil").withPageNumber(2).build());
    Assert.assertEquals(result, SearchResult.of(new JSONObject()));
  }
  
  @Test
  public void testPagedSearchMax() {
    QuandlSession session = getTestSession("http://quandl.com/api/v1/datasets.json?query=Crude+Oil&page=2&per_page=100");
    SearchResult result = session.search(SearchRequest.Builder.of("Crude Oil").withPageNumber(2).withMaxPerPage(100).build());
    Assert.assertEquals(result, SearchResult.of(new JSONObject()));
  }
  // CHECKSTYLE:ON
}

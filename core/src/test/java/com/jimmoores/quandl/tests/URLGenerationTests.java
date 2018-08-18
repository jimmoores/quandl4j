package com.jimmoores.quandl.tests;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.LocalDate;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.Frequency;
import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.MetaDataRequest;
import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.MultiDataSetRequest;
import com.jimmoores.quandl.MultiMetaDataRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.SearchRequest;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.SessionOptions;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.Transform;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.RESTDataProvider;

/**
 * @deprecated this test covers the deprecated API.
 * Tests to test the generation of URLs.
 */
@Test(groups = { "unit" })
public class URLGenerationTests {
  
  private static final int CLOSE_COLUMN = 4;
  @SuppressWarnings("unused")
  private static final int PRICE_COLUMN = 1;
  private static final HeaderDefinition TEST_HEADER_DEFINITION = HeaderDefinition.of("Date", "Close");
  private static final TabularResult TEST_TABULAR_RESULT = 
      TabularResult.of(TEST_HEADER_DEFINITION, Collections.singletonList(Row.of(TEST_HEADER_DEFINITION, new String[] { "Value1", "Value2"})));
  
  // CHECKSTYLE:OFF
  
  private class TestRESTDataProvider implements RESTDataProvider {

    private String _expectedURL;

    public TestRESTDataProvider(final String expectedURL) {
      _expectedURL = expectedURL;
    }
    
    public JSONObject getJSONResponse(final WebTarget target) {
      URI uri = target.getUriBuilder().build();
      Assert.assertEquals(_expectedURL, uri.toString());
      return new JSONObject();
    }

    public TabularResult getTabularResponse(final WebTarget target) {
      URI uri = target.getUriBuilder().build();
      Assert.assertEquals(_expectedURL, uri.toString());
      return TEST_TABULAR_RESULT;
    }
  }
  
  private QuandlSession getTestSession(final String expectedURL) {
    return QuandlSession.create(
             SessionOptions.Builder.withoutAuthToken()
                                   .withRESTDataProvider(
                                       new TestRESTDataProvider(expectedURL))
                                   .build());
  }

  @Test
  public void testSimpleGetDataSet() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets/WIKI/MSFT.csv");
    TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/MSFT").build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testMoreComplexGetDataSet() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?column_index=4&collapse=quarterly&transform=normalize");
    TabularResult tabularResult = session.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL")
                                                                           .withFrequency(Frequency.QUARTERLY)
                                                                           .withColumn(CLOSE_COLUMN)
                                                                           .withTransform(Transform.NORMALIZE)
                                                                           .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  @Test
  public void testMostComplexGetDataSet() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?start_date=2009-01-01&end_date=2010-12-31&column_index=4&collapse=quarterly&limit=10&transform=normalize");
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
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?start_date=2009-01-01&end_date=2010-12-31&column_index=4&collapse=quarterly&limit=10&transform=normalize");
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
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public void testLargeMultiDataSetRequestWithEmpty() {
    QuandlSession session = 
        getTestSession("https://www.quandl.com/api/v3/multisets.csv?columns=&collapse=monthly&transformation=normalize");
    TabularResult tabularResult = session.getDataSets( // expect an exception here because list is empty
        MultiDataSetRequest.Builder.of()
        .withFrequency(Frequency.MONTHLY)
        .withTransform(Transform.NORMALIZE)
        .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult);
  }
  
  // test getMetaData
  @Test
  public void testSimpleGetMetaData() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets/WIKI/MSFT.json?start_date=2100-01-01");
    MetaDataResult result = session.getMetaData(MetaDataRequest.of("WIKI/MSFT"));
    Assert.assertEquals(result, MetaDataResult.of(new JSONObject()));
  }
  
  @Test(expectedExceptions = QuandlRuntimeException.class)
  public void testEmptyGetMultiMetaData() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/multisets.json?columns=WIKI.MSFT,OFDP.FUTURE_CL1,WIKI.LIFE&trim_start=2100-01-01");
    MetaDataResult result = session.getMetaData(MultiMetaDataRequest.of()); // expect this to throw an exception
    Assert.assertEquals(result, MetaDataResult.of(new JSONObject()));
  }

  // test search
  @Test
  public void testSimpleSearch() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets.json?query=Crude+Oil");
    SearchResult result = session.search(SearchRequest.Builder.of("Crude Oil").build());
    Assert.assertEquals(result, SearchResult.of(new JSONObject()));
  }
  
  @Test
  public void testPagedSearch() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets.json?query=Crude+Oil&page=2");
    SearchResult result = session.search(SearchRequest.Builder.of("Crude Oil").withPageNumber(2).build());
    Assert.assertEquals(result, SearchResult.of(new JSONObject()));
  }
  
  @Test
  public void testPagedSearchMax() {
    QuandlSession session = getTestSession("https://www.quandl.com/api/v3/datasets.json?query=Crude+Oil&page=2&per_page=100");
    SearchResult result = session.search(SearchRequest.Builder.of("Crude Oil").withPageNumber(2).withMaxPerPage(100).build());
    Assert.assertEquals(result, SearchResult.of(new JSONObject()));
  }
  // CHECKSTYLE:ON
}

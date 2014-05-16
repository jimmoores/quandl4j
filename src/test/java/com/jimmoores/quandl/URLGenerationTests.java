package com.jimmoores.quandl;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.util.RESTDataProvider;

/**
 * Tests to test the generation of URLs.
 */
public class URLGenerationTests {
  
  private static final int CLOSE_COLUMN = 4;
  private static final HeaderDefinition TEST_HEADER_DEFINITION = HeaderDefinition.of("Date", "Close");
  private static final TabularResult TEST_TABULAR_RESULT = TabularResult.of(TEST_HEADER_DEFINITION, Collections.singletonList(Row.of(TEST_HEADER_DEFINITION, new String[] { "Value1", "Value2"})));
  
  // CHECKSTYLE:OFF
  
  private class TestRESTDataProvider implements RESTDataProvider {

    private String _expectedURL;

    public TestRESTDataProvider(final String expectedURL) {
      _expectedURL = expectedURL;
    }
    
    @Override
    public JSONObject getJSONResponse(final WebTarget target) {
      URI uri = target.getUriBuilder().build();
      Assert.assertEquals(_expectedURL, uri.toString());
      return new JSONObject();
    }

    @Override
    public TabularResult getTabularResponse(final WebTarget target) {
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
    // now try a different order
    TabularResult tabularResult2 = session.getDataSet(DataSetRequest.Builder.of("WIKI/AAPL")
                                                                            .withFrequency(Frequency.QUARTERLY)
                                                                            .withColumn(CLOSE_COLUMN)
                                                                            .withStartDate(LocalDate.of(2009, 1, 1))
                                                                            .withEndDate(LocalDate.of(2010, 12, 31))
                                                                            .withMaxRows(10)
                                                                            .withTransform(Transform.NORMALIZE)
                                                                            .build());
    Assert.assertEquals(TEST_TABULAR_RESULT, tabularResult2);
  }
  // CHECKSTYLE:ON
}

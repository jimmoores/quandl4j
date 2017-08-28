package com.jimmoores.quandl.processing.classic;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.processing.AbstractRESTDataProvider;
import com.jimmoores.quandl.processing.Request;

/**
 * Replacement for DefaultRESTDataProvider that specialises the AbstractRESTDataProvider for
 * org.json.JSONObject and TabularResult as the metadata and table types the user receives.
 * This is used by the new ClassicQuandlSession, providing the existing, familiar result types.
 */
public class JSONTabularResultRESTDataProvider extends AbstractRESTDataProvider<JSONObject, TabularResult> implements ClassicRESTDataProvider {
  private static final JSONObjectResponseProcessor JSON_OBJECT_RESPONSE_PROCESSOR = new JSONObjectResponseProcessor();
  private static final TabularResultResponseProcessor TABULAR_RESULT_RESPONSE_PROCESSOR = new TabularResultResponseProcessor();

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject getJSONResponse(final WebTarget target, final Request request) {
    return getResponse(target, JSON_OBJECT_RESPONSE_PROCESSOR, request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TabularResult getTabularResponse(final WebTarget target, final Request request) {
    return getResponse(target, TABULAR_RESULT_RESPONSE_PROCESSOR, request);
  }

}

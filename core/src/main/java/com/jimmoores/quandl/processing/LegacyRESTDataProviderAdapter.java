package com.jimmoores.quandl.processing;

import javax.ws.rs.client.WebTarget;

import org.json.JSONObject;

import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.util.RESTDataProvider;


/**
 * @deprecated this is a support class for implementers of the deprecated RESTDataProvider interface.
 * Adapter to convert legacy implementer of RESTDataProvider into the replacement GenericRESTDataProvider
 * which requires an extra parameter on the methods.
 */
public final class LegacyRESTDataProviderAdapter implements GenericRESTDataProvider<JSONObject, TabularResult> {
  private RESTDataProvider _legacyRestDataProvider;

  private LegacyRESTDataProviderAdapter(final RESTDataProvider legacyRestDataProvider) {
    _legacyRestDataProvider = legacyRestDataProvider;
  }
  
  /**
   * Create an instance of the adapter.
   * @param legacyRestDataProvider  the legacy RESTDataProvider
   * @return a GenericRESTDataProvider compatible with non-legacy code
   */
  public static GenericRESTDataProvider<JSONObject, TabularResult> of(final RESTDataProvider legacyRestDataProvider) {
    return new LegacyRESTDataProviderAdapter(legacyRestDataProvider);
  }

  /**
   * Specialised to JSONObject.
   * {@inheritDoc}
   */
  public JSONObject getJSONResponse(final WebTarget target, final Request request) {
    return _legacyRestDataProvider.getJSONResponse(target);
  }

  /**
   * Specialised to TabularResult.
   * {@inheritDoc}
   */
  public TabularResult getTabularResponse(final WebTarget target, final Request request) {
    return _legacyRestDataProvider.getTabularResponse(target);
  }
}

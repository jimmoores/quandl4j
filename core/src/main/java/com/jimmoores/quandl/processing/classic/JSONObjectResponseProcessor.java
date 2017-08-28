package com.jimmoores.quandl.processing.classic;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.processing.ResponseProcessor;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * Response processor to process an InputStream resulting from a request into
 * a JSONObject (org.json).
 */
public class JSONObjectResponseProcessor implements ResponseProcessor<JSONObject> {
  
  /**
   * {@inheritDoc}
   */
  public JSONObject process(final InputStream inputStream, final Request request) {
    JSONTokener tokeniser = new JSONTokener(new InputStreamReader(inputStream));
    try {
      return new JSONObject(tokeniser);
    } catch (JSONException e) {
      throw new QuandlRuntimeException("Error parsing JSON", e);
    }
  }
}

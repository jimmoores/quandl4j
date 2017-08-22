package com.jimmoores.quandl.processing.classic;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jimmoores.quandl.processing.ResponseProcessor;
import com.jimmoores.quandl.util.QuandlRuntimeException;

public class JSONObjectResponseProcessor implements ResponseProcessor<JSONObject> {
  public JSONObject process(InputStream inputStream) {
    JSONTokener tokeniser = new JSONTokener(new InputStreamReader(inputStream));
    try {
      return new JSONObject(tokeniser);
    } catch (JSONException e) {
      throw new QuandlRuntimeException("Error parsing JSON", e);
    }
  }
}

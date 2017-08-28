package com.jimmoores.quandl.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.RESTDataProvider;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @deprecated this version doesn't support newer interface GenericRestDataProvider
 *             a direct replacement isn't available and it's still usable via the 
 *             LegacyRESTDataProviderAdapter.O
 * RESTDataProvider that creates local file system copies of the files it gets
 * and keeps an index.  Files are created in the current working directory.
 */
public final class FileRESTDataProvider implements RESTDataProvider {
  private static Logger s_logger = LoggerFactory.getLogger(FileRESTDataProvider.class);
  
  private Map<String, String> _urlFileNameMap = new HashMap<String, String>();
  private Map<String, Exception> _urlExceptionMap = new HashMap<String, Exception>();

  private File _rootPath;

  private String _apiKeyString;
    
  /**
   * Construct an instance.
   * @param apiKey the Quandl API key (aka authorization token)
   */
  public FileRESTDataProvider(final String apiKey) {
    if (apiKey != null) {
      _apiKeyString = GenericQuandlSessionInterface.AUTH_TOKEN_PARAM_NAME + "=" + apiKey + "&";
    }
    File file;
    try {
      file = new File(RecordingRESTDataProvider.class.getResource("testdata/").toURI());
      _rootPath = file;
      readIndexFile(file);
    } catch (URISyntaxException ex) {
      throw new QuandlRuntimeException("Problem parsing path of testdata directory", ex);
    }
    
  }
  /**
   * Construct an instance.
   * @param rootPath the directory to store URI, Filename and exception data in
   * @param apiKey the Quandl API key (aka authorization token)
   */
  public FileRESTDataProvider(final File rootPath, final String apiKey) {
    if (apiKey != null) {
      _apiKeyString = GenericQuandlSessionInterface.AUTH_TOKEN_PARAM_NAME + "=" + apiKey;
    }
    readIndexFile(rootPath);
  }

  private void readIndexFile(final File rootPath) {
    try {
      CSVReader reader = new CSVReader(new FileReader(new File(rootPath, RecordingRESTDataProvider.INDEX_FILE_NAME)));
      String[] header = reader.readNext(); // discard header row
      s_logger.warn(Arrays.deepToString(header));
      String[] line;
      while ((line = reader.readNext()) != null) {
        String uri = line[RESTReponseIndexColumns.URI.getColumnIndex()];
        String file = line[RESTReponseIndexColumns.FILE.getColumnIndex()];
        String exceptionClass = line[RESTReponseIndexColumns.EXCEPTION_CLASS.getColumnIndex()];
        if (exceptionClass == null || exceptionClass.isEmpty()) {
          _urlFileNameMap.put(uri, file);
        } else {
          String exceptionMessage = line[RESTReponseIndexColumns.EXCEPTION_MESSAGE.getColumnIndex()];
          try {
            if (exceptionMessage == null || exceptionMessage.isEmpty()) {
              _urlExceptionMap.put(uri, (Exception) Class.forName(exceptionClass).newInstance());
            } else {
              Constructor<?> constructor = Class.forName(exceptionClass).getConstructor(String.class);
              if (constructor != null) {
                _urlExceptionMap.put(uri, (Exception) constructor.newInstance(exceptionMessage));
              } else {
                _urlExceptionMap.put(uri, new QuandlRuntimeException(exceptionMessage));
              }
            }
          } catch (Exception ex) {
            reader.close();
            throw new RuntimeException(ex);
          }
        }
      }
      reader.close();
    } catch (IOException ioe) {
      s_logger.error("Could not find index file (Index.csv) in {}, try doing a maven build", _rootPath, ioe); 
      throw new RuntimeException(ioe);
    }
  }
  
  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object.
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a CSV parsing problem or response code was unusual
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed JSON object
   */
  public JSONObject getJSONResponse(final WebTarget target) {
    String uri = removeAPIToken(target.getUriBuilder().build());
    if (_urlFileNameMap.containsKey(uri)) {
      // we have a file name to read from
      String fileName = _urlFileNameMap.get(uri);
      File file = new File(_rootPath, fileName);
      // should we be buffering this?
      try {
        FileReader fileReader = new FileReader(file);
        JSONTokener tokeniser = new JSONTokener(fileReader);
        JSONObject object = new JSONObject(tokeniser);
        fileReader.close();
        return object;
      } catch (JSONException je) {
        throw new QuandlRuntimeException("Problem parsing JSON", je);
      } catch (FileNotFoundException ex) {
        throw new QuandlRuntimeException("File named " + fileName + " in index cannot be found", ex);
      } catch (IOException ex) {
        throw new QuandlRuntimeException("Exception when closing file", ex);
    }
    } else if (_urlExceptionMap.containsKey(uri)) {
      Exception e = _urlExceptionMap.get(uri);
      e.fillInStackTrace();
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    } else {
      throw new QuandlRuntimeException("Cannot find " + uri + " in index");
    }
  }
  
  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV).
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a JSON parsing problem, network issue or response code was unusual
   * @param target the WebTarget describing the call to make, not null
   * @return the parsed TabularResult
   */
  public TabularResult getTabularResponse(final WebTarget target) {
    String uri = removeAPIToken(target.getUriBuilder().build());
    if (_urlFileNameMap.containsKey(uri)) {
      // we have a file name to read from
      String fileName = _urlFileNameMap.get(uri);
      File file = new File(_rootPath, fileName);
      // should we be buffering this?
      try {
        FileReader fileReader = new FileReader(file);
        CSVReader reader = new CSVReader(fileReader);
        String[] headerRow = reader.readNext();
        if (headerRow != null) {
          HeaderDefinition headerDef = HeaderDefinition.of(Arrays.asList(headerRow));
          List<Row> rows = new ArrayList<Row>();
          String[] next = reader.readNext();
          while (next != null) {
            Row row = Row.of(headerDef, next);
            rows.add(row);
            next = reader.readNext();
          }
          reader.close();
          TabularResult result = TabularResult.of(headerDef, rows);
          return result;
        } else {
          reader.close();
          QuandlRuntimeException ex = new QuandlRuntimeException("No data returned");
          throw ex;
        }
      } catch (IOException ioe) {
        throw new QuandlRuntimeException("Problem reading CSV", ioe);
      }
    } else if (_urlExceptionMap.containsKey(uri)) {
      Exception e = _urlExceptionMap.get(uri);
      e.fillInStackTrace();
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    } else {
      throw new QuandlRuntimeException("Cannot find " + uri + " in index");
    }
  }   
  
  private String removeAPIToken(final URI uri) {
    if (_apiKeyString != null) {
      return uri.toString().replace(_apiKeyString, ""); 
    } else {
      return uri.toString();
    }
  }

}
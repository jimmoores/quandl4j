package com.jimmoores.quandl.classic.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;
import com.jimmoores.quandl.processing.Request;
import com.jimmoores.quandl.processing.classic.ClassicRESTDataProvider;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.QuandlServiceUnavailableException;
import com.jimmoores.quandl.util.QuandlTooManyRequestsException;
import com.jimmoores.quandl.util.QuandlUnprocessableEntityException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * ClassicRESTDataProvider that creates local file system copies of the files it gets
 * and keeps an index.  Files are created in the current working directory.
 */
public final class RecordingRESTDataProvider implements ClassicRESTDataProvider {
  private static Logger s_logger = LoggerFactory.getLogger(RecordingRESTDataProvider.class);
  /**
   * The name of the index file.
   */
  public static final String INDEX_FILE_NAME = "Index.csv";
  private static final int FILE_BUFFER_SIZE = 65536;  // might want to tune this if used where performance matters.
  
  private CSVWriter _writer;
  private AtomicInteger _reponseNumber = new AtomicInteger();
  private File _rootPath;
  private String _apiKeyString;

  /**
   * Construct an instance that attempts to save recording data in testdata/ resource directory.
   * Throws a QuandlRuntimeException if it can't find the testdata/ resource directory
   * @param apiKey the Quandl API Key
   */
  public RecordingRESTDataProvider(final String apiKey) {
    if (apiKey != null) {
      _apiKeyString = GenericQuandlSessionInterface.AUTH_TOKEN_PARAM_NAME + "=" + apiKey + "&";
    }
    File file;
    try {
      file = new File(RecordingRESTDataProvider.class.getResource("testdata/").toURI());
      s_logger.info(file.getAbsolutePath());
      initWriter(file);
    } catch (URISyntaxException ex) {
      throw new QuandlRuntimeException("Problem parsing path of testdata directory", ex);
    }

  }
  /**
   * Construct an instance.  If you don't know where to put the files, try the default constructor.
   * @param rootPath the directory to store URI, Filename and exception data in
   * @param apiKey the Quandl API Key
   */
  public RecordingRESTDataProvider(final File rootPath, final String apiKey) {
    if (apiKey != null) {
      _apiKeyString = GenericQuandlSessionInterface.AUTH_TOKEN_PARAM_NAME + "=" + apiKey;
    }
    initWriter(rootPath);
  }
  
  private void initWriter(final File rootPath) {
    _rootPath = rootPath;
    try {
      File file = new File(_rootPath, INDEX_FILE_NAME);
      _writer = new CSVWriter(new FileWriter(file));
      _writer.writeNext(new String[] { RESTReponseIndexColumns.URI.getColumnLabel(), RESTReponseIndexColumns.FILE.getColumnLabel(), 
                                       RESTReponseIndexColumns.EXCEPTION_CLASS.getColumnLabel(), RESTReponseIndexColumns.EXCEPTION_MESSAGE.getColumnLabel() });
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }    
  }
  
  private void writeIndexEntry(final URI uri, final File file, final Exception e) {
    String clazz = null;
    String message = null;
    if (e != null) {
      clazz = e.getClass().getCanonicalName();
      message = e.getMessage();
    }
    _writer.writeNext(new String[] { removeAPIToken(uri), file.getName(), clazz, message });
    try {
      _writer.flush(); // in case someone doesn't close - remember this is not performance sensitive.
    } catch (IOException ioe) {
      s_logger.error("Problem flushing index", ioe);
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Close index file.
   */
  public void close() {
    try {
      _writer.close();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
  
  /**
   * Invoke a GET call on the web target and return the result as a parsed JSON object.
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a CSV parsing problem or response code was unusual
   * @param target  the WebTarget describing the call to make, not null
   * @param request  the request, or null
   * @return the parsed JSON object
   */
  public JSONObject getJSONResponse(final WebTarget target, final Request request) {
    Builder requestBuilder = target.request();
    Response response = requestBuilder.buildGet().invoke();
    File file = new File(_rootPath, "Response" + _reponseNumber.getAndIncrement() + ".json");
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      InputStream inputStream = response.readEntity(InputStream.class);
      try {
        saveFile(file, inputStream);
        // should we be buffering this?
        JSONTokener tokeniser = new JSONTokener(new FileReader(file));
        JSONObject object = new JSONObject(tokeniser);
        writeIndexEntry(target.getUriBuilder().build(), file, null);
        return object;
      } catch (JSONException jsone) {
        QuandlRuntimeException ex = new QuandlRuntimeException("Problem parsing JSON reply", jsone);
        writeIndexEntry(target.getUriBuilder().build(), file, ex);
        throw ex;
      } catch (IOException ioe) {
        QuandlRuntimeException ex = new QuandlRuntimeException("Problem saving file", ioe);
        writeIndexEntry(target.getUriBuilder().build(), file, ex);
        throw ex;
      }
    } else if (response.getStatus() == UNPROCESSABLE_ENTITY) {
      QuandlUnprocessableEntityException ex = new QuandlUnprocessableEntityException("Response code to " + target.getUri()
          + " was " + response.getStatusInfo());
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    } else if (response.getStatus() == TOO_MANY_REQUESTS) {
      QuandlTooManyRequestsException ex = new QuandlTooManyRequestsException("Response code to " + target.getUri() + " was " + response.getStatusInfo());      
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    } else if (response.getStatus() == SERVICE_UNAVAILABLE) {
      QuandlServiceUnavailableException ex = new QuandlServiceUnavailableException("Response code to " + target.getUri() + " was "
          + response.getStatusInfo());      
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    } else {
      s_logger.error("Error, reponse code was " + response.getStatus());
      QuandlRuntimeException ex = new QuandlRuntimeException("Response code to " + target.getUri() + " was " + response.getStatusInfo());
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    }
  }
  
  private String removeAPIToken(final URI uri) {
    if (_apiKeyString != null) {
      return uri.toString().replace(_apiKeyString, ""); 
    } else {
      return uri.toString();
    }
  }
  
  private void saveFile(final File file, final InputStream inputStream) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(file);
    int read = 0;
    byte[] bytes = new byte[FILE_BUFFER_SIZE];
    while ((read = inputStream.read(bytes)) != -1) {
      outputStream.write(bytes, 0, read);
    }
    outputStream.close();
  }
  
  /**
   * Invoke a GET call on the web target and return the result as a TabularResult (parsed CSV).
   * Throws a QuandlUnprocessableEntityException if Quandl returned a response code that indicates a nonsensical request
   * Throws a QuandlTooManyRequestsException if Quandl returned a response code indicating the client had made too many requests
   * Throws a QuandlRuntimeException if there was a JSON parsing problem, network issue or response code was unusual
   * @param target  the WebTarget describing the call to make, not null
   * @param request  the request, or null
   * @return the parsed TabularResult
   */
  public TabularResult getTabularResponse(final WebTarget target, final Request request) {
    Builder requestBuilder = target.request();
    Response response = requestBuilder.buildGet().invoke();
    File file = new File(_rootPath, "Response" + _reponseNumber.getAndIncrement() + ".csv");
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      InputStream inputStream = response.readEntity(InputStream.class);
      try {
        saveFile(file, inputStream);
        // should we be buffering this?
        CSVReader reader = new CSVReader(new FileReader(file));
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
          writeIndexEntry(target.getUriBuilder().build(), file, null);
          return result;
        } else {
          reader.close();
          QuandlRuntimeException ex = new QuandlRuntimeException("No data returned");
          writeIndexEntry(target.getUriBuilder().build(), file, ex);
          throw ex;
        }
      } catch (IOException ex) {
        QuandlRuntimeException qex = new QuandlRuntimeException("Problem reading result stream", ex);
        writeIndexEntry(target.getUriBuilder().build(), file, qex);
        throw qex;
      }
    } else if (response.getStatus() == UNPROCESSABLE_ENTITY) {
      QuandlUnprocessableEntityException ex = new QuandlUnprocessableEntityException(
          "Response code to " + target.getUri() + " was " 
          + response.getStatusInfo());
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    } else if (response.getStatus() == TOO_MANY_REQUESTS) {
      QuandlTooManyRequestsException ex = new QuandlTooManyRequestsException(
          "Response code to " + target.getUri() + " was " + response.getStatusInfo());      
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    } else if (response.getStatus() == SERVICE_UNAVAILABLE) {
      QuandlServiceUnavailableException ex = new QuandlServiceUnavailableException(
          "Response code to " + target.getUri() + " was " 
          + response.getStatusInfo());      
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    } else {
      QuandlRuntimeException ex = new QuandlRuntimeException(
          "Response code to " + target.getUri() + " was " + response.getStatusInfo());
      writeIndexEntry(target.getUriBuilder().build(), file, ex);
      throw ex;
    }
  }   

}
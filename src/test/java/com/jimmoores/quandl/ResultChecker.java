package com.jimmoores.quandl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.jimmoores.quandl.util.ArgumentChecker;
import com.jimmoores.quandl.util.PrettyPrinter;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * Class that either saves a sequence of results into files.
 */
public class ResultChecker implements ResultProcessor {
  private static Logger s_logger = LoggerFactory.getLogger(ResultChecker.class);
  
  private static final String LINE_SEPARATOR = String.format("%n");

  private File _baseDir;

  private AtomicLong _fileNumber = new AtomicLong();

  /**
   * Create a ResultChecker using the resource folder 'testresults' with it's filename counter set to zero.
   */
  public ResultChecker() {
    File file;
    try {
      file = new File(RecordingRESTDataProvider.class.getResource("testresults/").toURI());
      s_logger.info(file.getAbsolutePath());
      _baseDir = file;
    } catch (URISyntaxException ex) {
      throw new QuandlRuntimeException("Problem parsing path of testdata directory", ex);
    }
  }
  
  /**
   * Create a ResultSaver using a custom base directory with it's filename counter set to zero.
   * @param baseDir the directory in which to look for files
   */  
  public ResultChecker(final File baseDir) {
    ArgumentChecker.notNull(baseDir, "baseDir");
    _baseDir = baseDir;
  }
  
  /**
   * Load file using internal counter to generate file name and compare contents to the result provided.
   * @param tabularResult the result to check file contents against
   */
  public final void processResult(final TabularResult tabularResult) {
    File file = new File(_baseDir, TABULAR + _fileNumber.getAndIncrement() + TXT);
    if (file.exists()) {
      Assert.assertEquals(PrettyPrinter.toPrettyPrintedString(tabularResult), readFile(file));
    } else {
      Assert.fail("File " + file + " does not exist");
    }
  }

  /**
   * Load file using internal counter to generate file name and compare contents to the result provided.
   * @param metaDataResult the result to check file contents against
   */
  public final void processResult(final MetaDataResult metaDataResult) {
    processResult(metaDataResult.getRawJSON());
  }
  
  /**
   * Load file using internal counter to generate file name and compare contents to the result provided.
   * @param searchResult the result to check file contents against
   */
  public final void processResult(final SearchResult searchResult) {
    processResult(searchResult.getRawJSON());
  }
  
  private void processResult(final JSONObject jsonObject) {
    File file = new File(_baseDir, METADATA + _fileNumber.getAndIncrement() + JSON);
    if (file.exists()) {
      String value = PrettyPrinter.toPrettyPrintedString(jsonObject);
      String expected = readFile(file);
      outputDiff(value, expected);
      Assert.assertEquals(value, expected);
    } else {
      Assert.fail("File " + file + " does not exist");
    }
  }
  
  private void outputDiff(final String value, final String expected) {
    int i = 0;
    while (i < value.length() && i < expected.length() && value.charAt(i) == expected.charAt(i)) {
      i++;
    }
    if (value.length() != expected.length()) {
      s_logger.debug("lengths differ: value = {}, expected = {}", value.length(), expected.length());
    }
    if (i < value.length() && i < expected.length()) {
      s_logger.debug("Difference is at position " + i + " which contains character " + Character.getNumericValue(value.charAt(i)));
    }
  }
  
  private String readFile(final File file) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append(LINE_SEPARATOR);
      }
      reader.close();
      return sb.toString();
    } catch (IOException ex) {
      Assert.fail();
      return null; // unreachable, but compiler can't tell.
    }
  }
  
  
}

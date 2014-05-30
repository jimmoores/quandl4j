package com.jimmoores.quandl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.util.DefaultRESTDataProvider;
import com.jimmoores.quandl.util.PrettyPrinter;
import com.jimmoores.quandl.util.QuandlRuntimeException;
import com.jimmoores.quandl.util.RESTDataProvider;

/**
 * Grab test data from quandl by having a look through the search results and pulling out pseudo-randomized result sets.  These 
 * 'random' results are produced using a fixed seed, which means it produces the same sequence of test requests each time it is run.
 * This allows us to re-run tests and provide pre-recorded responses we got before very easily without indexing all the results.
 * One it has built a results list it can either save both the raw(ish) responses and parsed objects and then replay those results 
 * as a regression test.  Following the patterns followed in this class, it is possible to easily regression test your system that 
 * relies on Quandl data without hitting their servers.  This is both faster and more community friendly.
 * 
 * This class can be run as a TestNG test in which case it will run tests using the data in 
 * 
 *   src/test/resources/com/jimmoores/quandl/testdata
 * 
 * and 
 * 
 *   src/test/resources/com/jimmoores/quandl/testresults
 *   
 * If run on the command line, there are a number of options available (see command line help):
 *   --record        this will save responses and result objects for later 'replay', typically these will appear in
 *                   target/test-classes/com/jimmoores/quandl/test[data|results].
 *   --file-tests    run the tests using previously saved reponses and compare the resulting objects against previously saved
 *                   result objects.
 *   --direct-tests  run tests against Quandl directly and don't record anything, but check results against previously saved
 *                   result objects.  This may well legitimately fail due to changing fields like last updated.
 *   --api-key       pass in your API key.  Pretty much required as anonymous access has small request limit.
 *   --requests      override the number of randomized requests to run.  Default is 200
 *   --seed          override the default seed to produce a different sequence of results.  This will require re-recording 
 *                   results if used as the sequences of requests and reponses will no longer match.
 */
public final class RegressionTester {
  private static Logger s_logger = LoggerFactory.getLogger(RegressionTester.class);
  private static final String API_KEY = "U4c8PuHYsa61ECEorSGC";
  private static final int DAYS_PER_YEAR = 365;
  private static final int MAX_COLUMN = 5;
  private static final int DEFAULT_NUM_REQUESTS = 200;
  
  private static final double WITH_COLUMN_PROBABILITY = 0.1;
  private static final double WITH_FREQUENCY_PROBABILITY = 0.2;
  private static final double WITH_TRANSFORM_PROBABILITY = 0.1;
  private static final double WITH_MAX_ROWS_PROBABILITY = 0.1;
  
  private static final int MAX_ROWS = 400;
  private static final double WITH_START_DATE_PROBABILITY = 0.2;
  private static final int BASE_YEARS_FROM_EPOCH = 35;
  private static final int MAX_YEARS_OUT = 10;
  private static final double WITH_END_DATE_PROBABILITY = 0.2;
  private static final int MAX_PLUS_YEARS = 5;
  private static final long SEED = 2072619066;
  private static final int MAX_CODES_PER_MULTI_REQ = 20;
  private static final double PROBABILITY_SINGLE_COLUMN_REQ = 0.7;
  
  private static final String RECORD_OPTION_LONG = "record";
  private static final String RECORD_OPTION_SHORT = "r";
  private static final String FILE_TESTS_OPTION_LONG = "file-tests";
  private static final String FILE_TESTS_OPTION_SHORT = "f";
  private static final String DIRECT_TESTS_OPTION_LONG = "direct-tests";
  private static final String DIRECT_TESTS_OPTION_SHORT = "d";
  private static final String API_KEY_OPTION_LONG = "api-key";
  private static final String API_KEY_OPTION_SHORT = "a";
  private static final String REQUESTS_OPTION_LONG = "requests";
  private static final String REQUESTS_OPTION_SHORT = "req";
  private static final String SEED_OPTION_LONG = "seed";
  private static final String SEED_OPTION_SHORT = "s";
  
  private Random _random;
  private String _apiKey;
  private int _numRequests;

  private RegressionTester(final String apiKey, final long randomSeed, final int numRequests) {
    _apiKey = apiKey;
    _random = new Random(randomSeed);
    _numRequests = numRequests;
  }
  
  /** for TestNG. */
  private RegressionTester() {
    _apiKey = null;
    _random = new Random(SEED);
    _numRequests = DEFAULT_NUM_REQUESTS;
  }

  private void runRecording() {
    runTests(new RecordingRESTDataProvider(), new ResultSaver());
  }

  /**
   * Run tests using previously collected Quandl response data and compare with previously collected results.
   */
  @Test
  public void runFileBasedTests() {
    runTests(new FileRESTDataProvider(), new ResultChecker());
  }

  private void runDirectTests() {
    runTests(new DefaultRESTDataProvider(), new ResultChecker());
  }

  private void runTests(final RESTDataProvider restDataProvider, final ResultProcessor resultProcessor) {
    SessionOptions options;
    if (_apiKey != null) {
      options = SessionOptions.Builder
        .withAuthToken(API_KEY)
        .withRESTDataProvider(restDataProvider)
        .build();
    } else {
      options = SessionOptions.Builder
          .withAuthToken(API_KEY)
          .withRESTDataProvider(restDataProvider)
          .build();
    }
    QuandlSession session = QuandlSession.create(options);
    Set<String> quandlCodes = sampleSearch(session, resultProcessor);
    fuzzDataSetRequests(session, resultProcessor, quandlCodes);
    fuzzDataSetsRequests(session, resultProcessor, quandlCodes);
    runMetaDataRequests(session, resultProcessor, quandlCodes);
    runMultiMetaDataRequests(session, resultProcessor, quandlCodes);
    if (restDataProvider instanceof RecordingRESTDataProvider) {
      RecordingRESTDataProvider recordingRESTDataProvider = (RecordingRESTDataProvider) restDataProvider;
      recordingRESTDataProvider.close(); // a somewhat unpleasant special-case hack, probably not even necessary as each entry is flushed to disk, but being careful.
    }
  }

  /**
   * Run a set of data set requests using the provided quandl codes.
   * @param session the Quandl session
   * @param resultProcessor a result processor to ether record or check the results
   * @param quandlCodes a random set of Quandl codes to construct the requests from
   */
  private void fuzzDataSetRequests(final QuandlSession session, final ResultProcessor resultProcessor, final Set<String> quandlCodes) {
    for (String quandlCode : quandlCodes) {
      DataSetRequest req = fuzz(DataSetRequest.Builder.of(quandlCode)).build();
      try {
        TabularResult dataSet = session.getDataSet(req);
        s_logger.info(req.toString());
        s_logger.info(PrettyPrinter.toPrettyPrintedString(dataSet));
      } catch (QuandlRuntimeException qre) {
        s_logger.warn("Caught" + qre);
        s_logger.info("Continuing...");
      }
    }
  }

  /**
   * Run a set of Meta data requests using the provided quandl codes.
   * @param session the Quandl session
   * @param resultProcessor a result processor to ether record or check the results
   * @param quandlCodes a random set of Quandl codes to construct the requests from
   */
  private void runMetaDataRequests(final QuandlSession session, final ResultProcessor resultProcessor, final Set<String> quandlCodes) {
    for (String quandlCode : quandlCodes) {
      MetaDataRequest req = MetaDataRequest.of(quandlCode);
      try {
        MetaDataResult dataSet = session.getMetaData(req);
        resultProcessor.processResult(dataSet);
        s_logger.info(req.toString());
        s_logger.info(PrettyPrinter.toPrettyPrintedString(dataSet.getRawJSON()));
      } catch (QuandlRuntimeException qre) {
        s_logger.warn("Caught" + qre);
        s_logger.info("Continuing...");
      }
    }
  }

  /**
   * Run a set of randomized Multi data set requests, of varying size between 1 and MAX_CODES_PER_MULTI_REQ.
   * It will contains a randomized mixture of single and all column requests, according to PROBABILITY_SINGLE_COLUMN_REQ
   * The idea here is to stress test all the possible combinations of request types.
   * @param session the Quandl session
   * @param resultProcessor a result processor to either record or check the results
   * @param quandlCodes a random set of Quandl codes to construct requests from
   */
  private void fuzzDataSetsRequests(final QuandlSession session, final ResultProcessor resultProcessor, final Set<String> quandlCodes) {
    Iterator<String> iter = quandlCodes.iterator();
    while (iter.hasNext()) {
      int chunkSize = _random.nextInt(MAX_CODES_PER_MULTI_REQ - 1) + 1; // means we never get 0 sized chunks.
      List<QuandlCodeRequest> chunk = new ArrayList<QuandlCodeRequest>();
      while (iter.hasNext() && chunkSize > 0) {
        if (_random.nextDouble() > PROBABILITY_SINGLE_COLUMN_REQ) {
          chunk.add(QuandlCodeRequest.singleColumn(iter.next(), _random.nextInt(MAX_COLUMN)));
        } else {
          chunk.add(QuandlCodeRequest.allColumns(iter.next()));
        }
        chunkSize--;
      }
      MultiDataSetRequest req = fuzz(MultiDataSetRequest.Builder.of(chunk)).build();
      try {
        TabularResult dataSet = session.getDataSets(req);
        resultProcessor.processResult(dataSet);
        s_logger.info(req.toString());
        s_logger.info(PrettyPrinter.toPrettyPrintedString(dataSet));
      } catch (QuandlRuntimeException qre) {
        s_logger.warn("Caught exception", qre);
        s_logger.info("Continuing...");
      }
    }
  }

  /**
   * Run a set of randomized Multi meta data requests, of varying size between 1 and MAX_CODES_PER_MULTI_REQ.
   * @param session the Quandl session
   * @param resultProcessor a result processor to either record or check the results
   * @param quandlCodes a random set of Quandl codes to construct requests from
   */
  private void runMultiMetaDataRequests(final QuandlSession session, final ResultProcessor resultProcessor, final Set<String> quandlCodes) {
    Iterator<String> iter = quandlCodes.iterator();
    while (iter.hasNext()) {
      int chunkSize = _random.nextInt(MAX_CODES_PER_MULTI_REQ - 1) + 1; // means we never get 0 sized chunks.
      List<String> chunk = new ArrayList<String>();
      while (iter.hasNext() && chunkSize > 0) {
        chunk.add(iter.next());
        chunkSize--;
      }
      MultiMetaDataRequest req = MultiMetaDataRequest.of(chunk);
      try {
        MetaDataResult metaData = session.getMetaData(req);
        resultProcessor.processResult(metaData);
        s_logger.info(req.toString());
        s_logger.info(PrettyPrinter.toPrettyPrintedString(metaData.getRawJSON()));
      } catch (QuandlRuntimeException qre) {
        s_logger.warn("Caught exception", qre);
        s_logger.info("Continuing...");
      }
    }
  }

  /**
   * Perform a search to find how many documents there are in total and then choose random pages
   * and return a set of the quandl codes containing the first code in each page.  The idea here is to
   * give us a representative sample of the datasets available.
   * @param session the quandll session
   * @param resultProcessor a result processor to either save the results or check them
   * @return a randmly sampled set of quandl codes.
   */
  private Set<String> sampleSearch(final QuandlSession session, final ResultProcessor resultProcessor) {
    SearchResult result = session.search(SearchRequest.Builder.of("").build()); // return all available data sets.
    final int totalDocs = result.getTotalDocuments();
    final int docsPerPage = result.getDocumentsPerPage();
    final int totalPages = totalDocs / docsPerPage;
    Set<String> quandlCodes = new LinkedHashSet<String>();
    for (int i = 0; i < _numRequests; i++) {
      int pageRequired = _random.nextInt(totalPages);
      SearchRequest req = SearchRequest.Builder.of("").withPageNumber(pageRequired).build();
      System.out.println("About to run " + req);
      SearchResult searchResult = session.search(req);
      resultProcessor.processResult(searchResult);
      MetaDataResult metaDataResult = searchResult.getMetaDataResultList().get(0);
      quandlCodes.add(metaDataResult.getQuandlCode());
    }
    return quandlCodes;
  }

  private DataSetRequest.Builder fuzz(final DataSetRequest.Builder reqBuilder) {
    DataSetRequest.Builder builder = reqBuilder;
    if (_random.nextDouble() > WITH_COLUMN_PROBABILITY) {
      builder = builder.withColumn((int) (_random.nextDouble() * MAX_COLUMN));
    }
    if (_random.nextDouble() > WITH_FREQUENCY_PROBABILITY) {
      builder = builder.withFrequency(Frequency.values()[_random.nextInt(Frequency.values().length)]);
    }
    if (_random.nextDouble() > WITH_TRANSFORM_PROBABILITY) {
      builder = builder.withTransform(Transform.values()[_random.nextInt(Transform.values().length)]);
    }
    if (_random.nextDouble() > WITH_MAX_ROWS_PROBABILITY) {
      builder = builder.withMaxRows(_random.nextInt(MAX_ROWS));
    }
    LocalDate startDate = LocalDate.ofEpochDay((int) ((_random.nextDouble() * DAYS_PER_YEAR) * MAX_YEARS_OUT) + (BASE_YEARS_FROM_EPOCH * DAYS_PER_YEAR));
    if (_random.nextDouble() > WITH_START_DATE_PROBABILITY) {
      builder = builder.withStartDate(startDate);
    }
    if (_random.nextDouble() > WITH_END_DATE_PROBABILITY) {
      builder = builder.withEndDate(startDate.plusDays(_random.nextInt(DAYS_PER_YEAR * MAX_PLUS_YEARS)));
    }
    return builder;
  }

  private MultiDataSetRequest.Builder fuzz(final MultiDataSetRequest.Builder reqBuilder) {
    MultiDataSetRequest.Builder builder = reqBuilder;
    if (_random.nextDouble() > WITH_FREQUENCY_PROBABILITY) {
      builder = builder.withFrequency(Frequency.values()[_random.nextInt(Frequency.values().length)]);
    }
    if (_random.nextDouble() > WITH_TRANSFORM_PROBABILITY) {
      builder = builder.withTransform(Transform.values()[(int) (_random.nextInt(Transform.values().length))]);
    }
    if (_random.nextDouble() > WITH_MAX_ROWS_PROBABILITY) {
      builder = builder.withMaxRows(_random.nextInt(MAX_ROWS));
    }
    LocalDate startDate = LocalDate.ofEpochDay(_random.nextInt(MAX_YEARS_OUT * DAYS_PER_YEAR) + (BASE_YEARS_FROM_EPOCH * DAYS_PER_YEAR));
    if (_random.nextDouble() > WITH_START_DATE_PROBABILITY) {
      builder = builder.withStartDate(startDate);
    }
    if (_random.nextDouble() > WITH_END_DATE_PROBABILITY) {
      builder = builder.withEndDate(startDate.plusDays(_random.nextInt(DAYS_PER_YEAR * MAX_PLUS_YEARS)));
    }
    return builder;
  }

  /**
   * Entry point.
   * @param args command line parameters, not used
   */
  public static void main(final String[] args) {
    Parser parser = new GnuParser();
    CommandLine commandLine;
    try {
      commandLine = parser.parse(getOptions(), args);
    } catch (ParseException ex) {
      System.err.println("Could not parse command line options, exiting.");
      System.exit(1);
      return; // keep stupid compiler happy.
    }
    String apiKey = null;
    if (commandLine.hasOption(API_KEY_OPTION_SHORT)) {
      apiKey = commandLine.getOptionValue(API_KEY_OPTION_SHORT);
    }
    long randomSeed = SEED;
    if (commandLine.hasOption(SEED_OPTION_SHORT)) {
      try {
        randomSeed = Long.parseLong(commandLine.getOptionValue(SEED_OPTION_SHORT));
      } catch (NumberFormatException nfe) {
        System.err.println("Could not parse seed value (should be valid long), exiting.");
        System.exit(1);
      }
    }
    int numRequests = DEFAULT_NUM_REQUESTS;
    if (commandLine.hasOption(REQUESTS_OPTION_SHORT)) {
      try {
        randomSeed = Integer.parseInt(commandLine.getOptionValue(REQUESTS_OPTION_SHORT));
      } catch (NumberFormatException nfe) {
        System.err.println("Could not parse number of requests (should be valid int), exiting.");
        System.exit(1);
      }
    }    
    RegressionTester regressionTester = new RegressionTester(apiKey, randomSeed, numRequests);
    if (commandLine.hasOption(RECORD_OPTION_SHORT)) {
      regressionTester.runRecording();
    } else if (commandLine.hasOption(FILE_TESTS_OPTION_SHORT)) {
      regressionTester.runFileBasedTests();
    } else if (commandLine.hasOption(DIRECT_TESTS_OPTION_SHORT)) {
      regressionTester.runDirectTests();
    }
  }

  // we need to suppress warnings because of the broken implementation of the builder pattern in commons CLI.
  @SuppressWarnings("static-access")
  private static Options getOptions() {
    Options result = new Options();
    OptionGroup group = new OptionGroup();
    Option recordOption = OptionBuilder
        .withDescription("Send repeatable pseudo-random queries directly to Quandl and record the reponses and resulting objects in files")
        .withLongOpt(RECORD_OPTION_LONG)
        .create(RECORD_OPTION_SHORT);
    Option fileBasedTests = OptionBuilder
        .withDescription("Run repeatable pseudo-random queries against previously gathered reponses and regression test against previously gathered result objects")
        .withLongOpt(FILE_TESTS_OPTION_LONG)
        .create(FILE_TESTS_OPTION_SHORT);
    Option directTests = OptionBuilder
        .withDescription("Send repeatable pseudo-random queries directly to Quandl and regression test against previously gathered result objects")
        .withLongOpt(DIRECT_TESTS_OPTION_LONG)
        .create(DIRECT_TESTS_OPTION_SHORT);
    group.addOption(recordOption);
    group.addOption(fileBasedTests);
    group.addOption(directTests);
    result.addOptionGroup(group);
    Option apiKeyOption = OptionBuilder
        .withDescription("Specify an API key to use when making requests")
        .hasArg(true)
        .withArgName("The Quandl API Key")
        .isRequired(false)
        .withLongOpt(API_KEY_OPTION_LONG)
        .create(API_KEY_OPTION_SHORT);
    Option requestsOption = OptionBuilder
        .withDescription("Number of requests to fuzz (default 200)")
        .hasArg(true)
        .withArgName("The number of requests")
        .isRequired(false)
        .withLongOpt(REQUESTS_OPTION_LONG)
        .create(REQUESTS_OPTION_SHORT);
    Option randomSeedOption = OptionBuilder
        .withDescription("Override random seed")
        .hasArg(true)
        .withArgName("The new seed as a long in decimal")
        .isRequired(false)
        .withLongOpt(SEED_OPTION_LONG)
        .create(SEED_OPTION_SHORT);
    result.addOption(apiKeyOption);
    result.addOption(requestsOption);
    result.addOption(randomSeedOption);
    return result;
  }
}

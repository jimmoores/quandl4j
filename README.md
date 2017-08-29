Quandl4J : A Quandl library for Java
====================================
**NEWS: 2.0.0 released**

*The 2.0.0 release represents a substantial rewrite to allow the use of alternative types to hold tabular and meta-data.  The initial
implementations are **classic**, **tablesaw**.  Classic refers to the previous 1.x API's use of json.org's JSONObject type
for metadata and the home-grown TabularResult type for tabular data.  Tablesaw is new project build around an in-memory table 
implementation in the same vein as TabularResult, but taken much, much further, by allowing fast querying, filtering, and so on. 
Many thanks to Ben McCann for his suggestions and pull-requests, which kicked off development of 2.0.0.*

More details can be found in the [release notes](https://github.com/jimmoores/quandl4j/blob/master/RELEASE-NOTES.md).

# Introduction
[Quandl](http://quandl.com) is a source of millions of free data sets covering financial, economic, sociological and country data via an open REST API.  **Quandl4j** is a Java 7+ client-side wrapper for this API provided under the commercially friendly [Apache V2 license](http://www.apache.org/licenses/LICENSE-2.0.html).  It provides a type safe and fluent API in a modern style that takes care of constructing URLs and processing JSON and CSV responses but nonetheless allows access to all the functionality of the underlying REST API.

Quandl4J uses [Travis CI](http://travis-ci.org/jimmoores/quandl4j) to perform continuous builds.  The current status is [![Build Status](https://travis-ci.org/jimmoores/quandl4j.svg?branch=master)](https://travis-ci.org/jimmoores/quandl4j)

# Table of Contents
 - [Quick start](#quick-start)
 - [Design Principles](#design-principles)
 - [Release Notes](#release-notes)
 - [Tutorial](#tutorial)
 - [Documentation](#documentation)
 - [Roadmap](#roadmap)
 - [Contributions](#contributions)
 - [Community](#community)
 - [Versioning](#versioning)
 - [Creator](#creator)
 - [Copyright and license](#copyright-and-license)
 - [Gradle](#gradle)

### Quick Start
The minimum pre-requisites are:
 - OpenJDK 7, Oracle JDK 7 & 8 are tested.  The 2.0.0 release is the last major release that will support Java 7.
 - Maven 3.

Four options are available:
 - [Download the latest release](https://github.com/jimmoores/quandl4j/archive/rel/v2.0.0.zip)
 - Clone the repository: `git clone https://github.com/jimmoores/quandl4j.git`
   - Run `mvn install` to build the libray, test, javadoc and source jars and install to your local Maven repository.
   - Run `mvn javadoc:javadoc` to build the documentation.
 - Add the following fragment to your Maven POM file

```xml
<dependency>
  <groupId>com.jimmoores</groupId>
  <artifactId>quandl</artifactId>
  <version>2.0.0</version>
</dependency>
```

 - or in gradle

``` groovy
dependencies {
    compile 'com.jimmoores:quandl:2.0.0'
}
```

### Design Principles
The core design principles are:
 - Allow full access to the functionality of the underlying API.
 - Allow efficient network requests by using the more compact CSV encoding where
   possible.
 - Use modern Java design principles like immutable objects, builders and 
   factories and JSR-310 style date/times (using the [ThreeTen backport](http://threeten.org) so Java 7 is supported)
 - Thorough unit and integration test support, including a framework that can
   be reused by user applications without hitting the Quandl backend.
 - Publish maven artifacts on [Maven Central](http://search.maven.org/).
 - Provide concrete examples.
 - Provide comprehensive documentation and JavaDocs.

## Release Notes
### Version 2.0.0
 - A fairly comprehensive overhaul.  The primary aim was to allow the use of alternative types to hold tabular and metadata.  A common
user question has been around the choice of JSON or Table representation, and these are now abstracted in a way that allows you to 
choose types that best suit your application, and even add your own very easily.  This version is fully source compatible with previous
versions, although most existing session classes and interfaces have been deprecated.  Updating existing code is very simple, see the
[2.0.0 migration guide](#2.0.0-migration-guide) for more information.

### Version 1.5.0
 - Calls upgraded to use V3 of the REST API for both data and metadata. No API changes. Logback is removed as a normal dependency to 
allow users to choose their own implementation of SLF4J (which is the whole point of SLF4J!). If you have any build issues try 
adding logback to your own pom.xml. Examples are moved to test packages and commons-cli dependency is now only a dependency for 
test scope. Docs and contributors list updated.

### Version 1.4.2
 - This is a little tweak release, user William Farrugia suggested updating 
the version of Jackson used so it can be used with AWS.

### Version 1.4.1
 - This is a bug fix release, @Olivier-92 reported a resource leak that affects
those using RESTEasy as a JAX-RS provider.  The request objects are now closed
as required.

### Version 1.4.0
 - POM references to external OpenGamma Maven repository have been removed and the JSON library referred to has been switched for
Jackson using the json.org datatype module.  This should require no code changes in users outside of perhaps a POM change if you've
referred to the OpenGamma POM in your POM.

### Version 1.3.0
 - SearchRequest now supports the v3 API databasecode argument and makes the query
parameter (previous `of()`) optional.  This means the `of()` static factory method
is now deprecated in favor of a no-arg constructor.
``` java
SearchRequest.Builder.of(<query>).build();
```
becomes
``` java
new SearchRequest.Builder().withQuery(<query>).build();
```

### Version 1.2.0
 - Changed HTTPS URL because RESTEasy and Apache CXF can't handle redirect to https://www.quandl.com which is the address in the TLS certificate. Disabled integration test from main build because test framework misbehaving.

### Version 1.1.0
 - Switch to HTTPS, regenerate test data.

### Version 1.0.0
 - Handle deprecation of all multi-request APIs.  This release emulates the old behaviour of the multiset APIs by issuing
   multiple single requests and aggregating results into the same structure as returned before.  This should allow existing 
   applications to adjust seamlessly, abeit probably at reduced performance.  Please note though, that these APIs are now
   deprecated and you should plan for their eventual removal.
 - High loads on the Quandl servers have lead to an increase in the number of requests that return errors asking the client
   to throttle requests apart from when user maximum request counts are exceeded.  The QuandlSession will now retry requests
   that are likely to be transient (503 Service Unavailable and 429 Too Many Requests) according to a `RetryPolicy` that
   can be set in the SessionOptions.  The default is to back off for 1, 5, 20 and then 60 seconds and then give up.  Custom 
   policies can be put in place by subclassing `RetryPolicy`.  Some examples are available via factory methods on    
   `RetryPolicy`.
 - Emulated multiset queries (both Tabular and Metadata) now return Quandl codes of the form PROVIDER/CODE rather than
   PROVIDER.CODE.  This was only noticed after release.  If this causes big problems for you, please open a ticket and I will    provide the facility to revert to the old behaviour in a new release.
 - The new retry behaviour means that there are times that would previously have thrown a generic `QuandlRuntimeException`
   will now return `QuandlFailedRequestException` (which is thrown after receiving multiple   
   `QuandlTooManyRequestsException` or `QuandlServiceUnavailableException`).  Note that previously a bug prevented the 
   correct throwing of `QuandlTooManyRequestsException`.
 - To revert as closely as possible to the old behaviour (turn off retries), use `RetryPolicy.createNoRetryPolicy()` and
   set in the `SessionOptions`.
 - `getMetaData(final MultiMetaDataRequest request)` does it's best to emulate the JSON response from Quandl, but may be
   have fields like `Frequency` set to null rather than values that Quandl returned.
 - A missing dependency for Java 7 users has been added that allows the examples to run cleanly.
 - Test files were regenerated.

### Version 0.9.0
 - Skipped because it was used internally by the author in a private Maven repository for an intermediate version and never
   curated for Maven Central.

### Version 0.8.1
 - Fixed some POM issues.

### Version 0.8.0
 - Initial public release.

## Tutorial
Since 2.0.0, you can choose the types used to return data from a session by using the appropriate session object.  This tutorial 
considers the *classic* session style that returns data using the **json.org** module of the Apache Jackson library to return metadata
as `JSONObject`s, and uses a table class provided by Quandl4J since the beginning called `TabularResult`.  The same API structure 
apply to all sessions though.

### A First Taste of the API
The following gets the complete data history (with Date, Open, High, Low, Volume, Ex-Dividend, Split Ratio, And Adjusted Open, High, Low Close and Volume columns) of AAPL (Apple Inc).  The symbol `WIKI/AAPL` is what is known as the **Quandl code** and is made up of a data source (in this case `WIKI`) and a data source specific code (in this case the exchange code for Apple Inc, which is `AAPL`).
```java
// Example1.java
ClassicQuandlSession session = ClassicQuandlSession.create();
TabularResult tabularResult = session.getDataSet(
  DataSetRequest.Builder.of("WIKI/AAPL").build());
System.out.println(tabularResult.toPrettyPrintedString());
```
which produces
```
+------------+--------+----------+----------+----------+-------------+-------------+-------------+-----------------+-----------------+-----------------+-----------------+-------------+
| Date       | Open   | High     | Low      | Close    | Volume      | Ex-Dividend | Split Ratio | Adj. Open       | Adj. High       | Adj. Low        | Adj. Close      | Adj. Volume |
+------------+--------+----------+----------+----------+-------------+-------------+-------------+-----------------+-----------------+-----------------+-----------------+-------------+
| 2017-08-28 | 160.14 | 162.0    | 159.93   | 161.47   | 25279674.0  | 0.0         | 1.0         | 160.14          | 162.0           | 159.93          | 161.47          | 25279674.0  |
| 2017-08-25 | 159.65 | 160.56   | 159.27   | 159.86   | 25015218.0  | 0.0         | 1.0         | 159.65          | 160.56          | 159.27          | 159.86          | 25015218.0  |
... snip ...
| 1980-12-17 | 25.88  | 26.0     | 25.88    | 25.88    | 385900.0    | 0.0         | 1.0         | 3.0057815128016 | 3.0197186759213 | 3.0057815128016 | 3.0057815128016 | 3087200.0   |
| 1980-12-16 | 25.38  | 25.38    | 25.25    | 25.25    | 472000.0    | 0.0         | 1.0         | 2.9477099998031 | 2.9477099998031 | 2.9326114064235 | 2.9326114064235 | 3776000.0   |
| 1980-12-15 | 27.38  | 27.38    | 27.25    | 27.25    | 785200.0    | 0.0         | 1.0         | 3.1799960517971 | 3.1799960517971 | 3.1648974584175 | 3.1648974584175 | 6281600.0   |
| 1980-12-12 | 28.75  | 28.88    | 28.75    | 28.75    | 2093900.0   | 0.0         | 1.0         | 3.3391119974129 | 3.3542105907925 | 3.3391119974129 | 3.3391119974129 | 16751200.0  |
+------------+--------+----------+----------+----------+-------------+---------0---+-------------+-----------------+-----------------+------------------+-----------------+-------------+
```
### Refining the query
It's also possible to specify many refining options on your query.  In this next example we
request AAPL again, but this time sampled Quarterly, returning only the close column (CLOSE_COLUMN here is actually the integer constant 4), and performing a normalization pre-process step on the server side before returning the results.
```java
// Example2.java
QuandlSession session = QuandlSession.create();
TabularResult tabularResult = session.getDataSet(
  DataSetRequest.Builder
    .of("WIKI/AAPL")
    .withFrequency(Frequency.QUARTERLY)
    .withColumn(CLOSE_COLUMN)
    .withTransform(Transform.NORMALIZE)
    .build());
System.out.println(tabularResult.toPrettyPrintedString());
```
which will return something like
```
+------------+-----------------+
| Date       | Close           |
+------------+-----------------+
| 2014-06-30 | 1868.5228604924 |
| 2014-03-31 | 1573.0949589683 |
| 2013-12-31 | 1644.2555685815 |
| 2013-09-30 | 1397.2743259086 |
| 2013-06-30 | 1162.162954279  |
... snip ...
| 1982-03-31 | 49.47245017585  |
| 1981-12-31 | 64.830011723329 |
| 1981-09-30 | 44.695193434936 |
| 1981-06-30 | 76.20164126612  |
| 1981-03-31 | 71.805392731536 |
| 1980-12-31 | 100.0           |
+------------+-----------------+
```
note that the whole series is normalized against the first value.

### Structure of a TabularResult
The type `TabularResult` is made up of a `HeaderDefinition`, which is essentially a list of column names (plus the facility to map from name to column index), plus a list of `Row` objects, each of which is linked to their common `HeaderDefinition`.  This allows individual `Row` objects to address their data using the column name rather than just the index as is the underlying API.  `Row` also contains methods to parse and cast data into various types (String, LocalDate, Double).  Here is an example
```java
// Example3.java
ClassicQuandlSession session = ClassicQuandlSession.create();
TabularResult tabularResult = session.getDataSet(
DataSetRequest.Builder
      .of("SSE/VROS") // VERIANOS REAL ESTATE AG on Boerse Stuttgart
      .withColumn(3) // Last (looked up previously)
      .withStartDate(RECENTISH_DATE)
      .withFrequency(Frequency.MONTHLY)
      .build());
System.out.println("Header definition: " + tabularResult.getHeaderDefinition());
for (final Row row : tabularResult) {
  LocalDate date = row.getLocalDate("Date");
  Double value = row.getDouble("Last");
  System.out.println("Value on date " + date + " was " + value);
} 
```
produces:
```
Header definition: HeaderDefinition[Date,Last]
Value on date 2017-08-31 was 1.35
Value on date 2017-07-31 was 1.45
Value on date 2017-06-30 was 1.427
'''

### Single meta data request
It's also possible to retrieve meta-data about the data sets available.
```java
// Example4.java
QuandlSession session = QuandlSession.create();
MetaDataResult metaData = session.getMetaData(MetaDataRequest.of("WIKI/AAPL"));
System.out.println(metaData.toPrettyPrintedString());
```
which prints out the raw JSON of the underlying message
```json
{
  "code": "AAPL",
  "column_names": [
    "Date",
    "Open",
    "High",
    "Low",
    "Close",
    "Volume",
    "Ex-Dividend",
    "Split Ratio",
    "Adj. Open",
    "Adj. High",
    "Adj. Low",
    "Adj. Close",
    "Adj. Volume"
  ],
  "description": "\r\n<p>End of day open, high, low, close and volume, dividends and splits, and split/dividend adjusted open, high, low close and volume for Apple Inc. (AAPL). Ex-Dividend is non-zero on ex-dividend dates. Split Ratio is 1 on non-split dates. Adjusted prices are calculated per CRSP (<a href=\"http://www.crsp.com/products/documentation/crsp-calculations\" rel=\"nofollow\" target=\"blank\">www.crsp.com/products/documentation/crsp-calculations<\/a>)<\/p>\r\n\r\n<p>This data is in the public domain. You may copy, distribute, disseminate or include the data in other products for commercial and/or noncommercial purposes.<\/p>\r\n\r\n<p>This data is part of Quandl's Wiki initiative to get financial data permanently into the public domain. Quandl relies on users like you to flag errors and provide data where data is wrong or missing. Get involved: <a href=\"mailto:connect@quandl.com\" rel=\"nofollow\" target=\"blank\">connect@quandl.com<\/a>\r\n<\/p>\r\n",
  "display_url": "http://www.quandl.com/WIKI/AAPL",
  "errors": {},
  "frequency": "daily",
  "from_date": "1980-12-12",
  "id": 9775409,
  "name": "Apple Inc. (AAPL) Prices, Dividends, Splits and Trading Volume",
  "private": false,
  "source_code": "WIKI",
  "source_name": "Quandl Open Data",
  "to_date": "2014-06-03",
  "type": null,
  "updated_at": "2014-06-03T20:58:42Z",
  "urlize_name": "Apple-Inc-AAPL-Prices-Dividends-Splits-and-Trading-Volume"
}
```
The raw JSON message is accessible, but the intention is for the user to mostly use the convenience methods available to extract named fields (with type casts) and process the `column_names` array into a `HeaderDefinition`.
### Bulk meta-data
This uses an undocumented feature of Quandl, which allows you to make requests for JSON to the multisets endpoint.  To this we add the parameter to limit the start date to a date far in the future.  This means we only get the meta-data.  It's limited to only providing enough data to determine the available columns, but that's quite useful in itself.  There are two calls that can process this `MultiMetaDataRequest`.  The first is an overloaded version of `getMetaData`.  Again, it's worth noting we're allowed to use the normal form of Quandl codes here: in the REST API, the forward slash gets replaced with a full-stop/period in this context.
```java
// Example5.java
QuandlSession session = QuandlSession.create();
MetaDataResult metaData = session.getMetaData(MultiMetaDataRequest.of("WIKI/AAPL", "DOE/RWTC", "WIKI/MSFT"));
System.out.println(metaData.toPrettyPrintedString());
```
which returns a large JSON document wrapped in a normal MetaDataResult object.
```json
{
  "column_names": [
    "Date",
    "WIKI/AAPL - Open",
    "WIKI/AAPL - High",
    "WIKI/AAPL - Low",
    "WIKI/AAPL - Close",
    "WIKI/AAPL - Volume",
    "WIKI/AAPL - Ex-Dividend",
    "WIKI/AAPL - Split Ratio",
    "WIKI/AAPL - Adj. Open",
    "WIKI/AAPL - Adj. High",
    "WIKI/AAPL - Adj. Low",
    "WIKI/AAPL - Adj. Close",
    "WIKI/AAPL - Adj. Volume",
    "DOE/RWTC - Value",
    "WIKI/MSFT - Open",
    "WIKI/MSFT - High",
    "WIKI/MSFT - Low",
    "WIKI/MSFT - Close",
    "WIKI/MSFT - Volume",
    "WIKI/MSFT - Ex-Dividend",
    "WIKI/MSFT - Split Ratio",
    "WIKI/MSFT - Adj. Open",
    "WIKI/MSFT - Adj. High",
    "WIKI/MSFT - Adj. Low",
    "WIKI/MSFT - Adj. Close",
    "WIKI/MSFT - Adj. Volume"
  ],
  "columns": [
    "Date",
    "Open",
    "High",
    "Low",
    "Close",
    "Volume",
    "Ex-Dividend",
    "Split Ratio",
    "Adj. Open",
    "Adj. High",
    "Adj. Low",
    "Adj. Close",
    "Adj. Volume",
    "Value",
    "Open",
    "High",
    "Low",
    "Close",
    "Volume",
    "Ex-Dividend",
    "Split Ratio",
    "Adj. Open",
    "Adj. High",
    "Adj. Low",
    "Adj. Close",
    "Adj. Volume"
  ],
  "data": [],
  "errors": {},
  "frequency": "annual",
  "from_date": null,
  "to_date": null
}
```
A more generally useful method though, is to use the `getMultipleHeaderDefinition()` method
```java
// Example6.java
QuandlSession session = QuandlSession.create();
Map<String, HeaderDefinition> headers = session.getMultipleHeaderDefinition(MultiMetaDataRequest.of("WIKI/AAPL", "DOE/RWTC", "WIKI/MSFT"));
System.out.println(PrettyPrinter.toPrettyPrintedString(headers));
```
which returns the following map (`PrettyPrinter` contains a PrettyPrinter for these maps too):
```
WIKI/AAPL => Date, Open, High, Low, Close, Volume, Ex-Dividend, Split Ratio, Adj. Open, Adj. High, Adj. Low, Adj. Close, Adj. Volume
DOE/RWTC  => Date, Value
WIKI/MSFT => Date, Open, High, Low, Close, Volume, Ex-Dividend, Split Ratio, Adj. Open, Adj. High, Adj. Low, Adj. Close, Adj. Volume
```
### Searching
We can also make generalised free-text search requests to Quandl.  For this we use the `search(SearchRequest)` method.  This allows us to specify the maximum number of results per page, and also the page we want.  Note that queries with high page numbers are slow, presumably due to the server-side database having to project the entire result set of several million documents just to get the single page you want.  Try not to add to server load by making these requests excessively.
```java
// Example7.java
QuandlSession session = QuandlSession.create();
SearchResult searchResult = session.search(new SearchRequest.Builder().withQuery("Apple").withMaxPerPage(2).build());
System.out.println(searchResult.toPrettyPrintedString());
````
results in
```json
{
  "current_page": 1,
  "docs": [
    {
      "code": "NASDAQ_AAPL",
      "column_names": [
        "Date",
        "Open",
        "High",
        "Low",
        "Close",
        "Volume"
      ],
      "description": "Apple Inc. (Apple) designs, manufactures and markets mobile communication and media devices, personal computers, and portable digital music players, and a variety of related software, services, peripherals, networking solutions, and third-party digital content and applications. The Company's products and services include iPhone, iPad, Mac, iPod, Apple TV, a portfolio of consumer and professional software applications, the iOS and OS X operating systems, iCloud, and a variety of accessory, service and support offerings. The Company also delivers digital content and applications through the iTunes Store, App StoreSM, iBookstoreSM, and Mac App Store. The Company distributes its products worldwide through its retail stores, online stores, and direct sales force, as well as through third-party cellular network carriers, wholesalers, retailers, and value-added resellers. In February 2012, the Company acquired app-search engine Chomp.",
      "display_url": "http://www.google.com/finance/historical?q=NASDAQ%3AAAPL&startdate=Jan+1%2C+1990&output=csv",
      "frequency": "daily",
      "from_date": "1981-03-11",
      "id": 2318865,
      "name": "Apple Inc. (AAPL)",
      "private": false,
      "source_code": "GOOG",
      "source_name": "Google Finance",
      "to_date": "2014-06-03",
      "type": null,
      "updated_at": "2014-06-04T02:27:30Z",
      "urlize_name": "Apple-Inc-AAPL"
    },
    {
      "code": "AAPL_CASH",
      "column_names": [
        "Date",
        "Cash"
      ],
      "description": "Cash and Marketable Securities reported in the balance sheet. Units: millions of dollars. Corporate Finance data is collected and calculated by Prof. Aswath\nDamodaran, Professor of Finance at the Stern School of Business, New\nYork University.  The raw data is available here:\nhttp://pages.stern.nyu.edu/~adamodar/New_Home_Page/data.html",
      "display_url": "http://pages.stern.nyu.edu/~adamodar/New_Home_Page/data.html",
      "frequency": "annual",
      "from_date": "2000-09-27",
      "id": 3861439,
      "name": "Apple Inc. ( AAPL ) - Cash",
      "private": false,
      "source_code": "DMDRN",
      "source_name": "Damodaran Financial Data",
      "to_date": "2013-09-27",
      "type": null,
      "updated_at": "2014-05-15T18:07:39Z",
      "urlize_name": "Apple-Inc-AAPL-Cash"
    }
  ],
  "per_page": 2,
  "sources": [
    {
      "code": "DMDRN",
      "datasets_count": 0,
      "description": "",
      "host": "pages.stern.nyu.edu/~adamodar/",
      "id": 6946,
      "name": "Damodaran Financial Data"
    },
    {
      "code": "GOOG",
      "datasets_count": 43144,
      "description": "This data is NOT sourced directly from Google.  It is however verified against their numbers.\r\n\r\nwww.quandl.com/WIKI is a better source.",
      "host": "www.google.com",
      "id": 393,
      "name": "Google Finance"
    }
  ],
  "total_count": 4563
}
```
More usefully though, there are variously helper methods on SearchResult to allow you to get the document count, number of documents per page and to extract the individual matches as separate MetaDataResult objects.  For example
```java
// Example8.java
QuandlSession session = QuandlSession.create();
SearchResult searchResult = session.search(new SearchRequest.Builder().withQuery("Apple").withMaxPerPage(2).build());
System.out.println("Current page:" + searchResult.getCurrentPage());
System.out.println("Documents per page:" + searchResult.getDocumentsPerPage());
System.out.println("Total matching documents:" + searchResult.getTotalDocuments());
for (MetaDataResult document : searchResult.getMetaDataResultList()) {
  System.out.println("Quandl code " + document.getQuandlCode() + " matched");
  System.out.println("Available columns are: " + document.getHeaderDefinition());
}
```
produces
```
Current page:1
Documents per page:2
Total matching documents:4563
Quandl code GOOG/NASDAQ_AAPL matched
Available columns are: HeaderDefinition[Date,Open,High,Low,Close,Volume]
Quandl code DMDRN/AAPL_CASH matched
Available columns are: HeaderDefinition[Date,Cash]
```

### Quandl API Key and Session Options
To be allowed to use more than 50 requests per day it is necessary to [sign up for an account at quandl.com](https://www.quandl.com/users/sign_up).  This typically allows up to 50,000 requests per day.  When you've created your account, you can find your 'Auth Token' on your account details page.  There are several ways to pass this into Quandl4J:

1. Set the Java sytem property `quandl.auth.token` to your Auth Token (`-Dquandl.auth.token=MYAUTHTOKEN` when launching the JVM using the `java` launcher) and then call `QuandlSession.create()`.
2. Pass the token as a `String` parameter to `QuandlSession.create(String)` when you create the session.
3. Create a `SessionOptions` object using it's builder and pass your Auth Token into the `withAuthToken(String)` method and then pass the resulting `SessionOptions` object into the `QuandlSession.create(SessionOptions)` method.

Other properties of `SessionOptions` are the `RESTDataProvider` which is used to allow the use of a testing class that records requests in files, and `RetryPolicy` (`withRetryPolicy(RetryPolicy)` in the builder), which tells the session how to deal with failed requests.  Out of the box, there are three `RetryPolicy` types available:

 - `NoRetryPolicy`, created using `RetryPolicy.createNoRetryPolicy()`.  This doesn't retry and all and throws exceptions immediately.
 - `FixedRetryPolicy`, created using `RetryPolocy.createFixedRetryPolicy(int, long)`.  This will retry a maximum of <int> number of times, with a delay of <long> between each one.
 - `SequenceRetryPolicy`, createed using `RetryPolicy.createSequenceRetryPolocy(long[])`.  This will retry, waiting the number of milliseconds in each array element, until the array is exhausted, at which point the policy will give up.
 
The default policy is `SequenceRetryPolicy(new long[] {1000, 5000, 20000, 60000})`, which will back off first by 1 second, then 5, then 20 and finally a full minute.  Custom policies can be created by subclassing `RetryPolicy` yourself.

### Documentation
An addition to the tutorial, there is extra documentation at the package and class level within the [JavaDocs, which are hosted in GitHub Pages](http://jimmoores.github.io/quandl4j/apidocs).

### Roadmap
Some future plans for incorporation include:
 - Caching layer to speed up queries and minimize quandl traffic.
 - Ability to subselect columns/date ranges out from `TabularResult`.
 - Example Swing UI.
 - Locally stored data with ability to update (i.e. persistent cache).
 - Ability to specify column names in requests that will use cached metadata where possible or fall back to 
   performing a metadata request prior to a data request.

### Contributions
Contributions are welcome!  Please read through the [contributing guidelines](http://github.com/jimmoores/quandl4j/blob/master/CONTRIBUTING.md).  This gives guidelines on opening issues, coding standards and testing requirements.

### Community
Follow development here via
 - Twitter
 - Email

### Versioning
Releases will be numbered with the format `<major>.<minor>.<patch-level>`.  When to bump a version number will be dictated by:
 - Breaking backwards API compatibility will mean a bump in the major version number.
 - New features that retain backwards compatibility will require a minor version number bump.
 - Pure bug fixes will bump the patch-level.
 
### Creator
**Jim Moores**
 - <http://twitter.com/jim_moores>
 - <http://github.com/jimmoores>
 - <https://www.linkedin.com/pub/jim-moores/0/442/841>
 
### Copyright and license

Code and documentation Copyright (C) 2014 Jim Moores.  Code and documentation released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

### Gradle
As of release 1.4.0, you should not need to add the section below, but I'm retaining it here in case anyone has issues with older 
versions.  If you have success without this, please raise an issue and I will remove this section.

Because Quandl4J prior to 1.4.0 used an artifact not in Maven central you'll need some extra information:
```
repositories {
    maven {
        // Quandl4j has a dependency not in Maven: 'com.opengamma.external.json:json:1.0.0.v20101106'
        // Source of this URL: https://github.com/jimmoores/quandl4j/blob/master/pom.xml
        url "http://maven.opengamma.com/nexus/content/groups/public"
    }
}
```

For completeness, here's all the magic text we need in our `build.gradle` file:

```
repositories {
   // Or use mavenCentral(). I prefer new and cool shit + HTTPS, hence jcenter():
    jcenter()
}

repositories {
    maven {
        // Quandl4j has a dependency not in Maven: 'com.opengamma.external.json:json:1.0.0.v20101106'
        // Source of this URL: https://github.com/jimmoores/quandl4j/blob/master/pom.xml
        url "http://maven.opengamma.com/nexus/content/groups/public"
    }
}

dependencies {
    // note this is now obsolete, you should use 1.4.1 and you won't need the repositories above
    compile 'com.jimmoores:quandl:1.3.0'
}
```
Thanks to Martin Andersson for this contribution.


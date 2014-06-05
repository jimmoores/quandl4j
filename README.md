Quandl4J : A Quandl library for Java
====================================

[Quandl](http://quandl.com) is an source of millions of free data sets covering financial, economic, sociological and country data via an open REST API.  **Quandl4j** is a Java 7+ client-side wrapper for this API provided under the commercially friendly [Apache V2 license](http://www.apache.org/licenses/LICENSE-2.0.html).  It provides a type safe and fluent API in a modern style that takes care of constructing URLs and processing JSON and CSV responses but nonetheless allows access to all the functionality of the underlying REST API.

# Table of Contents
 - [Quick start](#quick-start)
 - [Design Principles](#design-principles)
 - [Tutorial](#tutorial)
 - [Documentation](#documentation)
 - [Roadmap](#roadmap)
 - [Contributing](#contributing)
 - [Community](#community)
 - [Versioning](#versioning)
 - [Bugs and feature requests](#bugs-and-feature-requests)
 - [Creator](#creator)
 - [Copyright and license](#copyright-and-license)

### Quick Start

Three options are available:
 - ~~[Download the latest release](https://github.com/jimmoores/quandl4j/archive/v0.9.zip)~~ **COMING SOON**
 - Clone the repository: `git clone https://github.com/jimmoores/quandl4j.git`
 - ~~Add the following fragment to your Maven POM file~~ **COMING SOON**
```xml
<dependency>
  <groupId>com.jimmoores</groupId>
  <artifactId>quandl4j</artifactId>
  <version>0.9</version>
</dependency>
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

## Tutorial
### A First Taste of the API
The following gets the complete data history (with Date, Open, High, Low, Volume, Ex-Dividend, Split Ratio, And Adjusted Open, High, Low Close and Volume columns) of AAPL (Apple Inc).  The symbol `WIKI/AAPL` is what is known as the **Quandl code** and is made up of a data source (in this case `WIKI`) and a data source specific code (in this case the exchange code for Apple Inc, which is `AAPL`).
```java
// Example1.java
QuandlSession session = QuandlSession.create();
TabularResult tabularResult = session.getDataSet(
  DataSetRequest.Builder.of("WIKI/AAPL").build());
System.out.println(tabularResult.toPrettyPrintedString());
```
which produces
```
+------------+--------+----------+----------+----------+------------+-------------+-------------+-----------------+-----------------+-----------------+-----------------+-------------+
| Date       | Open   | High     | Low      | Close    | Volume     | Ex-Dividend | Split Ratio | Adj. Open       | Adj. High       | Adj. Low        | Adj. Close      | Adj. Volume |
+------------+--------+----------+----------+----------+------------+-------------+-------------+-----------------+-----------------+-----------------+-----------------+-------------+
| 2014-06-03 | 628.47 | 638.74   | 628.25   | 637.54   | 10419625.0 | 0.0         | 1.0         | 628.47          | 638.74          | 628.25          | 637.54          | 10419625.0  |
| 2014-06-02 | 634.0  | 634.83   | 622.5    | 628.65   | 13149746.0 | 0.0         | 1.0         | 634.0           | 634.83          | 622.5           | 628.65          | 13149746.0  |
| 2014-05-30 | 637.98 | 644.17   | 628.9    | 633.0    | 20073091.0 | 0.0         | 1.0         | 637.98          | 644.17          | 628.9           | 633.0           | 20073091.0  |
| 2014-05-29 | 628.0  | 636.87   | 627.77   | 635.38   | 13352669.0 | 0.0         | 1.0         | 628.0           | 636.87          | 627.77          | 635.38          | 13352669.0  |
... snip ...
| 1980-12-17 | 25.88  | 26.0     | 25.88    | 25.88    | 385900.0    | 0.0        | 1.0         | 3.0057815128016 | 3.0197186759213 | 3.0057815128016 | 3.0057815128016 | 3087200.0   |
| 1980-12-16 | 25.38  | 25.38    | 25.25    | 25.25    | 472000.0    | 0.0        | 1.0         | 2.9477099998031 | 2.9477099998031 | 2.9326114064235 | 2.9326114064235 | 3776000.0   |
| 1980-12-15 | 27.38  | 27.38    | 27.25    | 27.25    | 785200.0    | 0.0        | 1.0         | 3.1799960517971 | 3.1799960517971 | 3.1648974584175 | 3.1648974584175 | 6281600.0   |
| 1980-12-12 | 28.75  | 28.88    | 28.75    | 28.75    | 2093900.0   | 0.0        | 1.0         | 3.3391119974129 | 3.3542105907925 | 3.3391119974129 | 3.3391119974129 | 16751200.0  |
+------------+--------+----------+----------+----------+-------------+------------+-------------+-----------------+-----------------+-----------------+-----------------+-------------+
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
### Retrieving data for multiple Quandl codes at the same time
To retrieve data for multiple codes, we need a different request structure.  In particular we need to say which Quandl codes we want data retrieved for, but also which columns are required for each.  This is done using the `QuandlCodeRequest`, which has two factory methods: `singleColumn(String quandlCode, int columnIndex)` and `allColumns(String quandlCode)`.  It's worth noting we're allowed to use the normal form of Quandl codes here, in the REST API, the forward slash gets replaced with a full-stop/period in this context:
```java
// Example3.java
QuandlSession session = QuandlSession.create();
TabularResult tabularResultMulti = session.getDataSets(
    MultiDataSetRequest.Builder
      .of(
        QuandlCodeRequest.singleColumn("WIKI/AAPL", CLOSE_COLUMN), 
        QuandlCodeRequest.allColumns("DOE/RWTC")
      )
      .withStartDate(RECENTISH_DATE)
      .withFrequency(Frequency.MONTHLY)
      .build());
System.out.println(tabularResultMulti.toPrettyPrintedString()); 
```
which returns all results in a single `TabularResult`
```
+------------+-------------------+------------------+
| Date       | WIKI.AAPL - Close | DOE.RWTC - Value |
+------------+-------------------+------------------+
| 2014-06-30 | 637.54            |                  |
| 2014-05-31 | 633.0             | 103.37           |
| 2014-04-30 | 590.09            | 100.07           |
| 2014-03-31 | 536.74            | 101.57           |
| 2014-02-28 | 526.24            | 102.88           |
| 2014-01-31 | 500.6             | 97.55            |
| 2013-12-31 | 561.02            | 98.17            |
| 2013-11-30 | 556.07            | 92.55            |
| 2013-10-31 | 522.7             | 96.29            |
| 2013-09-30 | 476.75            | 102.36           |
| 2013-08-31 | 487.22            | 107.98           |
| 2013-07-31 | 452.53            | 105.1            |
| 2013-06-30 | 396.53            | 96.36            |
| 2013-05-31 | 449.73            | 91.93            |
| 2013-04-30 | 442.78            | 93.22            |
| 2013-03-31 | 442.66            | 97.24            |
| 2013-02-28 | 441.4             | 92.03            |
| 2013-01-31 | 455.49            | 97.65            |
+------------+-------------------+------------------+
```
Note that the column labels actually do have a **SPACE-HYPHEN-SPACE** between the Quandl code and the Column name.  A future version of the library should allow these columns to be separated out automatically.
### Structure of a TabularResult
The type `TabularResult` is made up of a `HeaderDefinition`, which is essentially a list of column names (plus the facility to map from name to column index), plus a list of `Row` objects, each of which is linked to their common `HeaderDefinition`.  This allows individual `Row` objects to address their data using the column name rather than just the index as is the underlying API.  `Row` also contains methods to parse and cast data into various types (String, LocalDate, Double).  Here is an example
```java
// Example3a.java
QuandlSession session = QuandlSession.create();
TabularResult tabularResultMulti = session.getDataSets(
    MultiDataSetRequest.Builder
      .of(
        QuandlCodeRequest.singleColumn("WIKI/AAPL", CLOSE_COLUMN), 
        QuandlCodeRequest.allColumns("DOE/RWTC")
      )
      .withStartDate(RECENTISH_DATE)
      .withFrequency(Frequency.MONTHLY)
      .build());
System.out.println("Header definition: " + tabularResultMulti.getHeaderDefinition());
Iterator<Row> iter = tabularResultMulti.iterator();
while (iter.hasNext()) {
  Row row = iter.next();
  LocalDate date = row.getLocalDate("Date");
  Double value = row.getDouble("DOE.RWTC - Value");
  System.out.println("Value on date " + date + " was " + value);
} 
```
produces:
```
Header definition: HeaderDefinition[Date,WIKI.AAPL - Close,DOE.RWTC - Value]
Value on date 2014-06-30 was null
Value on date 2014-05-31 was 103.37
Value on date 2014-04-30 was 100.07
Value on date 2014-03-31 was 101.57
Value on date 2014-02-28 was 102.88
Value on date 2014-01-31 was 97.55
Value on date 2013-12-31 was 98.17
Value on date 2013-11-30 was 92.55
Value on date 2013-10-31 was 96.29
Value on date 2013-09-30 was 102.36
Value on date 2013-08-31 was 107.98
Value on date 2013-07-31 was 105.1
Value on date 2013-06-30 was 96.36
Value on date 2013-05-31 was 91.93
Value on date 2013-04-30 was 93.22
Value on date 2013-03-31 was 97.24
Value on date 2013-02-28 was 92.03
Value on date 2013-01-31 was 97.65
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
    "WIKI.AAPL - Open",
    "WIKI.AAPL - High",
    "WIKI.AAPL - Low",
    "WIKI.AAPL - Close",
    "WIKI.AAPL - Volume",
    "WIKI.AAPL - Ex-Dividend",
    "WIKI.AAPL - Split Ratio",
    "WIKI.AAPL - Adj. Open",
    "WIKI.AAPL - Adj. High",
    "WIKI.AAPL - Adj. Low",
    "WIKI.AAPL - Adj. Close",
    "WIKI.AAPL - Adj. Volume",
    "DOE.RWTC - Value",
    "WIKI.MSFT - Open",
    "WIKI.MSFT - High",
    "WIKI.MSFT - Low",
    "WIKI.MSFT - Close",
    "WIKI.MSFT - Volume",
    "WIKI.MSFT - Ex-Dividend",
    "WIKI.MSFT - Split Ratio",
    "WIKI.MSFT - Adj. Open",
    "WIKI.MSFT - Adj. High",
    "WIKI.MSFT - Adj. Low",
    "WIKI.MSFT - Adj. Close",
    "WIKI.MSFT - Adj. Volume"
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
WIKI.AAPL => Date, Open, High, Low, Close, Volume, Ex-Dividend, Split Ratio, Adj. Open, Adj. High, Adj. Low, Adj. Close, Adj. Volume
DOE.RWTC  => Date, Value
WIKI.MSFT => Date, Open, High, Low, Close, Volume, Ex-Dividend, Split Ratio, Adj. Open, Adj. High, Adj. Low, Adj. Close, Adj. Volume
```
### Searching
We can also make generalised free-text search requests to Quandl.  For this we use the `search(SearchRequest)` method.  This allows us to specify the maximum number of results per page, and also the page we want.  Note that queries with high page numbers are slow, presumably due to the server-side database having to project the entire result set of several million documents just to get the single page you want.  Try not to add to server load by making these requests excessively.
```java
// Example7.java
QuandlSession session = QuandlSession.create();
SearchResult searchResult = session.search(SearchRequest.Builder.of("Apple").withMaxPerPage(2).build());
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
SearchResult searchResult = session.search(SearchRequest.Builder.of("Apple").withMaxPerPage(2).build());
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

### Contrubutions
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
 


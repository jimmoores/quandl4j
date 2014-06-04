Quandl4J : A Quandl library for Java
====================================

[Quandl](http://quandl.com) is an source of millions of free data sets covering financial, economic, sociological and country data via an open REST API.  **Quandl4j** is a Java 7+ client-side wrapper for this API provided under the commercially friendly [Apache V2 license](http://www.apache.org/licenses/LICENSE-2.0.html).  It provides a type safe and fluent API in a modern style that takes care of constructing URLs and processing JSON and CSV responses but none-the-less allows access to all the functionality of the underlying REST API.  The core design principals are:
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

### A First Taste of the API
The following gets the complete data history (with Date, Open, High, Low, Volume, Ex-Dividend, Split Ratio, And Adjusted Open, High, Low Close and Volume columns) of AAPL (Apple Inc).  The symbol `WIKI/AAPL` is what is known as the **Quandl code** and is made up of a data source (in this case `WIKI`) and a data source specific code (in this case the exchange code for Apple Inc, which is `AAPL`).
```java
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
To retrieve data for multiple codes, we need a different request structure.  In particular we need to say which Quandl codes we want data retrieved for, but also which columns are required for each.  This is done using the `QuandlCodeRequest`, which has two factory methods: `singleColumn(String quandlCode, int columnIndex)` and `allColumns(String quandlCode)`.
```java
QuandlSession session = QuandlSession.create();
TabularResult tabularResultMulti = session.getDataSets(
    MultiDataSetRequest.Builder
      .of(
        QuandlCodeRequest.singleColumn("WIKI/AAPL", CLOSE_COLUMN), 
        QuandlCodeRequest.allColumns("DOE/RWTC")
      )
      .withStartDate(RECENTISH_DATE)
      .withFrequency(Frequency.WEEKLY)
      .build());
System.out.println(tabularResultMulti.toPrettyPrintedString()); 
```

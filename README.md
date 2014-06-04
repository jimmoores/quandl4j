Quandl4J : A Quandl library for Java
====================================

[Quandl](http://quandl.com) is a source of millions of free data sets covering financial, economic, sociological and country data via an open REST API.  **Quandl4j** is a Java 7+ client-side wrapper for this API provided under the commercially friendly [Apache V2 license](http://www.apache.org/licenses/LICENSE-2.0.html).  It provides a type safe and fluent API in a modern style that takes care of constructing URLs and processing JSON and CSV responses but none-the-less allows access to all the functionality of the underlying REST API.  The core design principals are:
> - Allow full access to the functionality of the underlying API.
> - Allow efficient network requests by using the more compact CSV encoding where
>   possible.
> - Use modern Java design principles like immutable objects, builders and 
>   factories.
> - Thorough unit and integration test support, including a framework that can
>   be reused by user applications without hitting the Quandl backend.
> - Publish maven artifacts on [Maven Central](http://search.maven.org/).
> - Provide concrete examples.
> - Provide comprehensive documentation and JavaDocs.

### A First Taste of the API
The following gets the complete data history (with Date, Open, High, Low, Volume columns) of AAPL (Apple Inc).
```
    QuandlSession session = QuandlSession.create();
    TabularResult tabularResult = session.getDataSet(
      DataSetRequest.Builder.of("WIKI/AAPL").build());
    System.out.println(tabularResult.toPrettyPrintedString());
```                        
from here it's possible to specify many refining options on your query.  In this case we
request AAPL again, but this time sampled Quarterly, returning only the close column (CLOSE_COLUMN here is actually the integer constant 4), and performing a normalization pre-process step on the server side before returning the results.
```
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

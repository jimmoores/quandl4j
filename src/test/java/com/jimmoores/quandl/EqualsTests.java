package com.jimmoores.quandl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

/**
 * Unit tests testing null checking.
 */
public class EqualsTests {
  //CHECKSTYLE:OFF

  @Test
  public final void testSimpleDataSetRequestEqualsAndHashCode() {
    DataSetRequest request1 = DataSetRequest.Builder.of("Hello").build();
    DataSetRequest request2 = DataSetRequest.Builder.of("Hello").build();
    DataSetRequest request3 = DataSetRequest.Builder.of("Goodbye").build();
    Assert.assertEquals(request1, request1);
    Assert.assertEquals(request1.hashCode(), request1.hashCode());
    Assert.assertEquals(request2, request1);
    Assert.assertEquals(request2.hashCode(), request1.hashCode());
    Assert.assertNotEquals(request3, request1);
    Assert.assertNotEquals(request3, request2);
    Assert.assertNotEquals(request1, null);
    Assert.assertNotEquals(request2, null);
    Assert.assertNotEquals(request3, null);
    Assert.assertEquals(request3.hashCode(), request3.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request2.hashCode());
    // we can't assert that 
  }

  @Test
  public final void testComplexDataSetRequestEqualsAndHashCode() {
    // Note that the tests on hashCode here are beyond the contract requirements of hashCode, we're saying they must differ if the object differs, which is 
    // generally good, but not required.
    DataSetRequest request1 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    
    DataSetRequest request2 = DataSetRequest.Builder
        .of("CODF/CODE") // note the F in there
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request2, request2);
    Assert.assertNotEquals(request1, request2);
    Assert.assertNotEquals(request1.hashCode(), request2.hashCode());
    
    DataSetRequest request3 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(0)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request3, request3);
    Assert.assertNotEquals(request1, request3);
    Assert.assertNotEquals(request2, request3);
    Assert.assertNotEquals(request1.hashCode(), request3.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request3.hashCode());
    
    DataSetRequest request4 = DataSetRequest.Builder
        .of("CODE/CODE") // no withColumn
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request4, request4);
    Assert.assertNotEquals(request1, request4);
    Assert.assertNotEquals(request2, request4);
    Assert.assertNotEquals(request3, request4);
    Assert.assertNotEquals(request1.hashCode(), request4.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request4.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request4.hashCode());
    
    DataSetRequest request5 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.WEEKLY) // chacnge freq
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request5, request5);
    Assert.assertNotEquals(request1, request5);
    Assert.assertNotEquals(request2, request5);
    Assert.assertNotEquals(request3, request5);
    Assert.assertNotEquals(request4, request5);
    Assert.assertNotEquals(request1.hashCode(), request5.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request5.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request5.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request5.hashCode());
    
    DataSetRequest request6 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3) // no frequency
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request6, request6);
    Assert.assertNotEquals(request1, request6);
    Assert.assertNotEquals(request2, request6);
    Assert.assertNotEquals(request3, request6);
    Assert.assertNotEquals(request4, request6);
    Assert.assertNotEquals(request5, request6);
    Assert.assertNotEquals(request1.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request6.hashCode());
    
    DataSetRequest request7 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(-49) // change freq
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request7, request7);
    Assert.assertNotEquals(request1, request7);
    Assert.assertNotEquals(request2, request7);
    Assert.assertNotEquals(request3, request7);
    Assert.assertNotEquals(request4, request7);
    Assert.assertNotEquals(request5, request7);
    Assert.assertNotEquals(request6, request7);
    Assert.assertNotEquals(request1.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request7.hashCode());
    
    DataSetRequest request8 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE) // no MaxRows
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request8, request8);
    Assert.assertNotEquals(request1, request8);
    Assert.assertNotEquals(request2, request8);
    Assert.assertNotEquals(request3, request8);
    Assert.assertNotEquals(request4, request8);
    Assert.assertNotEquals(request5, request8);
    Assert.assertNotEquals(request6, request8);
    Assert.assertNotEquals(request7, request8);
    Assert.assertNotEquals(request1.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request8.hashCode());
    
    DataSetRequest request9 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.DESCENDING) // change sort order
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request9, request9);
    Assert.assertNotEquals(request1, request9);
    Assert.assertNotEquals(request2, request9);
    Assert.assertNotEquals(request3, request9);
    Assert.assertNotEquals(request4, request9);
    Assert.assertNotEquals(request5, request9);
    Assert.assertNotEquals(request6, request9);
    Assert.assertNotEquals(request7, request9);
    Assert.assertNotEquals(request8, request9);
    Assert.assertNotEquals(request1.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request9.hashCode());
    
    DataSetRequest request10 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49) // remove sort order
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request10, request10);
    Assert.assertNotEquals(request1, request10);
    Assert.assertNotEquals(request2, request10);
    Assert.assertNotEquals(request3, request10);
    Assert.assertNotEquals(request4, request10);
    Assert.assertNotEquals(request5, request10);
    Assert.assertNotEquals(request6, request10);
    Assert.assertNotEquals(request7, request10);
    Assert.assertNotEquals(request8, request10);
    Assert.assertNotEquals(request9, request10);
    Assert.assertNotEquals(request1.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request10.hashCode());
    
    DataSetRequest request11 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 5, 1))  // change date
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request11, request11);
    Assert.assertNotEquals(request1, request11);
    Assert.assertNotEquals(request2, request11);
    Assert.assertNotEquals(request3, request11);
    Assert.assertNotEquals(request4, request11);
    Assert.assertNotEquals(request5, request11);
    Assert.assertNotEquals(request6, request11);
    Assert.assertNotEquals(request7, request11);
    Assert.assertNotEquals(request8, request11);
    Assert.assertNotEquals(request9, request11);
    Assert.assertNotEquals(request10, request11);
    Assert.assertNotEquals(request1.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request11.hashCode());
    
    DataSetRequest request12 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING) // remove start date
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request12, request12);
    Assert.assertNotEquals(request1, request12);
    Assert.assertNotEquals(request2, request12);
    Assert.assertNotEquals(request3, request12);
    Assert.assertNotEquals(request4, request12);
    Assert.assertNotEquals(request5, request12);
    Assert.assertNotEquals(request6, request12);
    Assert.assertNotEquals(request7, request12);
    Assert.assertNotEquals(request8, request12);
    Assert.assertNotEquals(request9, request12);
    Assert.assertNotEquals(request10, request12);
    Assert.assertNotEquals(request11, request12);
    Assert.assertNotEquals(request1.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request11.hashCode(), request12.hashCode());
    
    DataSetRequest request13 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2012, 1, 1)) // change date
        .build();
    Assert.assertEquals(request13, request13);
    Assert.assertNotEquals(request1, request13);
    Assert.assertNotEquals(request2, request13);
    Assert.assertNotEquals(request3, request13);
    Assert.assertNotEquals(request4, request13);
    Assert.assertNotEquals(request5, request13);
    Assert.assertNotEquals(request6, request13);
    Assert.assertNotEquals(request7, request13);
    Assert.assertNotEquals(request8, request13);
    Assert.assertNotEquals(request9, request13);
    Assert.assertNotEquals(request10, request13);
    Assert.assertNotEquals(request11, request13);
    Assert.assertNotEquals(request12, request13);
    Assert.assertNotEquals(request1.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request11.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request12.hashCode(), request13.hashCode());
    
    DataSetRequest request14 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1)) // remove date
        .build();
    Assert.assertEquals(request14, request14);
    Assert.assertNotEquals(request1, request14);
    Assert.assertNotEquals(request2, request14);
    Assert.assertNotEquals(request3, request14);
    Assert.assertNotEquals(request4, request14);
    Assert.assertNotEquals(request5, request14);
    Assert.assertNotEquals(request6, request14);
    Assert.assertNotEquals(request7, request14);
    Assert.assertNotEquals(request8, request14);
    Assert.assertNotEquals(request9, request14);
    Assert.assertNotEquals(request10, request14);
    Assert.assertNotEquals(request11, request14);
    Assert.assertNotEquals(request12, request14);
    Assert.assertNotEquals(request13, request14);
    Assert.assertNotEquals(request1.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request11.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request12.hashCode(), request14.hashCode());    
    Assert.assertNotEquals(request13.hashCode(), request14.hashCode());    
  }
  
  @Test
  public final void testComplexDataSetRequestEqualsAndHashCodeAgainstStringAndObject() {
    // Note that the tests on hashCode here are beyond the contract requirements of hashCode, we're saying they must differ if the object differs, which is 
    // generally good, but not required.
    DataSetRequest request1 = DataSetRequest.Builder
        .of("CODE/CODE")
        .withColumn(3)
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertNotEquals(request1, "Hello");
    Assert.assertNotEquals(request1, new Object());
  }

  @Test
  public void testMultiDataSetRequestEqualsAndHashCode() {
    // Note that the tests on hashCode here are beyond the contract requirements of hashCode, we're saying they must differ if the object differs, which is 
    // generally good, but not required.
    MultiDataSetRequest request1 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    
    MultiDataSetRequest request2 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODF")) // note the F in there
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request2, request2);
    Assert.assertNotEquals(request1, request2);
    Assert.assertNotEquals(request1.hashCode(), request2.hashCode());
    
    MultiDataSetRequest request3 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.singleColumn("CODE/CODE", 0))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request3, request3);
    Assert.assertNotEquals(request1, request3);
    Assert.assertNotEquals(request2, request3);
    Assert.assertNotEquals(request1.hashCode(), request3.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request3.hashCode());
    
    MultiDataSetRequest request4 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"), QuandlCodeRequest.singleColumn("CODE/CODE", 0))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request4, request4);
    Assert.assertNotEquals(request1, request4);
    Assert.assertNotEquals(request2, request4);
    Assert.assertNotEquals(request3, request4);
    Assert.assertNotEquals(request1.hashCode(), request4.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request4.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request4.hashCode());
    
    MultiDataSetRequest request5 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.WEEKLY) // chacnge freq
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request5, request5);
    Assert.assertNotEquals(request1, request5);
    Assert.assertNotEquals(request2, request5);
    Assert.assertNotEquals(request3, request5);
    Assert.assertNotEquals(request4, request5);
    Assert.assertNotEquals(request1.hashCode(), request5.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request5.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request5.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request5.hashCode());
    
    MultiDataSetRequest request6 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request6, request6);
    Assert.assertNotEquals(request1, request6);
    Assert.assertNotEquals(request2, request6);
    Assert.assertNotEquals(request3, request6);
    Assert.assertNotEquals(request4, request6);
    Assert.assertNotEquals(request5, request6);
    Assert.assertNotEquals(request1.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request6.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request6.hashCode());
    
    MultiDataSetRequest request7 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(-49) // change freq
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request7, request7);
    Assert.assertNotEquals(request1, request7);
    Assert.assertNotEquals(request2, request7);
    Assert.assertNotEquals(request3, request7);
    Assert.assertNotEquals(request4, request7);
    Assert.assertNotEquals(request5, request7);
    Assert.assertNotEquals(request6, request7);
    Assert.assertNotEquals(request1.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request7.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request7.hashCode());
    
    MultiDataSetRequest request8 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE) // no MaxRows
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request8, request8);
    Assert.assertNotEquals(request1, request8);
    Assert.assertNotEquals(request2, request8);
    Assert.assertNotEquals(request3, request8);
    Assert.assertNotEquals(request4, request8);
    Assert.assertNotEquals(request5, request8);
    Assert.assertNotEquals(request6, request8);
    Assert.assertNotEquals(request7, request8);
    Assert.assertNotEquals(request1.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request8.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request8.hashCode());
    
    MultiDataSetRequest request9 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.DESCENDING) // change sort order
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request9, request9);
    Assert.assertNotEquals(request1, request9);
    Assert.assertNotEquals(request2, request9);
    Assert.assertNotEquals(request3, request9);
    Assert.assertNotEquals(request4, request9);
    Assert.assertNotEquals(request5, request9);
    Assert.assertNotEquals(request6, request9);
    Assert.assertNotEquals(request7, request9);
    Assert.assertNotEquals(request8, request9);
    Assert.assertNotEquals(request1.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request9.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request9.hashCode());
    
    MultiDataSetRequest request10 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49) // remove sort order
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request10, request10);
    Assert.assertNotEquals(request1, request10);
    Assert.assertNotEquals(request2, request10);
    Assert.assertNotEquals(request3, request10);
    Assert.assertNotEquals(request4, request10);
    Assert.assertNotEquals(request5, request10);
    Assert.assertNotEquals(request6, request10);
    Assert.assertNotEquals(request7, request10);
    Assert.assertNotEquals(request8, request10);
    Assert.assertNotEquals(request9, request10);
    Assert.assertNotEquals(request1.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request10.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request10.hashCode());
    
    MultiDataSetRequest request11 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 5, 1))  // change date
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request11, request11);
    Assert.assertNotEquals(request1, request11);
    Assert.assertNotEquals(request2, request11);
    Assert.assertNotEquals(request3, request11);
    Assert.assertNotEquals(request4, request11);
    Assert.assertNotEquals(request5, request11);
    Assert.assertNotEquals(request6, request11);
    Assert.assertNotEquals(request7, request11);
    Assert.assertNotEquals(request8, request11);
    Assert.assertNotEquals(request9, request11);
    Assert.assertNotEquals(request10, request11);
    Assert.assertNotEquals(request1.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request11.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request11.hashCode());
    
    MultiDataSetRequest request12 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING) // remove start date
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertEquals(request12, request12);
    Assert.assertNotEquals(request1, request12);
    Assert.assertNotEquals(request2, request12);
    Assert.assertNotEquals(request3, request12);
    Assert.assertNotEquals(request4, request12);
    Assert.assertNotEquals(request5, request12);
    Assert.assertNotEquals(request6, request12);
    Assert.assertNotEquals(request7, request12);
    Assert.assertNotEquals(request8, request12);
    Assert.assertNotEquals(request9, request12);
    Assert.assertNotEquals(request10, request12);
    Assert.assertNotEquals(request11, request12);
    Assert.assertNotEquals(request1.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request12.hashCode());
    Assert.assertNotEquals(request11.hashCode(), request12.hashCode());
    
    MultiDataSetRequest request13 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2012, 1, 1)) // change date
        .build();
    Assert.assertEquals(request13, request13);
    Assert.assertNotEquals(request1, request13);
    Assert.assertNotEquals(request2, request13);
    Assert.assertNotEquals(request3, request13);
    Assert.assertNotEquals(request4, request13);
    Assert.assertNotEquals(request5, request13);
    Assert.assertNotEquals(request6, request13);
    Assert.assertNotEquals(request7, request13);
    Assert.assertNotEquals(request8, request13);
    Assert.assertNotEquals(request9, request13);
    Assert.assertNotEquals(request10, request13);
    Assert.assertNotEquals(request11, request13);
    Assert.assertNotEquals(request12, request13);
    Assert.assertNotEquals(request1.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request11.hashCode(), request13.hashCode());
    Assert.assertNotEquals(request12.hashCode(), request13.hashCode());
    
    MultiDataSetRequest request14 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1)) // remove date
        .build();
    Assert.assertEquals(request14, request14);
    Assert.assertNotEquals(request1, request14);
    Assert.assertNotEquals(request2, request14);
    Assert.assertNotEquals(request3, request14);
    Assert.assertNotEquals(request4, request14);
    Assert.assertNotEquals(request5, request14);
    Assert.assertNotEquals(request6, request14);
    Assert.assertNotEquals(request7, request14);
    Assert.assertNotEquals(request8, request14);
    Assert.assertNotEquals(request9, request14);
    Assert.assertNotEquals(request10, request14);
    Assert.assertNotEquals(request11, request14);
    Assert.assertNotEquals(request12, request14);
    Assert.assertNotEquals(request13, request14);
    Assert.assertNotEquals(request1.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request2.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request3.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request4.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request5.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request6.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request7.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request8.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request9.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request10.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request11.hashCode(), request14.hashCode());
    Assert.assertNotEquals(request12.hashCode(), request14.hashCode());    
    Assert.assertNotEquals(request13.hashCode(), request14.hashCode());   
  }
  
  @Test
  public void testComplexMultiDataSetRequestEqualsAndHashCodeAgainstStringAndObject() {
    // Note that the tests on hashCode here are beyond the contract requirements of hashCode, we're saying they must differ if the object differs, which is 
    // generally good, but not required.
    MultiDataSetRequest request1 = MultiDataSetRequest.Builder
        .of(QuandlCodeRequest.allColumns("CODE/CODE"))
        .withFrequency(Frequency.NONE)
        .withMaxRows(49)
        .withSortOrder(SortOrder.ASCENDING)
        .withStartDate(LocalDate.of(2010, 1, 1))
        .withEndDate(LocalDate.of(2011, 1, 1))
        .build();
    Assert.assertNotEquals(request1, "Hello");
    Assert.assertNotEquals(request1, new Object());
  }
  
  private JSONObject getTestObj1() {
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.put("private", false);
      jsonObj.put("from_date", "2009-09-01");
      JSONArray jsonArray = new JSONArray();
      jsonArray.put("Date");
      jsonArray.put("Open");
      jsonArray.put("Close");
      jsonObj.put("columns", jsonArray);
      return jsonObj;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }
  
  private JSONObject getTestObj2() {
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.put("private", false);
      jsonObj.put("from_date", "2009-09-01");
      JSONArray jsonArray = new JSONArray();
      jsonArray.put("Date");
      jsonArray.put("Open");
      jsonArray.put("Close");
      jsonObj.put("columns", jsonArray);
      JSONArray data = new JSONArray();
      JSONArray jsonDataArray1 = new JSONArray();
      jsonDataArray1.put("2010-01-01");
      jsonDataArray1.put(39.0d);
      jsonDataArray1.put(39.2d);
      jsonDataArray1.put(39.5d);
      JSONArray jsonDataArray2 = new JSONArray();
      jsonDataArray2.put("2010-01-02");
      jsonDataArray2.put(41.0d);
      jsonDataArray2.put(41.2d);
      jsonDataArray2.put(41.5d);
      JSONArray jsonDataArray3 = new JSONArray();
      jsonDataArray3.put("2010-01-03");
      jsonDataArray3.put(42.0d);
      jsonDataArray3.put(42.2d);
      jsonDataArray3.put(42.5d);
      data.put(jsonDataArray1);
      data.put(jsonDataArray2);
      data.put(jsonDataArray3);
      jsonObj.put("data", data);
      return jsonObj;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }
  
  @Test
  public void testMetaDataResult() {
    MetaDataResult metaDataResult1 = MetaDataResult.of(getTestObj1());
    MetaDataResult metaDataResult2 = MetaDataResult.of(getTestObj2());
    MetaDataResult metaDataResult3 = MetaDataResult.of(getTestObj1());
    Assert.assertEquals(metaDataResult1, metaDataResult1);
    Assert.assertEquals(metaDataResult1, metaDataResult3);
    Assert.assertEquals(metaDataResult3, metaDataResult1);
    Assert.assertNotEquals(metaDataResult1, metaDataResult2);
    Assert.assertNotEquals(metaDataResult2, metaDataResult1);
    // not strictly within the hashCode requirements, but we'd like this to be the case.
    Assert.assertNotEquals(metaDataResult1.hashCode(), metaDataResult2.hashCode());
    Assert.assertEquals(metaDataResult1.hashCode(), metaDataResult1.hashCode());
    Assert.assertEquals(metaDataResult1.hashCode(), metaDataResult3.hashCode());
  }
  
  @Test
  public void testSearchResult() {
    SearchResult searchResult1 = SearchResult.of(getTestObj1());
    SearchResult searchResult2 = SearchResult.of(getTestObj2());
    SearchResult searchResult3 = SearchResult.of(getTestObj1());
    Assert.assertEquals(searchResult1, searchResult1);
    Assert.assertEquals(searchResult1, searchResult3);
    Assert.assertEquals(searchResult3, searchResult1);
    Assert.assertNotEquals(searchResult1, searchResult2);
    Assert.assertNotEquals(searchResult2, searchResult1);
    // not strictly within the hashCode requirements, but we'd like this to be the case.
    Assert.assertNotEquals(searchResult1.hashCode(), searchResult2.hashCode());
    Assert.assertEquals(searchResult1.hashCode(), searchResult1.hashCode());
    Assert.assertEquals(searchResult1.hashCode(), searchResult3.hashCode());
  }

  // CHECKSTYLE:ON

}

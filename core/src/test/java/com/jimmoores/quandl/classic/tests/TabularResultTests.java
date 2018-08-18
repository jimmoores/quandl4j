package com.jimmoores.quandl.classic.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.LocalDate;

import com.jimmoores.quandl.HeaderDefinition;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;

/**
 * Tests for TabularResult.
 */
public class TabularResultTests {
  // CHECKSTYLE:OFF
  @Test
  public void runAccessorTests() {
    HeaderDefinition headerDefinition1 = HeaderDefinition.of("A", "B", "C", "D");
    HeaderDefinition headerDefinition2 = HeaderDefinition.of("A", "B", "C", "D");
    Row row1 = Row.of(headerDefinition1, new String[] {"Jim", "Miranda", "1.2", "2009-09-13" });
    Row row3 = Row.of(headerDefinition2, new String[] {"Jim", "Miranda", "Elaine", "Kostas" });
    TabularResult tr1 = TabularResult.of(headerDefinition1, Arrays.asList(row1, row3));
    Assert.assertEquals(tr1.get(0).getString(0), "Jim");
    Assert.assertEquals(tr1.get(0).getString(1), "Miranda");
    Assert.assertEquals(tr1.get(0).getString(2), "1.2");
    Assert.assertEquals(tr1.get(0).getDouble(2), 1.2d, 1e-14);
    Assert.assertEquals(tr1.get(0).getString(3), "2009-09-13");
    Assert.assertEquals(tr1.get(0).getLocalDate(3), LocalDate.of(2009, 9, 13));
    Assert.assertEquals(tr1.get(0).getString("A"), "Jim");
    Assert.assertEquals(tr1.get(0).getString("B"), "Miranda");
    Assert.assertEquals(tr1.get(0).getString("C"), "1.2");
    Assert.assertEquals(tr1.get(0).getDouble("C"), 1.2d, 1e-14);
    Assert.assertEquals(tr1.get(0).getString("D"), "2009-09-13");
    Assert.assertEquals(tr1.get(0).getLocalDate("D"), LocalDate.of(2009, 9, 13));
  }
  
  @Test
  public void testIterator() {
    HeaderDefinition headerDefinition1 = HeaderDefinition.of("A", "B", "C", "D");
    HeaderDefinition headerDefinition2 = HeaderDefinition.of("A", "B", "C", "D");
    HeaderDefinition headerDefinition3 = HeaderDefinition.of("B", "A", "C", "D");
    Row row1 = Row.of(headerDefinition1, new String[] {"Jim", "Miranda", "1.2", "2009-09-13" });
    Row row2 = Row.of(headerDefinition1, new String[] {"Jim", "Miranda", "1.2", "2009-09-13" });
    Row row3 = Row.of(headerDefinition2, new String[] {"Jim", "Miranda", "Elaine", "Kostas" });
    Row row4 = Row.of(headerDefinition3, new String[] {"Jim", "Miranda", "Elaine", "Kostas" });
    TabularResult tr1 = TabularResult.of(headerDefinition1, Arrays.asList(row1, row3));
    TabularResult tr2 = TabularResult.of(headerDefinition2, Arrays.asList(row2, row4));
    TabularResult tr3 = TabularResult.of(headerDefinition2, Arrays.asList(row2, row1));
    Iterator<Row> iter1 = tr1.iterator();
    Iterator<Row> iter2 = tr2.iterator();
    Iterator<Row> iter3 = tr3.iterator();
    Assert.assertTrue(iter1.hasNext()); 
    Assert.assertTrue(iter2.hasNext());
    Assert.assertTrue(iter3.hasNext());
    Row next1 = iter1.next();
    Row next2 = iter2.next();
    Row next3 = iter3.next();
    Assert.assertEquals(next2, next1);
    Assert.assertEquals(next2, next3);
    Assert.assertEquals(next1, next3);
    Assert.assertTrue(iter1.hasNext()); 
    Assert.assertTrue(iter2.hasNext());
    Assert.assertTrue(iter3.hasNext());
    Row nextnext1 = iter1.next();
    Row nextnext2 = iter2.next();
    Row nextnext3 = iter3.next();
    Assert.assertNotEquals(nextnext2, nextnext1);
    Assert.assertNotEquals(nextnext2, nextnext3);
    Assert.assertFalse(iter1.hasNext()); 
    Assert.assertFalse(iter2.hasNext());
    Assert.assertFalse(iter3.hasNext());
  }
  
  @Test
  public void testSize() {
    HeaderDefinition headerDefinition1 = HeaderDefinition.of("A", "B", "C", "D");
    HeaderDefinition headerDefinition2 = HeaderDefinition.of("A", "B", "C", "D");
    HeaderDefinition headerDefinition3 = HeaderDefinition.of("B", "A", "C", "D");
    Row row1 = Row.of(headerDefinition1, new String[] {"Jim", "Miranda", "1.2", "2009-09-13" });
    Row row2 = Row.of(headerDefinition1, new String[] {"Jim", "Miranda", "1.2", "2009-09-13" });
    Row row3 = Row.of(headerDefinition2, new String[] {"Jim", "Miranda", "Elaine", "Kostas" });
    Row row4 = Row.of(headerDefinition3, new String[] {"Jim", "Miranda", "Elaine", "Kostas" });
    TabularResult tr1 = TabularResult.of(headerDefinition1, Arrays.asList(row1, row2, row3, row4));
    TabularResult tr2 = TabularResult.of(headerDefinition2, Arrays.asList(row2, row4));
    TabularResult tr3 = TabularResult.of(headerDefinition3, Collections.<Row>emptyList());
    Assert.assertEquals(tr1.size(), 4);
    Assert.assertFalse(tr1.isEmpty());
    Assert.assertEquals(tr2.size(), 2);
    Assert.assertFalse(tr2.isEmpty());
    Assert.assertEquals(tr3.size(), 0);
    Assert.assertTrue(tr3.isEmpty());
    
  }
  // CHECKSTYLE:ON
}

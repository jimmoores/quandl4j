package com.jimmoores.quandl.classic.tests;

import org.testng.annotations.Test;

import com.jimmoores.quandl.classic.example.Demo;
import com.jimmoores.quandl.classic.example.Example1;
import com.jimmoores.quandl.classic.example.Example2;
import com.jimmoores.quandl.classic.example.Example3;
import com.jimmoores.quandl.classic.example.Example4;
import com.jimmoores.quandl.classic.example.Example5;


// CHECKSTYLE:OFF
/**
 * @deprecated this test covers the deprecated API.
 */
@Test
public class ExampleTests {
  @Test
  public void example1Test() {
    Example1.main(new String[] {});
  }
  
  @Test
  public void example2Test() {
    Example2.main(new String[] {});
  }
  @Test
  public void example3Test() {
    Example3.main(new String[] {});
  }
  @Test
  public void example4Test() {
    Example4.main(new String[] {});
  }
  @Test
  public void example5Test() {
    Example5.main(new String[] {});
  }
  @Test
  public void demoTest() {
    Demo.main(new String[] {});
  }
}

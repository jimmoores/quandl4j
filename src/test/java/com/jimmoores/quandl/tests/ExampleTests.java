package com.jimmoores.quandl.tests;

import org.testng.annotations.Test;

import com.jimmoores.quandl.example.Demo;
import com.jimmoores.quandl.example.Example1;
import com.jimmoores.quandl.example.Example2;
import com.jimmoores.quandl.example.Example3;
import com.jimmoores.quandl.example.Example3a;
import com.jimmoores.quandl.example.Example4;
import com.jimmoores.quandl.example.Example5;
import com.jimmoores.quandl.example.Example6;
import com.jimmoores.quandl.example.Example7;
import com.jimmoores.quandl.example.Example8;

// CHECKSTYLE:OFF
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
  public void example3aTest() {
    Example3a.main(new String[] {});
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
  public void example6Test() {
    Example6.main(new String[] {});
  }
  @Test
  public void example7Test() {
    Example7.main(new String[] {});
  }
  @Test
  public void example8Test() {
    Example8.main(new String[] {});
  }
  @Test
  public void demoTest() {
    Demo.main(new String[] {});
  }
}

package com.jimmoores.quandl.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for checking arguments.
 */
public final class ArgumentChecker {
  private static Logger s_logger = LoggerFactory.getLogger(ArgumentChecker.class);
  
  private ArgumentChecker() {
  }
  
  /**
   * Throws an exception if the argument is not null.
   * @param argument the object to check
   * @param name the name of the parameter
   */
  public static void notNull(final Object argument, final String name) {
    if (argument == null) {
      s_logger.error("Argument {} was null", name);
      throw new QuandlRuntimeException("Value " + name + " was null");
    }
  }
  
  /**
   * Throws an exception if the array argument is not null or empty.
   * @param <E> type of array
   * @param argument the object to check
   * @param name the name of the parameter
   */
  public static <E> void notNullOrEmpty(final E[] argument, final String name) {
    if (argument == null) {
      s_logger.error("Argument {} was null", name);
      throw new QuandlRuntimeException("Value " + name + " was null");
    } else if (argument.length == 0) {
      s_logger.error("Argument {} was empty array", name);
      throw new QuandlRuntimeException("Value " + name + " was empty array");
    }
  }
  
  /**
   * Throws an exception if the collection argument is not null or empty.
   * @param <E> type of array
   * @param argument the object to check
   * @param name the name of the parameter
   */
  public static <E> void notNullOrEmpty(final Collection<E> argument, final String name) {
    if (argument == null) {
      s_logger.error("Argument {} was null", name);
      throw new QuandlRuntimeException("Value " + name + " was null");
    } else if (argument.size() == 0) {
      s_logger.error("Argument {} was empty collection", name);
      throw new QuandlRuntimeException("Value " + name + " was empty collection");
    }
  }
  
  /**
   * Throws an exception if the string argument is not null or empty.
   * @param argument the String to check
   * @param name the name of the parameter
   */
  public static void notNullOrEmpty(final String argument, final String name) {
    if (argument == null) {
      s_logger.error("Argument {} was null", name);
      throw new QuandlRuntimeException("Value " + name + " was null");
    } else if (argument.length() == 0) {
      s_logger.error("Argument {} was empty string", name);
      throw new QuandlRuntimeException("Value " + name + " was empty string");
    }
  }
}

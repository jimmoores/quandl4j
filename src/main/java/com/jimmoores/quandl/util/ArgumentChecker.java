package com.jimmoores.quandl.util;

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
      throw new QuandlRuntimeException("Value " + name + " was not null");
    }
  }
}

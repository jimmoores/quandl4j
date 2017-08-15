package com.jimmoores.quandl.v2.util;

import java.io.InputStream;

public interface ResponseProcessor<T> {
  T process(InputStream inputStream);
}

package com.jimmoores.quandl.processing;

import java.io.InputStream;

public interface ResponseProcessor<T> {
  T process(InputStream inputStream);
}

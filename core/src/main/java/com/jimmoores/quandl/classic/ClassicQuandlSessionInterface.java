package com.jimmoores.quandl.classic;

import com.jimmoores.quandl.MetaDataResult;
import com.jimmoores.quandl.SearchResult;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.generic.GenericQuandlSessionInterface;

/**
 * Classic specialised interface for a GenericQuandlSession.
 */
public interface ClassicQuandlSessionInterface extends GenericQuandlSessionInterface<MetaDataResult, TabularResult, SearchResult> {
}

package com.company.sample.exchange.connector;

import com.company.sample.exchange.domain.ExchangeRate;

import java.util.List;

/**
 * Înterface to declare connectors
 * able to retrieve lists of CurrExRateResource
 * typically from an external source.
 */
public interface ICurrExServiceConnector {

    List<ExchangeRate> fetchCurrExRateResources() throws Exception;
}

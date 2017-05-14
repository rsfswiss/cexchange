package com.company.sample.exchange.connector;

import com.company.sample.exchange.service.CurrExRateResource;

import java.util.List;

/**
 * Înterface to declare connectors
 * able to retrieve lists of CurrExRateResource
 * typically from an external source.
 */
public interface ICurrExServiceConnector {

    List<CurrExRateResource> fetchCurrExRateResources() throws Exception;
}

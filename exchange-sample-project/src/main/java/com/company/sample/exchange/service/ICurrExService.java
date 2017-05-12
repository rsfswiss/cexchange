package com.company.sample.exchange.service;

import java.util.Date;

/**
 * Service definition for exchange rate data.
 * Provides method definitions to obtain and persist
 * the exchange rate information,
 * typically from an external source, and to obtain a
 * exchange rate value based on EUR given a currency code
 * and a date.
 */
public interface ICurrExService {

    void fetchAndStoreExchangeRateInformation() throws Exception;

    String getExchangeRateForEuroAtDate(String currencyCode, Date chgRateDate);
}

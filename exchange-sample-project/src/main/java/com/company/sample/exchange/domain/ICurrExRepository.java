package com.company.sample.exchange.domain;

import com.company.sample.exchange.service.CurrExRateResource;

import java.util.List;

/**
 * Repository definition, allows
 * a total reset of the contents,
 * adding and finding resources.
 *
 * A currency and date combination uniquely identifies
 * a resource.
 *
 */
public interface ICurrExRepository {

    /**
     * Replaces the contents of the repository with
     * the ones in the passed container
     *
     * @param resources the new contents of the repository
     */
    void updateRepository(List<CurrExRateResource> resources);

    /**
     * Will return the exchange rate value in string format
     * for the input parameters.
     * A currencyCode and a date uniquely identify
     * an exchange rate value.
     *
     * @param currencyCode three chars e.g. 'USD'
     * @param dateStr in format YYYYMMDD
     * @return the exchange rate value or null if not found
     */
    String findByCurrencyCodeAndDate(String currencyCode, String dateStr);

    String getMaxAvailableDateStr();

    String getMinAvailableDateStr();

    List<CurrExRateResource> getAllExchangeRatesBasedOnEuroForCurrency(String currencyCode);

    List<String> getAllCurrencyCodes();
}

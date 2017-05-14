package com.company.sample.exchange.domain;

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

    void deleteAll();

    void addOverwriting(String exchangeRate, String currencyCode, String dateStr);

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

}

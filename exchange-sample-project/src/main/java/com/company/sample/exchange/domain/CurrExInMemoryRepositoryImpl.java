package com.company.sample.exchange.domain;

import org.springframework.stereotype.Repository;

/**
 * Wraps a Hasmap as a very simple <key,value> pair
 * in memory repository implementation for the currex sample project.
 *
 * Can be moved to a persisted solution by providing a different implementation.
 *
 * Does not perform validation.
 *
 * Currency codes are stored in uppercase.
 */
@Repository
public class CurrExInMemoryRepositoryImpl implements ICurrExRepository {


    @Override
    public void deleteAll() {
        //WIP
    }

    @Override
    public void addOverwriting(String exchangeRate, String currencyCode, String dateStr) {
        //WIP
    }

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
    @Override
    public String findByCurrencyCodeAndDate(String currencyCode, String dateStr) {
        //WIP
        return null;
    }

    @Override
    public String getMaxAvailableDateStr() {
        //WIP
        return "";
    }

    @Override
    public String getMinAvailableDateStr() {
        //WIP
        return "";
    }


}

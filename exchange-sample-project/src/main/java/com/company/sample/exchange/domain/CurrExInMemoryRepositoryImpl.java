package com.company.sample.exchange.domain;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

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

    //key is currencyCode#Date, e.g. USD#20170515
    //value is the exchange rate, e.g. 125.6
    private HashMap<String,String> inMemoryContainerMap = new HashMap<>();

    //conveniently keeping track of the oldest inserted date
    private String minAvailableDateStr = "00010101";

    //conveniently keeping track of the newest inserted date
    private String maxAvailableDateStr = "99991212";

    @Override
    public synchronized void deleteAll() {
        inMemoryContainerMap.clear();
        resetMaxAndMinAvailableDateStr();
    }

    @Override
    public synchronized void addOverwriting(String exchangeRate, String currencyCode, String dateStr) {
        updateMaxAndMinAvailableDateStr(dateStr);
        inMemoryContainerMap.put(generateKey(currencyCode, dateStr),exchangeRate);
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
        return inMemoryContainerMap.get(generateKey(currencyCode, dateStr));
    }

    @Override
    public String getMaxAvailableDateStr() {
        return maxAvailableDateStr;
    }

    @Override
    public String getMinAvailableDateStr() {
        return minAvailableDateStr;
    }

    private String generateKey(String currencyCode, String dateStr) {
        return currencyCode.toUpperCase()+"#"+dateStr;
    }

    private void resetMaxAndMinAvailableDateStr() {
        minAvailableDateStr = "99991212";
        maxAvailableDateStr = "00010101";
    }

    private void updateMaxAndMinAvailableDateStr(String dateStr) {
        if(inMemoryContainerMap.isEmpty() || dateStr.compareTo(getMaxAvailableDateStr()) > 0)
            maxAvailableDateStr = dateStr;

        if(inMemoryContainerMap.isEmpty() || dateStr.compareTo(getMinAvailableDateStr()) < 0)
            minAvailableDateStr = dateStr;
    }



}

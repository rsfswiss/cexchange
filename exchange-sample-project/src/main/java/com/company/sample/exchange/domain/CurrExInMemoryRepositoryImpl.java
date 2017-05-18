package com.company.sample.exchange.domain;

import com.company.sample.exchange.service.CurrExRateResource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Wraps a Hasmap as a very simple <key,value> pair
 * in memory repository implementation for the currex sample project.
 *
 * Can be moved to a persisted solution by providing a different implementation.
 *
 * Does not perform validation.
 *
 * Move to ConcurrentHashmap if post operations are needed in the future
 *
 * Currency codes are stored in uppercase.
 */
@Repository
public class CurrExInMemoryRepositoryImpl implements ICurrExRepository {

    //key is currencyCode#Date, e.g. USD#20170515
    //value is the exchange rate, e.g. 125.6
    private static HashMap<String,String> inMemoryContainerMap = new HashMap<>();

    //conveniently keeps track of all different currency codes
    private ArrayList<String> allCurrencyCodes = new ArrayList<>();

    //needed to ninitialize the status of the max and min dates
    private final static String MIN_DATE = "00010101";
    private final static String MAX_DATE = "99991212";

    //conveniently keeping track of the oldest inserted date
    private String minAvailableDateStr = MAX_DATE;

    //conveniently keeping track of the newest inserted date
    private String maxAvailableDateStr = MIN_DATE;

    @Override
    public synchronized void deleteAll() {
        //not using clear operation since we use iterators. ConcurrentHashmap seems a bit overkill for this use case
        inMemoryContainerMap = new HashMap<>();
        resetMaxAndMinAvailableDateStr();
        allCurrencyCodes.clear();
    }

    @Override
    public synchronized void addOverwriting(String exchangeRate, String currencyCode, String dateStr) {
        updateMaxAndMinAvailableDateStr(dateStr);
        inMemoryContainerMap.put(generateKey(currencyCode, dateStr),exchangeRate);
        if(!allCurrencyCodes.contains(currencyCode.toUpperCase())) allCurrencyCodes.add(currencyCode.toUpperCase());
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

    @Override
    public List<CurrExRateResource> getAllExchangeRatesBasedOnEuroForCurrency(String currencyCode) {
        //an iterator is used, but the only update operation always acts on a new instance
        List<CurrExRateResource> resources = new ArrayList<>();
        for(String key : inMemoryContainerMap.keySet()) {
            //currency code is the first part of the key
            if(key.startsWith(currencyCode.toUpperCase())) {
                //the value is the exchg rate and the date is the second part of the key
                String exchgDate = key.replace(currencyCode.toUpperCase() + "#","");
                resources.add(new CurrExRateResource(inMemoryContainerMap.get(key),
                        currencyCode.toUpperCase(),
                        exchgDate));
            }
        }
        return resources;
    }

    @Override
    public List<String> getAllCurrencyCodes() {
        return allCurrencyCodes;
    }

    private String generateKey(String currencyCode, String dateStr) {
        return currencyCode.toUpperCase()+"#"+dateStr;
    }

    private void resetMaxAndMinAvailableDateStr() {
        minAvailableDateStr = MAX_DATE;
        maxAvailableDateStr = MIN_DATE;
    }

    private void updateMaxAndMinAvailableDateStr(String dateStr) {
        if(inMemoryContainerMap.isEmpty() || dateStr.compareTo(getMaxAvailableDateStr()) > 0)
            maxAvailableDateStr = dateStr;

        if(inMemoryContainerMap.isEmpty() || dateStr.compareTo(getMinAvailableDateStr()) < 0)
            minAvailableDateStr = dateStr;
    }



}

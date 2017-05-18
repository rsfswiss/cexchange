package com.company.sample.exchange.domain;

import com.company.sample.exchange.service.CurrExRateResource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final HashMap<String,String> inMemoryContainerMap = new HashMap<>();

    //conveniently keeps track of all different currency codes
    private static final ArrayList<String> allCurrencyCodes = new ArrayList<>();

    //needed to initialize the status of the max and min dates
    private final static String MIN_DATE = "00010101";
    private final static String MAX_DATE = "99991212";

    //conveniently keeping track of the oldest inserted date
    private String minAvailableDateStr = MAX_DATE;

    //conveniently keeping track of the newest inserted date
    private String maxAvailableDateStr = MIN_DATE;

    /**
     * Replaces the contents of the hashmap with the new exchange rates.
     * Calculates new values for the max and min date values available.
     * Updates the list of available currencies.
     *
     * This operation needs to be synchronized, it is only meant to be executed
     * by one single thread at a time (typically once per day)
     *
     * The internal inMemoryContainerMap and allCurrencyCodes also need
     * to be protected against readers during the update operation.
     *
     * @param resources the new contents of the repository
     */
    @Override
    public synchronized void updateRepository(List<CurrExRateResource> resources) {
        synchronized (inMemoryContainerMap) { //to avoid ConcurrentModification exception while iterating in getAllExchangeRatesBasedOnEuroForCurrency
            inMemoryContainerMap.clear();
            for(CurrExRateResource resource : resources){
                inMemoryContainerMap.put(generateKey(resource.getCurrencyCode(),
                                         resource.getExchangeRateDate()), resource.getExchangeRate());
            }
        }
        synchronized (allCurrencyCodes) { //to protect readers of the ArrayList at getAllCurrencyCodes
            allCurrencyCodes.clear();
            resetMaxAndMinAvailableDateStr();
            for(CurrExRateResource resource : resources){
                updateMaxAndMinAvailableDateStr(resource.getExchangeRateDate());
                synchronized (allCurrencyCodes) {
                    if (!allCurrencyCodes.contains(resource.getCurrencyCode().toUpperCase()))
                        allCurrencyCodes.add(resource.getCurrencyCode().toUpperCase());
                }
            }
        }
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
        synchronized(inMemoryContainerMap) { //to avoid ConcurrentModification if the request is during the daily update operation
            for (String key : inMemoryContainerMap.keySet()) {
                //currency code is the first part of the key
                if (key.startsWith(currencyCode.toUpperCase())) {
                    //the value is the exchg rate and the date is the second part of the key
                    String exchgDate = key.replace(currencyCode.toUpperCase() + "#", "");
                    resources.add(new CurrExRateResource(inMemoryContainerMap.get(key),
                            currencyCode.toUpperCase(),
                            exchgDate));
                }
            }
        }
        return resources;
    }

    @Override
    public List<String> getAllCurrencyCodes() {
        synchronized(allCurrencyCodes){ //users get a copy to protect read while daily updates
            return Collections.unmodifiableList(new ArrayList<String>(allCurrencyCodes));
        }
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

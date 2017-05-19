package com.company.sample.exchange.domain;

import com.company.sample.exchange.service.CurrExRateResource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private final static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final static Lock readLock = readWriteLock.readLock();

    private final static Lock writeLock = readWriteLock.writeLock();



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
    public void updateRepository(List<CurrExRateResource> resources) {
        //to avoid ConcurrentModification exception while iterating in getAllExchangeRatesBasedOnEuroForCurrency
        //to protect readers of the ArrayList at getAllCurrencyCodes
        writeLock.lock(); //blocks all readers during the update operation
        try {
            inMemoryContainerMap.clear();
            allCurrencyCodes.clear();
            resetMaxAndMinAvailableDateStr();
            for (CurrExRateResource resource : resources) {
                inMemoryContainerMap.put(generateKey(resource.getCurrencyCode(),
                        resource.getExchangeRateDate()), resource.getExchangeRate());
                updateMaxAndMinAvailableDateStr(resource.getExchangeRateDate());
                if (!allCurrencyCodes.contains(resource.getCurrencyCode().toUpperCase()))
                    allCurrencyCodes.add(resource.getCurrencyCode().toUpperCase());
            }
        }
        finally
        {
            writeLock.unlock();
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
        readLock.lock(); //forces wait during update operation
        try {
            return inMemoryContainerMap.get(generateKey(currencyCode, dateStr));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String getMaxAvailableDateStr() {
        readLock.lock(); //forces wait during update operation
        try {
            return maxAvailableDateStr;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String getMinAvailableDateStr() {
        readLock.lock(); //forces wait during update operation
        try {
            return minAvailableDateStr;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<CurrExRateResource> getAllExchangeRatesBasedOnEuroForCurrency(String currencyCode) {
        //a manually synchronized iterator is used
        List<CurrExRateResource> resources = new ArrayList<>();
        readLock.lock(); //forces wait during update operation
        try {
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
        } finally {
            readLock.unlock();
        }
        return resources;
    }

    @Override
    public List<String> getAllCurrencyCodes() {
        readLock.lock(); //forces wait during update operation
        try{
            return Collections.unmodifiableList(new ArrayList<String>(allCurrencyCodes));
        } finally {
            readLock.unlock();
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

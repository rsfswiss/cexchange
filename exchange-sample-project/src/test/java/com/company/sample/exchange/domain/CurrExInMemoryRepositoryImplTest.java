package com.company.sample.exchange.domain;

import com.company.sample.exchange.service.CurrExRateResource;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CurrExInMemoryRepositoryImplTest {

    @Test
    public void testGetAllExchangeRatesBasedOnEuroForCurrency() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        List<CurrExRateResource> resources = new ArrayList<>();
        resources.add(new CurrExRateResource("123.6","USD","20100911"));
        resources.add(new CurrExRateResource("124.9","USD","20100912"));
        repoImpl.updateRepository(resources);

        assertFalse(
                StringUtils.isEmpty(repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD")));
        assertTrue(
                repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD").size() == 2);
        assertTrue(
                repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD").stream().
                        anyMatch(c -> c.getExchangeRate().equals("123.6") &&
                                c.getCurrencyCode().equals("USD") &&
                                c.getExchangeRateDate().equals("20100911")));
        assertTrue(
                repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD").stream().
                        anyMatch(c -> c.getExchangeRate().equals("124.9") &&
                                c.getCurrencyCode().equals("USD") &&
                                c.getExchangeRateDate().equals("20100912")));
    }

    @Test
    public void testGetAllCurrencyCodes() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        List<CurrExRateResource> resources = new ArrayList<>();
        resources.add(new CurrExRateResource("123.6","USD","20100911"));
        resources.add(new CurrExRateResource("124.9","USD","20100912"));
        resources.add(new CurrExRateResource("124.1","JPY","20100912"));
        repoImpl.updateRepository(resources);

        assertFalse(
                StringUtils.isEmpty(repoImpl.getAllCurrencyCodes()));
        assertTrue(
                repoImpl.getAllCurrencyCodes().size() == 2);
        assertTrue(
                repoImpl.getAllCurrencyCodes().stream().
                        anyMatch(c -> c.equals("USD")));
        assertTrue(
                repoImpl.getAllCurrencyCodes().stream().
                        anyMatch(c -> c.equals("JPY")));
    }


    @Test
    public void testUpdateRepository() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        List<CurrExRateResource> resources = new ArrayList<>();
        resources.add(new CurrExRateResource("123.6","USD","20100911"));
        resources.add(new CurrExRateResource("124.9","USD","20100912"));
        repoImpl.updateRepository(resources);

        assertEquals(repoImpl.findByCurrencyCodeAndDate("USD", "20100911"), "123.6");
        assertEquals(repoImpl.findByCurrencyCodeAndDate("USD", "20100912"), "124.9");

        resources = new ArrayList<>();
        resources.add(new CurrExRateResource("923.6","USD","20100911"));
        resources.add(new CurrExRateResource("924.9","USD","20100912"));
        repoImpl.updateRepository(resources);

        assertEquals(repoImpl.findByCurrencyCodeAndDate("USD", "20100911"), "923.6");
        assertEquals(repoImpl.findByCurrencyCodeAndDate("USD", "20100912"), "924.9");
    }


    @Test
    public void testFindByCurrencyCodeAndDate() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        List<CurrExRateResource> resources = new ArrayList<>();
        resources.add(new CurrExRateResource("123.6","USD","20100911"));
        repoImpl.updateRepository(resources);

        assertEquals(repoImpl.findByCurrencyCodeAndDate("USD", "20100911"), "123.6");
    }

    @Test
    public void testGetMaxAvailableDateStr() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        List<CurrExRateResource> resources = new ArrayList<>();
        resources.add(new CurrExRateResource("123.6","USD","20100911"));
        resources.add(new CurrExRateResource("123.7","USD","20100912"));
        resources.add(new CurrExRateResource("123.7","USD","20100913"));
        resources.add(new CurrExRateResource("123.7","USD","20010912"));
        repoImpl.updateRepository(resources);

        assertEquals(repoImpl.getMaxAvailableDateStr(), "20100913");
    }

    @Test
    public void testGetMinAvailableDateStr() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        List<CurrExRateResource> resources = new ArrayList<>();
        resources.add(new CurrExRateResource("123.6","USD","20100911"));
        resources.add(new CurrExRateResource("123.7","USD","20100912"));
        resources.add(new CurrExRateResource("123.7","USD","20100913"));
        resources.add(new CurrExRateResource("123.7","USD","20070912"));
        repoImpl.updateRepository(resources);

        assertEquals(repoImpl.getMinAvailableDateStr(), "20070912");

    }

}
package com.company.sample.exchange.domain;

import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.Objects;


public class CurrExInMemoryRepositoryImplTest {

    @Test
    public void getAllExchangeRatesBasedOnEuroForCurrency() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        repoImpl.addOverwriting("124.9","USD","20100912");
        org.junit.Assert.assertFalse(
                StringUtils.isEmpty(repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD")));
        org.junit.Assert.assertTrue(
                repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD").size() == 2);
        org.junit.Assert.assertTrue(
                repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD").stream().
                        anyMatch(c -> c.getExchangeRate().equals("123.6") &&
                        c.getCurrencyCode().equals("USD") &&
                        c.getExchangeRateDate().equals("20100911")));
        org.junit.Assert.assertTrue(
                repoImpl.getAllExchangeRatesBasedOnEuroForCurrency("USD").stream().
                        anyMatch(c -> c.getExchangeRate().equals("124.9") &&
                                c.getCurrencyCode().equals("USD") &&
                                c.getExchangeRateDate().equals("20100912")));
    }

    @Test
    public void getAllCurrencyCodes() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        repoImpl.addOverwriting("124.9","USD","20100912");
        repoImpl.addOverwriting("124.1","JPY","20100912");
        org.junit.Assert.assertFalse(
                StringUtils.isEmpty(repoImpl.getAllCurrencyCodes()));
        org.junit.Assert.assertTrue(
                repoImpl.getAllCurrencyCodes().size() == 2);
        org.junit.Assert.assertTrue(
                repoImpl.getAllCurrencyCodes().stream().
                        anyMatch(c -> c.equals("USD")));
        org.junit.Assert.assertTrue(
                repoImpl.getAllCurrencyCodes().stream().
                        anyMatch(c -> c.equals("JPY")));
    }

    @Test
    public void deleteAll() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        repoImpl.addOverwriting("124.9","JPY","20100912");
        repoImpl.deleteAll();
        org.junit.Assert.assertTrue(
                StringUtils.isEmpty(repoImpl.findByCurrencyCodeAndDate("USD","20100911")));
        org.junit.Assert.assertTrue(
                StringUtils.isEmpty(repoImpl.findByCurrencyCodeAndDate("JPY","20100912")));
    }

    @Test
    public void addOverwriting() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        org.junit.Assert.assertTrue(
                Objects.equals(repoImpl.findByCurrencyCodeAndDate("USD", "20100911"), "123.6"));
        repoImpl.addOverwriting("123.7","USD","20100911");
        org.junit.Assert.assertTrue(
                Objects.equals(repoImpl.findByCurrencyCodeAndDate("USD", "20100911"), "123.7"));
    }


    @Test
    public void findByCurrencyCodeAndDate() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        org.junit.Assert.assertTrue(
                Objects.equals(repoImpl.findByCurrencyCodeAndDate("USD", "20100911"), "123.6"));
    }

    @Test
    public void getMaxAvailableDateStr() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        repoImpl.addOverwriting("123.7","USD","20100912");
        repoImpl.addOverwriting("123.7","USD","20100913");
        repoImpl.addOverwriting("123.7","USD","20010912");

        org.junit.Assert.assertTrue(
                Objects.equals(repoImpl.getMaxAvailableDateStr(), "20100913"));
    }

    @Test
    public void getMinAvailableDateStr() throws Exception {
        CurrExInMemoryRepositoryImpl repoImpl = new CurrExInMemoryRepositoryImpl();
        repoImpl.addOverwriting("123.6","USD","20100911");
        repoImpl.addOverwriting("123.7","USD","20100912");
        repoImpl.addOverwriting("123.7","USD","20100913");
        repoImpl.addOverwriting("123.7","USD","20010912");

        org.junit.Assert.assertTrue(
                Objects.equals(repoImpl.getMinAvailableDateStr(), "20010912"));
    }

}
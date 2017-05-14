package com.company.sample.exchange.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Repository resource,  with Json support.
 * Wraps the currency code, the date,
 * and the exchange rate, all in string
 * format.
 */
public class CurrExRateResource extends ResourceSupport {


    private String currencyCode;

    private String exchangeRateDate;

    private String exchangeRate;

    @JsonCreator
    public CurrExRateResource(@JsonProperty("exchangeRate") String exchangeRate,
                              @JsonProperty("currencyCode") String currencyCode,
                              @JsonProperty("exchangeRateDate") String exchangeRateDate) {
        this.exchangeRate = exchangeRate;
        this.currencyCode = currencyCode;
        this.exchangeRateDate = exchangeRateDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getExchangeRateDate() {
        return exchangeRateDate;
    }

    public void setExchangeRateDate(String exchangeRateDate) {
        this.exchangeRateDate = exchangeRateDate;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

}

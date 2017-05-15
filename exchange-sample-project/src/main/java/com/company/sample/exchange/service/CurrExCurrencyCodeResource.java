package com.company.sample.exchange.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Repository resource, with Json support.
 * Wraps the currency code, in string
 * format.
 */
public class CurrExCurrencyCodeResource extends ResourceSupport {

    private String currencyCode;


    @JsonCreator
    public CurrExCurrencyCodeResource(@JsonProperty("currencyCode") String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

}

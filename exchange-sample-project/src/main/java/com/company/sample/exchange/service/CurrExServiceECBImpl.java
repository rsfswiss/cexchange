package com.company.sample.exchange.service;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Implementation of the rate exchange service contract,
 * will retrieve and store the data from ECB on request:
 *
 * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
 *
 * and will provide the exchange rate value based on the stored values,
 * only available for the previous 90 days in ECB,
 * given a currency code (ISO 4217) as provided by ECB,
 * and the date for which the rate is requested.
 */
@Service
public class CurrExServiceECBImpl implements ICurrExService {


    @Override
    public void fetchAndStoreExchangeRateInformation() {
        //WIP
    }

    @Override
    public String getExchangeRateForEuroAtDate(String currencyCode, Date chgRateDate) {
        //WIP
        return "Hello World";
    }

}

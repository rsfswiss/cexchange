package com.company.sample.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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

//singleton scope by default, de-couple scheduled fetch operation in case you change scope
@Service
public class CurrExServiceECBImpl implements ICurrExService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * From:
     * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
     * "The reference rates are usually updated around 16:00 CET on every working day, except on TARGET closing days."
     *
     * This method should at least execute every day at 16:00 CET and at application startup.
     *
     */
    @Override
    @Scheduled(cron = "${currex.service.ecb.scheduler}")
    public void fetchAndStoreExchangeRateInformation() throws Exception{
        //WIP
        log.debug("############################ fetchAndStoreExchangeRateInformation executed  ############################ ");
    }

    @Override
    public String getExchangeRateForEuroAtDate(String currencyCode, Date chgRateDate) {
        //WIP
        return "Hello World";
    }

}

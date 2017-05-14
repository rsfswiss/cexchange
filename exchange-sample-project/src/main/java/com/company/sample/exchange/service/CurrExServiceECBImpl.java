package com.company.sample.exchange.service;

import com.company.sample.exchange.domain.CurrExRateResource;
import com.company.sample.exchange.domain.ICurrExRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

//singleton scope by default, de-couple scheduled fetch operation and data repository in case you change scope
@Service
public class CurrExServiceECBImpl implements ICurrExService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICurrExRepository currExRepository;

    /**
     * From:
     * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
     * "The reference rates are usually updated around 16:00 CET on every working day, except on TARGET closing days."
     *
     * This method should at least execute every day at 16:05 CET and at application startup.
     */
    @Override
    @Scheduled(cron = "${currex.service.ecb.scheduler.cron}", zone = "${currex.service.ecb.scheduler.zone}")
    public void fetchAndStoreExchangeRateInformation() throws Exception{
        //WIP
        log.debug("############################ fetchAndStoreExchangeRateInformation executed  ############################ ");
    }

    /**
     *
     * @param currencyCode
     * @param chgRateDate
     * @return the exchange rate in
     */
    @Override
    public CurrExRateResource getExchangeRateBasedOnEuroForCurrencyAtDate(String currencyCode, String chgRateDate) throws CurrExServiceException {
        //WIP
        return new CurrExRateResource("0",currencyCode, chgRateDate);
    }

}

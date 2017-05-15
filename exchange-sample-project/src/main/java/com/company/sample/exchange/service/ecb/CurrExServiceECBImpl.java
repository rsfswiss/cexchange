package com.company.sample.exchange.service.ecb;

import com.company.sample.exchange.connector.ecb.CurrExServiceECBConnector;
import com.company.sample.exchange.domain.ExchangeRate;
import com.company.sample.exchange.domain.ICurrExDataRestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the rate exchange service contract,
 * will retrieve and store the data from ECB on request:
 * <p>
 * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
 * <p>
 * and will provide the exchange rate value based on the stored values,
 * only available for the previous 90 days in ECB,
 * given a currency code (ISO 4217) as provided by ECB,
 * and the date for which the rate is requested.
 */

@Service
public class CurrExServiceECBImpl {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${currex.service.ecb.service.date.pattern.repository}")
    private String dateFormat;


    @Autowired
    private CurrExServiceECBConnector ecbConnector;

    @Autowired
    private ICurrExDataRestRepository repository;

    /**
     * From:
     * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
     * "The reference rates are usually updated around 16:00 CET on every working day, except on TARGET closing days."
     * <p>
     * This method should at least execute every day at 16:05 CET and at application startup.
     */
    @Scheduled(cron = "${currex.service.ecb.scheduler.cron}", zone = "${currex.service.ecb.scheduler.zone}")
    public void fetchAndStoreExchangeRateInformation() throws Exception {
        List<ExchangeRate> resourcesResult = ecbConnector.fetchCurrExRateResources();
        if (resourcesResult == null || resourcesResult.isEmpty())
            throw new Exception("Connector returned empty set of resources");
        repository.deleteAll();
        resourcesResult.forEach(r -> repository.save(r));
        log.debug("fetchAndStoreExchangeRateInformation executed successfully, added " + resourcesResult.size() + " rates from ECB.");
    }



}

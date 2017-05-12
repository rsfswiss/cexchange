package com.company.sample.exchange.controller;

import com.company.sample.exchange.service.ICurrExService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Rest API for euro currency exchage rates.
 * Delivers the exchange rate obtained from ECB
 * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
 * The rates are not available for current day and only for the previous 90 days.
 *
 * The request for a exchange rate must be of the form:
 *
 * http://example.com/eurocurrex/[CUR]/[Date]
 *
 * where [CUR] is the currency code from the ECB website, e.g. USD, JPY,..
 * and [Date] is the date in ISO-8601 format, e.g. 20170131
 *
 * for example:
 *
 * http://example.com/eurocurrex/USD/20170511
 *
 * Will return code 404 (not found) when the information is not available, e.g. the
 * currency code does not exist in ECB, or the date is out of the 90 previous days range
 *
 * Will return a 400 (Bad Request) when the date can't be interpreted (is not ISO-8601, YYYYDDMM).
 *
 * Produces JSON format responses and accepts both GET and POST requests.
 */
@RestController
@RequestMapping("/eurocurrex")
public class CurrExRestController  {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICurrExService currExService;

    @RequestMapping("/{currencyCode}/{chgRateDate}")
    public String exchange(@PathVariable String currencyCode, @PathVariable Date chgRateDate) {
        return currExService.getExchangeRateForEuroAtDate(currencyCode,chgRateDate);
    }

    @RequestMapping("/hello")
    public String hello() {
        log.debug("*********Dummy request");
        return currExService.getExchangeRateForEuroAtDate("",null);
    }

}

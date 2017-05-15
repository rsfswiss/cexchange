package com.company.sample.exchange.service.ecb;

import com.company.sample.exchange.connector.ecb.CurrExServiceECBConnector;
import com.company.sample.exchange.domain.ICurrExRepository;
import com.company.sample.exchange.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

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

    @Value("${currex.service.ecb.service.date.pattern.repository}")
    private String dateFormat;

    @Autowired
    private ICurrExRepository currExRepository;

    @Autowired
    private CurrExServiceECBConnector ecbConnector;

    /**
     * From:
     * http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html#dev
     * "The reference rates are usually updated around 16:00 CET on every working day, except on TARGET closing days."
     *
     * This method should at least execute every day at 16:05 CET and at application startup.
     */
    @Override
    @Scheduled(cron = "${currex.service.ecb.scheduler.cron}", zone = "${currex.service.ecb.scheduler.zone}")
    public void fetchAndStoreExchangeRateInformation() throws Exception {
        List<CurrExRateResource> resourcesResult =  ecbConnector.fetchCurrExRateResources();
        if(resourcesResult == null || resourcesResult.isEmpty())
            throw new Exception("Connector returned empty set of resources");
        currExRepository.deleteAll();
        resourcesResult.forEach(r ->
                currExRepository.addOverwriting(r.getExchangeRate(),r.getCurrencyCode(),r.getExchangeRateDate()));
        log.debug("fetchAndStoreExchangeRateInformation executed successfully, added " + resourcesResult.size() + " rates from ECB.");
        log.debug(" max available date is: " + currExRepository.getMaxAvailableDateStr());
        log.debug(" min available date is: " + currExRepository.getMinAvailableDateStr());
    }

    /**
     * Given a currency code, e.g. USD, and a date, e.g. 20170531
     * uses the injected repository imlementation to deliver
     * the exchange rate.
     *
     * Performs validation of both parameters and the result.
     *
     * @param currencyCode the currency in iso three char format
     * @param dateStr the date in iso yyyyMMdd format
     * @return the exchange rate if found in the repository and is
     * parseable to a valid java Double, exception otherwise
     * @throws CurrExServiceException as mapped in CurrExRestGlobalExceptionHandler
     */
    @Override
    public CurrExRateResource getExchangeRateBasedOnEuroForCurrencyAtDate(String currencyCode, String dateStr) throws CurrExServiceException {
        validateParameters(currencyCode, dateStr);
        String exChangeRate = currExRepository.findByCurrencyCodeAndDate(currencyCode, dateStr);
        validateResult(exChangeRate);
        return new CurrExRateResource(exChangeRate, currencyCode, dateStr);
    }

    @Override
    public List<CurrExRateResource> getAllExchangeRatesBasedOnEuroForCurrency(String currencyCode) throws CurrExServiceException {
        validateCurrencyCode(currencyCode);
        return currExRepository.getAllExchangeRatesBasedOnEuroForCurrency(currencyCode);
    }

    @Override
    public List<CurrExCurrencyCodeResource> getAllCurrencyCodes() {
        return currExRepository.getAllCurrencyCodes().stream().
                map(s -> new CurrExCurrencyCodeResource(s)).collect(Collectors.toList());
    }

    //this should be moved to a Validator in the future
    private void validateCurrencyCode(String currencyCode) throws CurrExServiceException{
        //to improve validation, validate against a list of ISO currency codes
        if(StringUtils.isEmpty(currencyCode)
                || currencyCode.length() !=3
                || !Character.isLetter(currencyCode.charAt(0))
                || !Character.isLetter(currencyCode.charAt(1))
                || !Character.isLetter(currencyCode.charAt(2)))
            throw new CurrExServiceCurrencyIncorrectException();
    }

    //this should be moved to a Validator in the future
    private void validateParameters(String currencyCode, String dateStr) throws CurrExServiceException{

        validateCurrencyCode( currencyCode);

        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(dateFormat);
            LocalDate.parse(dateStr, formatter);
        }
        catch (DateTimeParseException exc) {
            throw new CurrExServiceDateNotRecognizedException();
        }

        if(dateStr.compareTo(currExRepository.getMaxAvailableDateStr()) > 0)
            throw new CurrExServiceDateTooNewException();

        if(dateStr.compareTo(currExRepository.getMinAvailableDateStr()) < 0)
            throw new CurrExServiceDateTooOldException();

    }

    private void validateResult(String exChangeRate) throws CurrExServiceException{

        if(StringUtils.isEmpty(exChangeRate)) throw new CurrExServiceDataNotFoundException();

        try {
            Double.parseDouble(exChangeRate);
        }
        catch (NumberFormatException e) {
            throw new CurrExServiceCurrencyNotAvailableException();
        }
    }

}

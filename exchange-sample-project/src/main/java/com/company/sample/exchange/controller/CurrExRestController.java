package com.company.sample.exchange.controller;

import com.company.sample.exchange.domain.CurrExRateResource;
import com.company.sample.exchange.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Rest API for euro currency exchange rates.
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
 * Will return a 400 (Bad Request) when the date can't be interpreted (is not ISO-8601, YYYYMMDD).
 *
 * Produces JSON format responses and accepts only GET requests.
 */
@RestController
//TODO: adding the base uri mapping property currex.controller.uri.base breaks the ControllerLinkBuilder below
@RequestMapping(value = "eurocurrex",
        method = RequestMethod.GET,
        produces = "application/json")
public class CurrExRestController  {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICurrExService currExService;

    @Autowired
    private Environment env;

    @RequestMapping(value = "{currencyCode}/{chgRateDate}", method = RequestMethod.GET, produces = "application/json")
    public HttpEntity<CurrExRateResource> exchange(@PathVariable String currencyCode, @PathVariable String chgRateDate) throws Exception
    {
        CurrExRateResource exchangeRateResource = currExService.getExchangeRateBasedOnEuroForCurrencyAtDate(currencyCode, chgRateDate);
        exchangeRateResource.add(ControllerLinkBuilder.linkTo(methodOn(this.getClass()).exchange(currencyCode, chgRateDate)).withSelfRel());
        return new ResponseEntity<CurrExRateResource>(exchangeRateResource, HttpStatus.OK);
    }

    @ExceptionHandler({CurrExServiceException.class, Exception.class})
    public ResponseEntity<String> unDeterminedCurrExException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.undetermined");
        return new ResponseEntity<String>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CurrExServiceCurrencyNotAvailableException.class)
    public ResponseEntity<String> currExServiceCurrencyNotAvailableException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.currency.na");
        return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrExServiceDataNotFoundException.class)
    public ResponseEntity<String> currExServiceDataNotFoundException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.undetermined");
        return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrExServiceCurrencyIncorrectException.class)
    public ResponseEntity<String> currExServiceCurrencyIncorrectException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.currency.incorrect");
        return new ResponseEntity<String>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrExServiceDateNotRecognizedException.class)
    public ResponseEntity<String> currExServiceDateNotRecognizedException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.date.incorrect");
        return new ResponseEntity<String>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrExServiceDateTooNewException.class)
    public ResponseEntity<String> currExServiceDateTooNewException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.date.too.new");
        return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrExServiceDateTooOldException.class)
    public ResponseEntity<String> currExServiceDateTooOldException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.date.too.old");
        return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
    }


}

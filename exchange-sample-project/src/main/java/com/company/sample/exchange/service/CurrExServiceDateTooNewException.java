package com.company.sample.exchange.service;

/**
 * The date requested is past the available range. This service only provides data for the past 90 days. Please make sure to use format YYYYMMDD.
 */
public class CurrExServiceDateTooNewException extends CurrExServiceException {

}

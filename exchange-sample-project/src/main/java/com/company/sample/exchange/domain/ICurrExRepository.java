package com.company.sample.exchange.domain;

/**
 * Repository definition, allows
 * a total reset of the contents,
 * adding and finding resources.
 *
 * A currency and date combination uniquely identifies
 * a resource.
 */
public interface ICurrExRepository {

    void deleteAll();

    void addOverwriting(CurrExRateResource resource);

    CurrExRateResource find(String currency, String date);
}

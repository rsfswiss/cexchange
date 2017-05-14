package com.company.sample.exchange.domain;

import org.springframework.stereotype.Repository;

/**
 * Wraps a Hasmap as a very simple <key,value> pair
 * in memory repository implementation for the currex sample project.
 *
 * Can be moved to a persisted solution by providing a different implementation.
 */
@Repository
public class CurrExInMemoryRepositoryImpl implements ICurrExRepository {


    @Override
    public void deleteAll() {
        //WIP
    }

    @Override
    public void addOverwriting(CurrExRateResource resource) {
        //WIP
    }

    @Override
    public CurrExRateResource find(String currency, String date) {
        //WIP
        return null;
    }


}

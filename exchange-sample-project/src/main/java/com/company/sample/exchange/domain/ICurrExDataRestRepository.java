package com.company.sample.exchange.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "exchangeRates", path = "exchangeRates")
public interface ICurrExDataRestRepository extends PagingAndSortingRepository<ExchangeRate, Long> {

    List<ExchangeRate> findByExchangeRateDate(@Param("exchangeRateDate") String exchangeRateDate);

    List<ExchangeRate> findByCurrencyCode(@Param("currencyCode") String currencyCode);

    List<ExchangeRate> findByCurrencyCodeAndExchangeRateDate(@Param("currencyCode") String currencyCode, @Param("exchangeRateDate") String exchangeRateDate);

}

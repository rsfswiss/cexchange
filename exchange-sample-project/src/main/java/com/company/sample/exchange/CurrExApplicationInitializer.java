package com.company.sample.exchange;

import com.company.sample.exchange.service.ecb.CurrExServiceECBImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Used to run the ECB update at the startup.
 * Should not let the application start in case of exception.
 */
@Component
public class CurrExApplicationInitializer implements InitializingBean {

    @Autowired
    private CurrExServiceECBImpl currExService;

    @Override
    public void afterPropertiesSet() throws Exception {
        currExService.fetchAndStoreExchangeRateInformation();
        //an exception will stop spring application as expected
    }
}

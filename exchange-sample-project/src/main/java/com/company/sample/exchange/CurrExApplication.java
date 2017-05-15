package com.company.sample.exchange;

import com.company.sample.exchange.service.ecb.CurrExServiceECBImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrExApplication {

    @Autowired
    private CurrExServiceECBImpl currExService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(CurrExApplication.class, args);
    }
}

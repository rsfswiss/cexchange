package com.company.sample.exchange;

import com.company.sample.exchange.service.ICurrExService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO: retry strategy for the ECB scheduler, add global exception handler
@SpringBootApplication
@EnableScheduling
public class CurrExApplication {

    @Autowired
    private ICurrExService currExService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(CurrExApplication.class, args);
    }
}

package com.company.sample.exchange.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currex")
public class CurrExRestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/exchange")
    public String exchange() {
        log.debug("Logging works!");
        return "Hello World!";
    }
}

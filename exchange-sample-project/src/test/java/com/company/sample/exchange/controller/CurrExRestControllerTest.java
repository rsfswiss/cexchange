package com.company.sample.exchange.controller;

import com.company.sample.exchange.CurrExApplication;
import com.company.sample.exchange.domain.CurrExRateResource;
import com.company.sample.exchange.service.CurrExServiceCurrencyIncorrectException;
import com.company.sample.exchange.service.CurrExServiceDateNotRecognizedException;
import com.company.sample.exchange.service.CurrExServiceDateTooOldException;
import com.company.sample.exchange.service.ICurrExService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrExApplication.class)
@AutoConfigureMockMvc
public class CurrExRestControllerTest {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @MockBean
    private ICurrExService currExService;

    private MockMvc mockMvc;

    @Value("${currex.controller.uri.base}")
    private String baseUri;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Environment env;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        Mockito.doNothing().when(currExService).fetchAndStoreExchangeRateInformation();
    }

    @Test
    public void testGetEurExchgRateForISO8601Date() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","20170511")).
                willReturn(new CurrExRateResource("1.086","USD","20170511"));

        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/20170511")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRate").value("1.086"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyCode").value("USD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRateDate").value("20170511"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value("http://localhost/eurocurrex/USD/20170511"));

        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("JPY","20170511")).
                willReturn(new CurrExRateResource("123.69","JPY","20170511"));

        mockMvc.perform(get("/"+baseUri+"/JPY/20170511")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRate").value("123.69"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyCode").value("JPY"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRateDate").value("20170511"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value("http://localhost/eurocurrex/JPY/20170511"));

    }

    @Test
    public void testGetEurExchgRateForISO8601DateTooOld() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","20170511")).
                willThrow(new CurrExServiceDateTooOldException());
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/20170511")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(content().string(env.getProperty("currex.controller.message.date.too.old")));
    }

    @Test
    public void testGetEurExchgRateForISO8601CurrencyDoesNotExist() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("ER","20170511")).
                willThrow(new CurrExServiceCurrencyIncorrectException());
        mockMvc.perform(get("/"+baseUri+"/ERR/20170511")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(env.getProperty("currex.controller.message.currency.incorrect")));
    }

    @Test
    public void testGetEurExchgRateForUnrecognizedDateFormat() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","05-11-2017")).
                willThrow(new CurrExServiceDateNotRecognizedException());
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/05-11-2017")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(env.getProperty("currex.controller.message.date.incorrect")));
    }

    @Test
    public void testGetEurExchgRateForInvalidDate() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","ERROR2017")).
                willThrow(new CurrExServiceDateNotRecognizedException());
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/ERROR2017")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(env.getProperty("currex.controller.message.date.incorrect")));
    }

    @Test
    //TODO we do not list all dates at the moment, this error should be detected by Spring REST
    public void testMissingDate() throws Exception {
        mockMvc.perform(get("/"+baseUri+"/USD")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(content().string(env.getProperty("currex.controller.message.resource.not.found")));
    }

    @Test
    //TODO we do not list resources at the moment, this error should be detected by Spring REST
    public void testMissingURI() throws Exception {
        mockMvc.perform(get("/"+baseUri)
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(content().string(env.getProperty("currex.controller.message.resource.not.found")));
    }

}
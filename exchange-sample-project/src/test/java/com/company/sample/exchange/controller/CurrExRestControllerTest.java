package com.company.sample.exchange.controller;

import com.company.sample.exchange.CurrExApplication;
import com.company.sample.exchange.service.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Value("${server.servlet-path}")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value(endsWith("/USD/20170511")));

        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("JPY","20170511")).
                willReturn(new CurrExRateResource("123.69","JPY","20170511"));

        mockMvc.perform(get("/"+baseUri+"/JPY/20170511")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRate").value("123.69"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currencyCode").value("JPY"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exchangeRateDate").value("20170511"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value(endsWith("/JPY/20170511")));

    }

    @Test
    public void testGetEurExchgRateForISO8601DateTooOld() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","20170511")).
                willThrow(new CurrExServiceDateTooOldException());
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/20170511")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMsg").
                        value(env.getProperty("currex.controller.message.date.too.old")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("404"));
    }

    @Test
    public void testGetEurExchgRateForISO8601CurrencyDoesNotExist() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("ER","20170511")).
                willThrow(new CurrExServiceCurrencyIncorrectException());
        mockMvc.perform(get("/"+baseUri+"/ER/20170511")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMsg").
                        value(env.getProperty("currex.controller.message.currency.incorrect")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"));
    }

    @Test
    public void testGetEurExchgRateForUnrecognizedDateFormat() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","05-11-2017")).
                willThrow(new CurrExServiceDateNotRecognizedException());
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/05-11-2017")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMsg").
                        value(env.getProperty("currex.controller.message.date.incorrect")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"));
    }

    @Test
    public void testGetEurExchgRateForInvalidDate() throws Exception {
        given(currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD","ERROR2017")).
                willThrow(new CurrExServiceDateNotRecognizedException());
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/"+baseUri+"/USD/ERROR2017")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMsg").
                        value(env.getProperty("currex.controller.message.date.incorrect")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"));
    }

    @Test
    //TODO: find a way to test for any json element order
    public void testGetAllCurrencyCodes() throws Exception {
        CurrExCurrencyCodeResource usdRes = new CurrExCurrencyCodeResource("USD");
        CurrExCurrencyCodeResource jpyRes = new CurrExCurrencyCodeResource("JPY");
        List<CurrExCurrencyCodeResource> allCurrRes =new ArrayList<CurrExCurrencyCodeResource>();
        allCurrRes.add(usdRes);
        allCurrRes.add(jpyRes);
        given(currExService.getAllCurrencyCodes()).willReturn(allCurrRes);
        mockMvc.perform(get("/"+baseUri)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].currencyCode").value("USD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].currencyCode").value("JPY"));
    }


    @Test
    //TODO: find a way to test for any json element order
    public void testGetAllExchangeRatesForCurrency() throws Exception {
        List<CurrExRateResource> resources = new ArrayList<CurrExRateResource>();
        resources.add(new CurrExRateResource("1.086","USD","20170511"));
        resources.add(new CurrExRateResource("1.0867","USD","20170512"));
        given(currExService.getAllExchangeRatesBasedOnEuroForCurrency("USD")).willReturn(resources);

        mockMvc.perform(get("/"+baseUri+"/USD")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].currencyCode").value("USD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].currencyCode").value("USD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].exchangeRate").value("1.086"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].exchangeRate").value("1.0867"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].exchangeRateDate").value("20170511"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].exchangeRateDate").value("20170512"));

    }

}
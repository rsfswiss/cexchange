package com.company.sample.exchange.controller;

import com.company.sample.exchange.CurrExApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrExApplication.class)
@WebAppConfiguration
public class CurrExRestControllerTest {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        //TODO mock service with currency values

    }

    @Test
    public void testGetEurExchgRateForISO8601Date() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex/USD/20170511")
                .content(this.json("1.086"))
                .contentType(contentType))
                .andExpect(status().isOk());

        mockMvc.perform(get("/eurocurrex/JPY/20170511")
                .content(this.json("123.69"))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEurExchgRateForISO8601DateTooOld() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex/USD/20170511")
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEurExchgRateForISO8601CurrencyDoesNotExist() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex/ERR/20170511")
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEurExchgRateForUnrecognizedDateFormat() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex/USD/05-11-2017")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEurExchgRateForInvalidDate() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex/USD/ERROR2017")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    //we do not list all dates at the moment
    public void testMissingDate() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex/USD")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingURI() throws Exception {
        //YYYYMMDD is the ISO8601, the format we support
        mockMvc.perform(get("/eurocurrex")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
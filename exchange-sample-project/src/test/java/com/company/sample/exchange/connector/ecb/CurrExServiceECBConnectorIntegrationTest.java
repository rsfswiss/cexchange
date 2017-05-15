package com.company.sample.exchange.connector.ecb;

import com.company.sample.exchange.CurrExApplication;
import com.company.sample.exchange.CurrExApplicationInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrExApplication.class)
@AutoConfigureMockMvc
public class CurrExServiceECBConnectorIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CurrExServiceECBConnector connector;

    @MockBean
    private CurrExApplicationInitializer currExApplicationInitializer;


    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Ignore
    public void fetchCurrExRateResources() throws Exception {
        String xmlFeedFromECB = connector.fetchXmlFeed();
        Assert.assertFalse(StringUtils.isEmpty(xmlFeedFromECB));
        //arbitrary sanity check
        Assert.assertTrue(StringUtils.countOccurrencesOf(xmlFeedFromECB, "<Cube currency=\"USD\"") > 30);
        Assert.assertTrue(StringUtils.countOccurrencesOf(xmlFeedFromECB, "<Cube time") > 30);
    }

}
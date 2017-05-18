package com.company.sample.exchange.connector.ecb;

import com.company.sample.exchange.CurrExApplication;
import com.company.sample.exchange.CurrExApplicationInitializer;
import com.company.sample.exchange.service.CurrExRateResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrExApplication.class)
@AutoConfigureMockMvc
public class CurrExServiceECBConnectorTest {

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
    public void testDeSerializeFeed() throws Exception {
        String fakeXmlFromECB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">" +
                "<gesmes:subject>Reference rates</gesmes:subject><gesmes:Sender><gesmes:name>European Central Bank</gesmes:name></gesmes:Sender>" +
                "<Cube>" +
                "<Cube time=\"2017-05-12\">" +
                "<Cube currency=\"USD\" rate=\"1.0876\"/>" +
                "<Cube currency=\"JPY\" rate=\"123.82\"/>" +
                "</Cube>\n" +
                "<Cube time=\"2017-05-11\">" +
                "<Cube currency=\"USD\" rate=\"1.086\"/>" +
                "<Cube currency=\"JPY\" rate=\"123.69\"/>" +
                "</Cube>\n" +
                "</Cube>" +
                "</gesmes:Envelope>";

        List<CurrExRateResource> resources = connector.deSerializeFeed(fakeXmlFromECB);
        Assert.assertTrue(resources != null);
        Assert.assertTrue(resources.size() == 4);
        Assert.assertTrue(resources.stream().anyMatch(r -> r.getCurrencyCode().equals("USD")
                && r.getExchangeRate().equals("1.0876") && r.getExchangeRateDate().equals("20170512")));
        Assert.assertTrue(resources.stream().anyMatch(r -> r.getCurrencyCode().equals("USD")
                && r.getExchangeRate().equals("1.086") && r.getExchangeRateDate().equals("20170511")));
        Assert.assertTrue(resources.stream().anyMatch(r -> r.getCurrencyCode().equals("JPY")
                && r.getExchangeRate().equals("123.82") && r.getExchangeRateDate().equals("20170512")));
        Assert.assertTrue(resources.stream().anyMatch(r -> r.getCurrencyCode().equals("JPY")
                && r.getExchangeRate().equals("123.69") && r.getExchangeRateDate().equals("20170511")));

    }

}
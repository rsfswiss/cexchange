package com.company.sample.exchange.connector.ecb;

import org.junit.Test;


public class CurrExServiceECBConnectorTest {

    @Test
    public void deSerializeFeed() throws Exception {
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

        //ObjectMapper xmlmapper = new XmlMapper();
       // CurrExRateResource resource = xmlmapper.readValue(response.toString(),CurrExRateResource.class);

    }

}
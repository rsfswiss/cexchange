package com.company.sample.exchange.connector.ecb;

import com.company.sample.exchange.connector.ICurrExServiceConnector;
import com.company.sample.exchange.domain.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



/**
 * Encapsulates http retrieval of the raw ECB resource from:
 * <p>
 * http://www.ecb.europa.eu/stats/eurofxref/
 * <p>
 * Will parse the xml and produce a list of CurrExRateResource,
 * or throw an Exception in case of failure.
 */
@Service
public class CurrExServiceECBConnector implements ICurrExServiceConnector {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Value("${currex.connector.ecb.endpoint}")
    private String endPoint;

    @Value("${currex.service.ecb.service.date.pattern.repository}")
    private String dateRepositoryFormat;

    @Value("${currex.service.ecb.service.date.pattern.ecb}")
    private String dateECBFormat;


    public List<ExchangeRate> fetchCurrExRateResources() throws Exception {
        String xmlContent = fetchXmlFeed();
        return deSerializeFeed(xmlContent);
    }

    //ECB xml has three nested 'Cube' tags!!!
    protected List<ExchangeRate> deSerializeFeed(String xmlContent) throws Exception {
        List<ExchangeRate> resultResources = new ArrayList<ExchangeRate>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlContent)));
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            //e.g. <Cube><Cube time="2017-05-12"><Cube currency="USD" rate="1.0876"/>
            //skipping the namespace problem by using local-name
            String expression = "/*[local-name()='Envelope']/*[local-name()='Cube']/*[@time]";
            NodeList datesNodeList = (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            DateTimeFormatter ecbFormatter =
                    DateTimeFormatter.ofPattern(dateECBFormat);
            DateTimeFormatter repositoryFormatter =
                    DateTimeFormatter.ofPattern(dateRepositoryFormat);
            for (int i = 0; i < datesNodeList.getLength(); i++) {
                try {
                    Node cubeTimeNode = datesNodeList.item(i);
                    String date = cubeTimeNode.getAttributes().getNamedItem("time").getNodeValue();
                    //comes as yyyy-MM-dd
                    LocalDate localDate = LocalDate.parse(date, ecbFormatter);
                    //we want yyyyMMdd
                    date = localDate.format(repositoryFormatter);
                    //exchange rates for this day
                    NodeList ratesNodeList = cubeTimeNode.getChildNodes();
                    for (int j = 0; j < ratesNodeList.getLength(); j++) {
                        Node cubeRateNode = ratesNodeList.item(j);
                        String currencyCode = cubeRateNode.getAttributes().getNamedItem("currency").getNodeValue();
                        String exRate = cubeRateNode.getAttributes().getNamedItem("rate").getNodeValue();
                        ExchangeRate rate = new ExchangeRate();
                        rate.setCurrencyCode(currencyCode);
                        rate.setExchangeRate(exRate);
                        rate.setExchangeRateDate(date);
                        resultResources.add(rate);
                    }
                } catch (Exception ex) {
                    log.warn("Error found parsing data from ecb. ", ex);
                }
            }

        } catch (Exception e) {
            log.error("Failure parsing data from ecb. ", e);
            throw e;
        }
        return resultResources;
    }

    protected String fetchXmlFeed() throws Exception {
        URL url = new URL(endPoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // By default it is GET request
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String output;
            StringBuffer response = new StringBuffer();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            if ((responseCode < 200 || responseCode > 299) && responseCode != 304)
                throw new Exception("HTTP request to " + endPoint + " failed with response code " +
                        responseCode + " " + response.toString());
            return response.toString();
        } catch (Exception e) {
            log.error("Failure fetching data from ecb. ", e);
            throw e;
        }
    }

}

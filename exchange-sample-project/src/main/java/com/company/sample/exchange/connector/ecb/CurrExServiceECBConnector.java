package com.company.sample.exchange.connector.ecb;

import com.company.sample.exchange.connector.ICurrExServiceConnector;
import com.company.sample.exchange.service.CurrExRateResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

;

/**
 * Encapsulates http retrieval of the raw ECB resource from:
 *
 * http://www.ecb.europa.eu/stats/eurofxref/
 *
 * Will parse the xml and produce a list of CurrExRateResource,
 * or throw an Exception in case of failure.
 */
@Service
public class CurrExServiceECBConnector implements ICurrExServiceConnector{

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Value("${currex.connector.ecb.endpoint}")
    private String endPoint;


    public List<CurrExRateResource> fetchCurrExRateResources() throws Exception {
        String xmlContent = fetchXmlFeed();
        return deSerializeFeed(xmlContent);
    }

    protected List<CurrExRateResource> deSerializeFeed(String xmlContent) {
        //WIP
        return new ArrayList<CurrExRateResource>();
    }

    protected String fetchXmlFeed() throws Exception {
        URL url = new URL(endPoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // By default it is GET request
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
            String output;
            StringBuffer response = new StringBuffer();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            if((responseCode < 200 || responseCode > 299) && responseCode != 304)
                throw new Exception("HTTP request to " + endPoint +  " failed with response code " +
                        responseCode + " " + response.toString());
            return response.toString();
        } catch(Exception e){
            log.error("Failure fetching data from ecb. ", e);
            throw e;
        }
    }

}

package ftn.e2.udd.websearch.api.impl;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import ftn.e2.udd.websearch.api.crawler.ContinuousCrawlJobConfig;
import ftn.e2.udd.websearch.api.crawler.ContinuousCrawlerJobResponse;
import ftn.e2.udd.websearch.api.crawler.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 *  Calling Nutch Crawler Job
 *
 * @author a.ilic
 */
public class CrawlerServiceImpl implements CrawlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    private Client client;
    private WebResource nutchResource;
    private static String host = "localhost";
    private static Integer port = 7601;

    public CrawlerServiceImpl() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures()
                .put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        this.client = Client.create(clientConfig);
        this.nutchResource= client.resource(getUrl());
    }

    @Override
    public ContinuousCrawlerJobResponse getResults(ContinuousCrawlJobConfig request) {
        return nutchResource.path("/ccjob/create").type(APPLICATION_JSON)
                .post(ContinuousCrawlerJobResponse.class, request);
    }

    public URI getUrl() {
        try {
            return new URI("http", null, host, port, null, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot parse url parameters", e);
        }
    }
}

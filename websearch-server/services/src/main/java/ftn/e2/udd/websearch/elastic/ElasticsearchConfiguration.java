package ftn.e2.udd.websearch.elastic;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Configuration
@PropertySource(value = "classpath:elasticsearch.properties")
public class ElasticsearchConfiguration {

	@Resource
	private Environment environment;

//	@Bean
	/**
	 * Configures a transport client for the Elastic search cluster.
	 * This configuration should be used when the Elastic search cluster is already up and running
	 * 
	 * @return {@link org.elasticsearch.client.Client}
	 */
	public Client transportClient() {
		String host = environment.getProperty("elasticsearch.host");
		int port = Integer.parseInt(environment.getProperty("elasticsearch.port"));
		
		TransportClient transportClient = new TransportClient () ;
		transportClient.addTransportAddress(
				new InetSocketTransportAddress(new InetSocketAddress(host, port)));

		return transportClient;
	}


}

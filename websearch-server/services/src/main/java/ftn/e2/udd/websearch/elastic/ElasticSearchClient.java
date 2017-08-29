package ftn.e2.udd.websearch.elastic;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Elastic Search Client class which is responsible to perform crud operations on elastic search.
 */
public class ElasticSearchClient {
	private Client client;
	private Client getClient() {
		return client;
	}
	/** 
	 * Creates Elastic Search transport client based on the input parameters - host and port. 
	 * This method creates transport client which can connect only to single instance ES server.
	 * Returns true if transportClient is successfully created.
	 * 
	 * * @param host The hostname/ipaddess of the elastic search server 
	 * @param port The port on which Elastic search service is running on
	 * @return <<tt>>true<<tt>> if successful transport client creation
	 * @throws java.net.UnknownHostException
	 */
	public  Client initClient(String host, int port) throws UnknownHostException {
		return client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
	}
	/**
	 * This method indexes document in elastic search. Document is in json format.
	 * Returns true if client can successfully creates a new index 
	 * @param indexName The name of the index
	 * @param indexType Thr type of the index
	 * @param json The document to be indexed
	 * @param numRetries The number of retries in case of error for fault tolerance
	 * @return <<tt>>true<<tt>> if successful document creation
	 * @throws java.util.concurrent.ExecutionException
	 * @throws InterruptedException 
	 */
	public IndexResponse indexSingle(String indexName, String indexType, String json, int numRetries) throws InterruptedException, ExecutionException {
		return   client.prepareIndex(indexName,indexType)
				.setSource(json).execute()
				.actionGet();
	}
	/**
	 * This method performs bulk indexing of document in elastic search.
	 * Returns true if bulk indexing is successful 
	 * @param indexRequestList The list of all index requests
	 * @return 
	 * @return <<tt>>true<<tt>> if successful document creation
	 * @throws java.util.concurrent.ExecutionException
	 * @throws InterruptedException 
	 */
	public boolean indexBulk(List<IndexRequest> indexRequestList) throws InterruptedException, ExecutionException {
		if(indexRequestList==null)
			throw new NullPointerException();
		if(indexRequestList.isEmpty()) 
			throw new IllegalArgumentException("");
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		indexRequestList.forEach(indexRequest-> {
			bulkRequest.add(indexRequest);});
		BulkResponse response = bulkRequest.execute().get();
		return response.hasFailures();
	}
/* 	public SearchResponse search(String indexType, String fields,String ...indexName) {
		boolean indexExists = client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();

		SearchResponse allHits = client.prepareSearch(indexName)
				.setTypes(indexType)
				.addFields("flight_no", "category")
				.setQuery(QueryBuilders.matchAllQuery())
				.addAggregation(
						AggregationBuilders.terms("dt_timeaggs").field("pilot_name"))
				.execute().actionGet();
		return allHits;
	} */

	public void close() {
		client.close();		
	}
}	

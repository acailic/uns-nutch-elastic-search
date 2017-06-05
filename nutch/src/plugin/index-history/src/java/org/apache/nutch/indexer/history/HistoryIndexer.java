/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.indexer.history;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.crawl.SignatureComparator;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.StorageUtils;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * If a {@link WebPage} was modified since last indexing, that WebPage is copied to
 * a corresponding {@link CrawledWebPage} DB collection.
 * 
 * @author yairshefi
 */
public class HistoryIndexer implements IndexingFilter {

  private Configuration conf;

  public static final Logger LOG = LoggerFactory.getLogger(HistoryIndexer.class);
  
  private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

  static {
	// all WebPage's fields are needed since they're all copied to CrawledWebPage
	for (WebPage.Field field : WebPage.Field.values()) {
		FIELDS.add(field);
	}
  }

  /**
   * Compares <code>page</code>'s {@link WebPage#getSignature() signature} the
   * corresponding {@link CrawledWebPage}'s pages signatures. If the {@link CrawledWebPage} 
   * that corresponds in the DB to <code>url</code> doesn't already contain a {@link WebPage}
   * with the same signature, then <code>page</code> is copied into a new {@link WebPage} under
   * that {@link CrawledWebPage}.
   * <p>
   * The schema / collection in which {@link CrawledWebPage} is searched for in the DB,
   * is configurable via <code>crawled.schema.webpage</code> in <code>nutch-site.xml</code>.
   * @param doc that's obtains information regarding which fields of <code>page</code> are to index.
   * 			This isn't used in this method.
   * @param url from which <code>page</code> was fetched.
   * @param page that obtains page information that was fetched from <code>url</code> and reltated metadata. 
   */
  @Override
  public NutchDocument filter(NutchDocument doc, String url, WebPage page) throws IndexingException {

    // just in case
    if (doc == null)
      return doc;

    try {
	    conf.unset("preferred.schema.name");
	    final String schema = conf.get("history.schema.webpage", "c_webpage");
	    
	    final DataStore<String, CrawledWebPage> dataStore = createDataStore(schema);
	
	    final CrawledWebPage crawledWebPage = Objects.firstNonNull(dataStore.get(url), new CrawledWebPage());
	    
	    List<WebPage> webPageData = crawledWebPage.getWebPageData();
	    if (webPageData == null) {
			webPageData = new ArrayList<>();
			crawledWebPage.setWebPageData(webPageData);
		}
	    
	    final boolean skipUnmodified = Boolean.parseBoolean( conf.get("history.skip.unmodified", "true") );
	    if ( skipUnmodified ) {
		    final ByteBuffer signature = page.getSignature();
		    for (final WebPage historyWebPage : webPageData) {
			  final ByteBuffer prevSig = historyWebPage.getSignature();
		      if (SignatureComparator.compare(prevSig, signature) == 0) {
		        return doc; // current page with current content was already copied to history
		      }
			}
	    }
	    
	    copyPage(url, page, dataStore, crawledWebPage);
	    
	    final String msg = buildMessage(url, page.getFetchTime());
	    sendEvent(msg);
    } catch (Exception e) {
    	LOG.error(String.format("Failed indexing url %s", url), e);
    }

	return doc;
  }

  private void sendEvent(final String message) {
	  
	ConnectionFactory factory = new ConnectionFactory();
	final String host = conf.get("history.rabbitmq.host", "localhost");
    factory.setHost( host );
    
    final String user = conf.get("history.rabbitmq.user");
    factory.setUsername( user );
    
    final String password = conf.get("history.rabbitmq.password");
    factory.setPassword( password );
    
    Connection connection = null;
    Channel channel = null;    
	try {
		connection = factory.newConnection();
		channel = connection.createChannel();
	} catch (IOException | TimeoutException e) {
		final String errMsg = "failed opening connection or channel";
		LOG.error(errMsg, e);
		closeChannelAndConnection(channel, true);
		throw new RuntimeException(errMsg, e);
	}
	
	final String queueNamesStr = conf.get("history.rabbitmq.queue.names", "nlp,langdetector");
	final String[] queueNames = queueNamesStr.split(",");
	for (final String singleQueueName : queueNames) {
		try {
			channel.queueDeclare(singleQueueName, true, false, false, null);
			channel.basicPublish("", singleQueueName, null, message.getBytes());
			LOG.debug(" [x] Sent '" + message + "'");
		} catch (IOException e) {
			final String errMsg = String.format( "failed sending message [%s] to queue [%s]", message, singleQueueName);
			LOG.error(errMsg, e);
		}
	}
	
	closeChannelAndConnection(channel, false);
  }

  private String buildMessage(final String url, final Long fetchTime) {
	final EventMessage eventMessage = buildEventMessage(url, fetchTime);
	
	final Gson gson = new Gson();
	final String eventJson = gson.toJson(eventMessage);
	
	return eventJson;
  }

  private EventMessage buildEventMessage(final String url, final Long fetchTime) {
	final EventMessage eventMessage = new EventMessage();
	
	final String messageType = conf.get("newdata.msg.type", "newUrlData");
	eventMessage.setMsgType(messageType);
	
	final String source = conf.get("newdata.source", "crawler");
	eventMessage.setSource(source);
	
	eventMessage.setMsgUuid(UUID.randomUUID());
	
	final HashMap<String, Object> params = new HashMap<>();
	params.put("db", "mongo");
	params.put("key", url);
	params.put("fetchTime", fetchTime);
	eventMessage.setParams(params);
	return eventMessage;
  }

  private void closeChannelAndConnection(@Nullable final Channel channel, final boolean swallowErrors) {
	
	if ( channel == null ) {
		return;
	}
	
	try {
		final Connection connection = channel.getConnection();
		if (channel.isOpen()) {
			channel.close();
		}
		if ( connection != null && connection.isOpen() ) {
			connection.close();
		}
	} catch (IOException | TimeoutException e) {
		final String errMsg = "failed closing connection or channel";
		LOG.error(errMsg, e);
		if ( ! swallowErrors ) {
			throw new RuntimeException(errMsg, e);
		}
	}
  }

  private DataStore<String, CrawledWebPage> createDataStore(String schema) {
	DataStore<String, CrawledWebPage> dataStore = null;
    try {
	    Class<? extends DataStore<String, CrawledWebPage>> dataStoreClass = StorageUtils.getDataStoreClass(conf);
	    dataStore = DataStoreFactory.createDataStore(dataStoreClass, String.class, CrawledWebPage.class, conf, schema);
    } catch (ClassNotFoundException e) {
    	final String errMsg = "failed initialize datastore";
		LOG.error(errMsg, e);
    	throw new RuntimeException(errMsg, e);
    } catch (GoraException e) {
    	final String errMsg = "failed initialize datastore";
		LOG.error(errMsg, e);
    	throw new RuntimeException(errMsg, e);
    }
	return dataStore;
  }

  private void copyPage(String url, WebPage page, DataStore<String, CrawledWebPage> dataStore, CrawledWebPage crawledWebPage) {
	final WebPage webPage = new WebPage();
    webPage.setBaseUrl(page.getBaseUrl());
    webPage.setBatchId(page.getBatchId());
    webPage.setContent(page.getContent());
    webPage.setContentType(page.getContentType());
    webPage.setFetchInterval(page.getFetchInterval());
    webPage.setFetchTime(page.getFetchTime());
    webPage.setHeaders(page.getHeaders());
    webPage.setInlinks(page.getInlinks());
    webPage.setMarkers(page.getMarkers());
    webPage.setMetadata(page.getMetadata());
    webPage.setModifiedTime(page.getModifiedTime());
    webPage.setOutlinks(page.getOutlinks());
    webPage.setParseStatus(page.getParseStatus());
    webPage.setPrevFetchTime(page.getPrevFetchTime());
    webPage.setPrevModifiedTime(page.getPrevModifiedTime());
    webPage.setPrevSignature(page.getPrevSignature());
    webPage.setProtocolStatus(page.getProtocolStatus());
    webPage.setReprUrl(page.getReprUrl());
    webPage.setRetriesSinceFetch(page.getRetriesSinceFetch());
    webPage.setScore(page.getScore());
    webPage.setSignature(page.getSignature());
    webPage.setStatus(page.getStatus());
    webPage.setText(page.getText());
    webPage.setTitle(page.getTitle());
    
    final List<WebPage> webPageData = crawledWebPage.getWebPageData();
    webPageData.add(0, webPage);
    
 // setting back webPageData marks this field as dirty (== should be flushed to DB), otherwise data will be lost
    crawledWebPage.setWebPageData(webPageData);
    dataStore.put(url, crawledWebPage);
    
    dataStore.flush();
  }

  @Override
  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  @Override
  public Configuration getConf() {
    return this.conf;
  }

  /**
   * Gets all the fields for a given {@link WebPage} Many datastores need to
   * setup the mapreduce job by specifying the fields needed. All extensions
   * that work on WebPage are able to specify what fields they need.
   */
  @Override
  public Collection<Field> getFields() {
    return FIELDS;
  }
}

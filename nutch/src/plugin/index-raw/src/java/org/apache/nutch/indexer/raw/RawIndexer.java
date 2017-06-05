package org.apache.nutch.indexer.raw;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.storage.WebPage.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public class RawIndexer implements IndexingFilter {
  private Configuration conf;

  public static final Logger LOG = LoggerFactory.getLogger(RawIndexer.class);
  
  private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

  static {
    FIELDS.add(WebPage.Field.CONTENT);
  }

  public NutchDocument filter(NutchDocument doc, String url, WebPage page)
      throws IndexingException {

    // just in case
    if (doc == null)
      return doc;

    final ByteBuffer contentBuf = page.getContent();
    if (contentBuf == null) {
    	LOG.warn( "page.getContent() is null. url = " + url );
		return doc;
	}
    final int remaining = contentBuf.remaining();
    if ( remaining > Integer.MAX_VALUE ) {
    	return doc;
    }
    
	final byte[] contentBytes = new byte[remaining];
	contentBuf.get(contentBytes);
    
    final String contentString = new String(contentBytes, Charsets.UTF_8);
    doc.add("raw", contentString);    

    return doc;
  }

  public void setConf(Configuration conf) {
    this.conf = conf;
  }

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

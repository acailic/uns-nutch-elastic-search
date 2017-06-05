package ftn.e2.udd.websearch.model;

import org.elasticsearch.common.text.Text;

/**
 *
 *  Nutch mapping
 *
 *  @a.ilic
 */

public class WebPage  {
    private String id;
    private String url;
    private String title;
    private String content;
    private String tstamp;
    private String host;
    private Text[] highlighted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTstamp() {
        return tstamp;
    }

    public void setTstamp(String tstamp) {
        this.tstamp = tstamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Text[] getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Text[] highlighted) {
        this.highlighted = highlighted;
    }
}

package ftn.e2.udd.websearch.api.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jungj on 2014.12.08..
 */
public class ApiSearchRequest {

    private String query;

    private String highlightStartTag = "";

    private String highlightEndTag = "";

    private QueryType queryType = QueryType.FUZZY;

    private List<QueryParam> queryParams = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getHighlightStartTag() {
        return highlightStartTag;
    }

    public void setHighlightStartTag(String highlightStartTag) {
        this.highlightStartTag = highlightStartTag;
    }

    public String getHighlightEndTag() {
        return highlightEndTag;
    }

    public void setHighlightEndTag(String highlightEndTag) {
        this.highlightEndTag = highlightEndTag;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public List<QueryParam> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<QueryParam> queryParams) {
        this.queryParams = queryParams;
    }
}

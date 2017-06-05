package ftn.e2.udd.websearch.android.service;

import android.net.http.AndroidHttpClient;

import org.apache.http.client.HttpClient;

import ftn.e2.udd.websearch.android.dto.SearchResult;
import ftn.e2.udd.websearch.android.util.SearchUtils;

public class SearchService {

    private static SearchService instance;

    private HttpClient httpClient;

    private SearchService() {
        httpClient = AndroidHttpClient.newInstance(SearchService.class.getSimpleName());
    }

    public static SearchService instance() {
        if (instance == null) {
            instance = new SearchService();
        }
        return instance;
    }

    public SearchResult getResults(String query, int pageNr) {
        return SearchUtils.convertSearchResult(post(query, pageNr));
    }

    private String post(String query, int pageNr) {
        try {
            return SearchUtils.convertHttpResponse(httpClient.execute(SearchUtils.buildPost(SearchUtils.buildUrl(pageNr), query)));
        } catch (Exception e) {
            return SearchUtils.EMPTY_STRING;
        }
    }

}

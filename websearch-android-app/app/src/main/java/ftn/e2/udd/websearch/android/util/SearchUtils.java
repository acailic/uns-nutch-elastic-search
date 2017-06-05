package ftn.e2.udd.websearch.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ftn.e2.udd.websearch.android.R;
import ftn.e2.udd.websearch.android.dto.Entry;
import ftn.e2.udd.websearch.android.dto.SearchResult;
import ftn.e2.udd.websearch.android.settings.ApplicationConfiguration;

public class SearchUtils {

    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-type";
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    public static final String APPLICATION_JSON = "application/json";
    public static final String SEARCH_QUERY_KEY = "query";
    public static final String SEARCH_HIGHLIGHT_START_TAG_KEY = "highlightStartTag";
    public static final String SEARCH_HIGHLIGHT_START_TAG_VALUE = "<strong>";
    public static final String SEARCH_HIGHLIGHT_END_TAG_KEY = "highlightEndTag";
    public static final String SEARCH_HIGHLIGHT_END_TAG_VALUE = "</strong>";
    public static final String URL_PATH_SEPARATOR = "/";
    public static final String NEW_LINE = "\n";
    public static final String EMPTY_STRING = "";

    private SearchUtils() { }

    public static int calculateNumberOfPages(int totalNumberOfResults) {
        return (int) Math.ceil((double) totalNumberOfResults / Double.parseDouble(ApplicationConfiguration.instance().getResultsPerPage()));
    }

    public static boolean isBlank(String s) {
        return  (s == null || s.trim().length() == 0);
    }

    public static SearchResult convertSearchResult(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            SearchResult searchResult = new SearchResult().withTotalNumberOfResults(jsonObject.getInt("totalNumberOfResults")).withPageNumber(jsonObject.getInt("pageNumber"));
            List<Entry> entries = new ArrayList<>();
            JSONArray jsonEntries = jsonObject.getJSONArray("entries");
            for (int i = 0; i < jsonEntries.length(); i++) {
                JSONObject jsonEntry = jsonEntries.getJSONObject(i);
                entries.add(new Entry().withTitle(SearchUtils.isBlank(jsonEntry.getString("title")) ? "Untitled" : jsonEntry.getString("title").trim()).withUrl(jsonEntry.getString("link")).withDescription(jsonEntry.getString("description")));
            }
            return searchResult.withEntries(entries);
        } catch (NullPointerException | JSONException e) {
            return new SearchResult().withErrorOccurred(true);
        }
    }

    public static HttpPost buildPost(String url, String query) throws JSONException, UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        post.addHeader(HTTP_HEADER_CONTENT_TYPE, APPLICATION_JSON);
        post.addHeader(HTTP_HEADER_ACCEPT, APPLICATION_JSON);
        JSONObject postDataJson = new JSONObject();
        postDataJson.put(SEARCH_QUERY_KEY, CyrillicToLatinConverter.cir2lat(query));
        postDataJson.put(SEARCH_HIGHLIGHT_START_TAG_KEY, SEARCH_HIGHLIGHT_START_TAG_VALUE);
        postDataJson.put(SEARCH_HIGHLIGHT_END_TAG_KEY, SEARCH_HIGHLIGHT_END_TAG_VALUE);
        post.setEntity(new StringEntity(postDataJson.toString()));
        return post;
    }

    public static String convertHttpResponse(HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(NEW_LINE);
        }
        response.getEntity().consumeContent();
        return sb.toString();
    }

    public static String buildUrl(int pageNr) {
        return ApplicationConfiguration.instance().getServiceUrl() + ApplicationConfiguration.instance().getServicePath() + SearchUtils.URL_PATH_SEPARATOR + ApplicationConfiguration.instance().getResultsPerPage() + SearchUtils.URL_PATH_SEPARATOR + pageNr;
    }


    public static void showAboutDialog(Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.about_title).setMessage(R.string.about_message).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
    }

}

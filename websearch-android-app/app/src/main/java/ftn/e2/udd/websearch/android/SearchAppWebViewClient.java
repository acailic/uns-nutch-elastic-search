package ftn.e2.udd.websearch.android;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SearchAppWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

}

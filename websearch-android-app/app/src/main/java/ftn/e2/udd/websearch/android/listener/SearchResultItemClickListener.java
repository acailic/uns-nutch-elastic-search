package ftn.e2.udd.websearch.android.listener;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;

import ftn.e2.udd.websearch.android.MainActivity;
import ftn.e2.udd.websearch.android.R;
import ftn.e2.udd.websearch.android.SearchAppWebViewClient;
import ftn.e2.udd.websearch.android.SearchResultAdapter;
import ftn.e2.udd.websearch.android.settings.ApplicationConfiguration;

public class SearchResultItemClickListener implements AdapterView.OnItemClickListener {

    private final MainActivity context;

    public SearchResultItemClickListener(MainActivity context) {
        this.context = context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchResultAdapter.ViewHolder holder = (SearchResultAdapter.ViewHolder) view.getTag();
        if (ApplicationConfiguration.instance().getOpenResultsInDefaultBrowser()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(holder.getLink()));
            context.startActivity(browserIntent);
        } else {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.result_view_layout);
            WebView webView = (WebView) dialog.findViewById(R.id.resultwebview);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new SearchAppWebViewClient());
            webView.loadUrl(holder.getLink());
            Button closeBtn = (Button) dialog.findViewById(R.id.result_button_close);
            closeBtn.setOnClickListener(new SearchResultCloseListener((SearchView) context.findViewById(R.id.searchView), dialog));
            dialog.show();
        }
    }
}

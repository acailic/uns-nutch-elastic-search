package ftn.e2.udd.websearch.android.listener;

import android.app.Dialog;
import android.view.View;
import android.widget.SearchView;

public class SearchResultCloseListener implements View.OnClickListener {

    private final SearchView searchView;

    private final Dialog dialog;

    public SearchResultCloseListener(SearchView searchView, Dialog dialog) {
        this.searchView = searchView;
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        searchView.clearFocus();
    }
}

package ftn.e2.udd.websearch.android.listener;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import ftn.e2.udd.websearch.android.MainActivity;
import ftn.e2.udd.websearch.android.R;
import ftn.e2.udd.websearch.android.service.SearchTask;

public class SearchListener implements SearchView.OnQueryTextListener {

    private final MainActivity context;

    public SearchListener(MainActivity context) {
        this.context = context;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        new SearchTask(context).execute(s, "0");
        SearchView searchView = (SearchView)context.findViewById(R.id.searchView);
        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

}

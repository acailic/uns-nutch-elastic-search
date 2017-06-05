package ftn.e2.udd.websearch.android.listener;

import android.view.View;
import android.widget.SearchView;

import ftn.e2.udd.websearch.android.MainActivity;
import ftn.e2.udd.websearch.android.R;
import ftn.e2.udd.websearch.android.service.SearchTask;

public class GoToPageListener implements View.OnClickListener {

    private final MainActivity context;

    private int page;

    public GoToPageListener(MainActivity context) {
        this.context = context;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void onClick(View v) {
        new SearchTask(context).execute(((SearchView)context.findViewById(R.id.searchView)).getQuery().toString(), Integer.toString(page));
    }
}

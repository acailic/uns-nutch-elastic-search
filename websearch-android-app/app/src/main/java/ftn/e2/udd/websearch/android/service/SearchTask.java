package ftn.e2.udd.websearch.android.service;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ftn.e2.udd.websearch.android.MainActivity;
import ftn.e2.udd.websearch.android.R;
import ftn.e2.udd.websearch.android.SearchResultAdapter;
import ftn.e2.udd.websearch.android.dto.SearchResult;
import ftn.e2.udd.websearch.android.util.SearchUtils;

public class SearchTask extends AsyncTask<String, Void, SearchResult> {

    private final MainActivity context;
    private final ProgressDialog progress;

    public SearchTask(MainActivity context) {
        super();
        this.context = context;
        progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.search_in_progress));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    @Override
    protected SearchResult doInBackground(String... params) {
        return SearchService.instance().getResults(params[0], Integer.parseInt(params[1]));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.show();
    }

    @Override
    protected void onPostExecute(final SearchResult searchResult) {
        super.onPostExecute(searchResult);
        progress.dismiss();
        ListView listView = (ListView) context.findViewById(R.id.listView);
        ((SearchResultAdapter)listView.getAdapter()).setSearchResult(searchResult);
        if (searchResult.getTotalNumberOfResults() > 0) {
            final int totalNumberOfPages = SearchUtils.calculateNumberOfPages(searchResult.getTotalNumberOfResults());
            final TextView nrOfResultsView = (TextView) context.findViewById(R.id.nrOfResults);
            nrOfResultsView.setText(context.getString(R.string.page_number).replace("#0", Integer.toString(searchResult.getPageNumber() + 1)).replace("#1", Integer.toString(totalNumberOfPages)));
            context.getGoToFirstPageListener().setPage(0);
            context.getGoToPrevPageListener().setPage(searchResult.getPageNumber() > 0 ? (searchResult.getPageNumber() - 1) : 0);
            context.getGoToNextPageListener().setPage(searchResult.getPageNumber() < (totalNumberOfPages - 1) ? searchResult.getPageNumber() + 1 : totalNumberOfPages - 1);
            context.getGoToLastPageListener().setPage(totalNumberOfPages - 1);
            if (searchResult.getPageNumber() == 0) {
                context.findViewById(R.id.firstButton).setEnabled(false);
                context.findViewById(R.id.prevButton).setEnabled(false);
            } else {
                context.findViewById(R.id.firstButton).setEnabled(true);
                context.findViewById(R.id.prevButton).setEnabled(true);
            }
            if (searchResult.getPageNumber() == (totalNumberOfPages - 1)) {
                context.findViewById(R.id.nextButton).setEnabled(false);
                context.findViewById(R.id.lastButton).setEnabled(false);
            } else {
                context.findViewById(R.id.nextButton).setEnabled(true);
                context.findViewById(R.id.lastButton).setEnabled(true);
            }
            context.findViewById(R.id.navigationLayout).setVisibility(View.VISIBLE);
            listView.smoothScrollToPosition(0);
        } else {
            if (!searchResult.isErrorOccurred()) {
                Toast.makeText(context, R.string.no_search_results, Toast.LENGTH_LONG).show();
            }
            context.findViewById(R.id.navigationLayout).setVisibility(View.INVISIBLE);
        }
        if (searchResult.isErrorOccurred()) {
            Toast.makeText(context, R.string.error_during_search, Toast.LENGTH_LONG).show();
        }
    }
}
package ftn.e2.udd.websearch.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import ftn.e2.udd.websearch.android.dto.SearchResult;
import ftn.e2.udd.websearch.android.listener.GoToPageListener;
import ftn.e2.udd.websearch.android.listener.SearchListener;
import ftn.e2.udd.websearch.android.listener.SearchResultItemClickListener;
import ftn.e2.udd.websearch.android.settings.ApplicationConfiguration;
import ftn.e2.udd.websearch.android.settings.SettingsActivity;
import ftn.e2.udd.websearch.android.util.SearchUtils;


public class MainActivity extends ActionBarActivity {

    private GoToPageListener goToFirstPageListener;
    private GoToPageListener goToPrevPageListener;
    private GoToPageListener goToNextPageListener;
    private GoToPageListener goToLastPageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        ApplicationConfiguration.initialize(this);
        setContentView(R.layout.activity_main);

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchListener(this));
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new SearchResultAdapter(this, new SearchResult()));
        listView.setOnItemClickListener(new SearchResultItemClickListener(this));
        findViewById(R.id.navigationLayout).setVisibility(View.INVISIBLE);

        goToFirstPageListener = new GoToPageListener(this);
        goToPrevPageListener = new GoToPageListener(this);
        goToNextPageListener = new GoToPageListener(this);
        goToLastPageListener = new GoToPageListener(this);

        findViewById(R.id.firstButton).setOnClickListener(goToFirstPageListener);
        findViewById(R.id.prevButton).setOnClickListener(goToPrevPageListener);
        findViewById(R.id.nextButton).setOnClickListener(goToNextPageListener);
        findViewById(R.id.lastButton).setOnClickListener(goToLastPageListener);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_about) {
            SearchUtils.showAboutDialog(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public GoToPageListener getGoToFirstPageListener() {
        return goToFirstPageListener;
    }

    public GoToPageListener getGoToPrevPageListener() {
        return goToPrevPageListener;
    }

    public GoToPageListener getGoToNextPageListener() {
        return goToNextPageListener;
    }

    public GoToPageListener getGoToLastPageListener() {
        return goToLastPageListener;
    }
}

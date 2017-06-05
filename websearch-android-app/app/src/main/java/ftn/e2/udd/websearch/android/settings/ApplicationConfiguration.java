package ftn.e2.udd.websearch.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Holds the application configuration
 */
public class ApplicationConfiguration {

    private static final String TAG = "ApplicationConfiguration";

    private static ApplicationConfiguration instance = null;

    private static Context context = null;

    private String serviceUrl;
    private String servicePath;
    private String resultsPerPage;
    private String openResultsInDefaultBrowser;

    private ApplicationConfiguration(Context ctx) {
        ApplicationConfiguration.context = ctx;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(String resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public boolean getOpenResultsInDefaultBrowser() {
        return Boolean.parseBoolean(openResultsInDefaultBrowser);
    }

    public void setOpenResultsInDefaultBrowser(String openResultsInDefaultBrowser) {
        this.openResultsInDefaultBrowser = Boolean.valueOf(openResultsInDefaultBrowser).toString();
    }

    /**
     * Return the single instance of this class, creating it
     * if necessary. This method is thread-safe.
     */
    public static void initialize(Context ctx) {
        if (instance == null) {
            synchronized(ApplicationConfiguration.class) {
                if (instance == null) {
                    instance = new ApplicationConfiguration(ctx);
                    instance.readPrefs();
                }
            }
        }
    }

    public static ApplicationConfiguration instance() {
        return instance;
    }

    /**
     * Re-read all preferences (you never need to call this explicitly)
     */
    private void readPrefs() {
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            serviceUrl = sp.getString("serviceUrl", "http://localhost:8080");
            servicePath = sp.getString("servicePath", "/search");
            resultsPerPage = sp.getString("resultsPerPage", "10");
            openResultsInDefaultBrowser = sp.getString("openResultsInDefaultBrowser", Boolean.FALSE.toString());
        } catch (Exception e) {
            Log.e(TAG, "exception reading preferences: " + e, e);
        }
    }

    /**
     * Save preferences; you can call this from onPause()
     */
    public void savePrefs() {
        try {
            SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
            sp.putString("serviceUrl", serviceUrl);
            sp.putString("servicePath", servicePath);
            sp.putString("resultsPerPage", resultsPerPage);
            sp.putString("openResultsInDefaultBrowser", openResultsInDefaultBrowser);
            sp.apply();
        } catch (Exception e) {
            Log.e(TAG, "exception reading preferences: " + e, e);
        }
    }

    /**
     * Save preferences to a bundle. You don't really need to implement
     * this, but it can make start-up go faster.
     * Call this from onSaveInstanceState()
     */
    @SuppressWarnings("unused")
    public void onSaveInstanceState(Bundle state) {
        state.putString("serviceUrl", serviceUrl);
        state.putString("servicePath", servicePath);
        state.putString("resultsPerPage", resultsPerPage);
        state.putString("openResultsInDefaultBrowser", openResultsInDefaultBrowser);
    }

    /**
     * Recall preferences from a bundle. You don't really need to implement
     * this, but it can make start-up go faster.
     * Call this from onCreate()
     */
    @SuppressWarnings("unused")
    public void restoreInstanceState(Bundle state) {
        serviceUrl = state.getString("serviceUrl");
        servicePath = state.getString("servicePath");
        resultsPerPage = state.getString("resultsPerPage");
        openResultsInDefaultBrowser = state.getString("openResultsInDefaultBrowser");
    }


}

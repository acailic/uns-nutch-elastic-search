package ftn.e2.udd.websearch.android.listener;

import android.preference.Preference;

import ftn.e2.udd.websearch.android.settings.ApplicationConfiguration;

public class OpenResultInPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ApplicationConfiguration.instance().setOpenResultsInDefaultBrowser(newValue.toString());
        return true;
    }
}

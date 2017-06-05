package ftn.e2.udd.websearch.android.settings;

import android.os.Bundle;
import android.preference.*;

import ftn.e2.udd.websearch.android.R;
import ftn.e2.udd.websearch.android.listener.OpenResultInPreferenceChangeListener;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_layout);
        getPreferenceScreen().findPreference("openResultInDefaultBrowser").setDefaultValue(ApplicationConfiguration.instance().getOpenResultsInDefaultBrowser());
        getPreferenceScreen().findPreference("openResultInDefaultBrowser").setOnPreferenceChangeListener(new OpenResultInPreferenceChangeListener());
    }
}

package ftn.e2.udd.websearch.android.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import ftn.e2.udd.websearch.android.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationConfiguration.instance().savePrefs();
        Toast.makeText(getApplicationContext(), R.string.settings_updated, Toast.LENGTH_LONG).show();
    }

}

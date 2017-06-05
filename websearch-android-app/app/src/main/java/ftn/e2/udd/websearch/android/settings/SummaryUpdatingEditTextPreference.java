package ftn.e2.udd.websearch.android.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;

public class SummaryUpdatingEditTextPreference extends EditTextPreference {

    public SummaryUpdatingEditTextPreference(Context context) {
        super(context);
        setText(getSetting(getKey()));
    }

    public SummaryUpdatingEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setText(getSetting(getKey()));
    }

    public SummaryUpdatingEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setText(getSetting(getKey()));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        setSummary(getSummary());
        updateSetting(getKey());
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }

    private String getSetting(String key) {
        switch (key) {
            case "searchServiceUrl":
                return ApplicationConfiguration.instance().getServiceUrl();
            case "searchServicePath":
                return ApplicationConfiguration.instance().getServicePath();
            case "resultsPerPage":
                return ApplicationConfiguration.instance().getResultsPerPage();
            default:
                Log.w("SummaryUpdatingEditTextPreference", "Unknown settings key: " + key);
                return "";
        }
    }

    private void updateSetting(String key) {
        switch (key) {
            case "searchServiceUrl":
                ApplicationConfiguration.instance().setServiceUrl(this.getText());
                break;
            case "searchServicePath":
                ApplicationConfiguration.instance().setServicePath(this.getText());
                break;
            case "resultsPerPage":
                ApplicationConfiguration.instance().setResultsPerPage(this.getText());
                break;
            default:
                Log.w("SummaryUpdatingEditTextPreference", "Unknown settings key: " + key);
        }
    }
}

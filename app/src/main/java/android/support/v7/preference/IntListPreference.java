package android.support.v7.preference;

import android.content.Context;
import android.util.AttributeSet;

public class IntListPreference extends ListPreference {

    public IntListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean persistString(String value) {
        return persistInt(Integer.parseInt(value));
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        int defaultIntReturnValue;

        if (defaultReturnValue == null) {
            defaultIntReturnValue = 0;
        } else {
            defaultIntReturnValue = Integer.parseInt(defaultReturnValue);
        }

        return Integer.toString(getPersistedInt(defaultIntReturnValue));
    }

    @Override
    public CharSequence getSummary() {
        CharSequence summary = super.getSummary();

        if (summary == null || summary.length() == 0) {
            return getEntry();
        } else {
            return summary;
        }
    }
}

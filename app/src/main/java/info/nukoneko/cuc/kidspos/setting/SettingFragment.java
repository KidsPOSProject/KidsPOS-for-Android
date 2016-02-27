package info.nukoneko.cuc.kidspos.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import info.nukoneko.cuc.kidspos.R;

/**
 * Created by atsumi on 2016/02/20.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference p = getPreferenceScreen().findPreference(key);
//        if(p instanceof EditTextPreference)
//        {
//            setEdited(!((EditTextPreference) p).getText().equals(p.getSummary()));
//            setPrefData(key, ((EditTextPreference) p).getText());
//        }else if(p instanceof ListPreference){
//            setEdited(!((ListPreference) p).getValue().equals(p.getSummary()));
//            setPrefData(key, ((ListPreference) p).getValue());
//        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if(v != null) {
            ListView lv = (ListView) v.findViewById(android.R.id.list);
            lv.setPadding(0, 0, 0, 0);
        }
        return v;
    }
}

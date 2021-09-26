package com.test.rnids.ui.support;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

import android.preference.PreferenceManager;

import android.preference.ListPreference;

import com.test.rnids.R;


public class PreferenceKlasa extends PreferenceActivity {
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.root_preferences);
        loadTheme();
    }

    private void loadTheme(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean checkNight = sp.getBoolean("NIGHT",false);

        if(checkNight) getListView().setBackgroundColor(Color.parseColor("#222222"));
        else getListView().setBackgroundColor(Color.parseColor("#ffffff"));

        CheckBoxPreference checkNightInstant = (CheckBoxPreference) findPreference("NIGHT");
        checkNightInstant.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.preference.Preference prefs, Object obj) {
                boolean yes = (boolean) obj;
                System.out.println(yes+ " rmsdasdasd");

                if(yes) getListView().setBackgroundColor(Color.parseColor("#222222"));
                else getListView().setBackgroundColor(Color.parseColor("#ffffff"));

                return true;
            }
        });

        ListPreference listPreference = (ListPreference)findPreference("choose_language");

        String orient = sp.getString("choose_language", String.valueOf(false));
        if("1".equals(orient))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        else if("2".equals(orient))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        else if("3".equals(orient))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        listPreference.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.preference.Preference preference, Object o) {
                String items = (String)o;
                if (preference.getKey().equals("choose_language")){
                    switch (items){
                        case"1":
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
                            break;
                        case "2":
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            break;
                        case"3":
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            break;


                    }
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
         loadTheme();
        super.onResume();
    }
}

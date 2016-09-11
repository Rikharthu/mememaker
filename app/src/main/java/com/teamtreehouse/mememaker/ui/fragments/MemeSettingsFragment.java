package com.teamtreehouse.mememaker.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamtreehouse.mememaker.R;


public class MemeSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflates the given XML resource
        // and adds the preference hierarchy to the current preference hierarchy (list).
        // i.o. automatically construct from XML file
        addPreferencesFromResource(R.xml.preferences);


        // Dynamically add Preferences
        //fetch the item where you wish to insert the CheckBoxPreference, in this case a PreferenceCategory with key "targetCategory"
        PreferenceCategory targetCategory = (PreferenceCategory)findPreference("pref_key_storage_settings");

        //create one check box for each setting you need
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
        //make sure each key is unique
        checkBoxPreference.setKey("keyName");
        checkBoxPreference.setChecked(true);
        // add Preference to category
        targetCategory.addPreference(checkBoxPreference);
    }


}

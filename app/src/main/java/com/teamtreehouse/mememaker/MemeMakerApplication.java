package com.teamtreehouse.mememaker;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.teamtreehouse.mememaker.utils.FileUtilities;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class MemeMakerApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Preferences",PreferenceManager.getDefaultSharedPreferences(this).getAll().toString());
        FileUtilities.saveAssetImage(this, "dogmess.jpg");
        FileUtilities.saveAssetImage(this, "excitedcat.jpg");
        FileUtilities.saveAssetImage(this, "guiltypup.jpg");
        FileUtilities.saveAssetImage(this, "Insanity-Wolf.jpg");

        // Sets the default values from an XML preference file by reading the values defined by each Preference
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // false - ensures that default values are set for preferences that aren't initialized
        // thus making sure that it will not override user parameters set before
        // pass true to reset all app settings
    }
}

package com.teamtreehouse.mememaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.StorageType;

/**
 * Helper class that manages our preferences
 * This way we do not have to implement these method
 * in every class that uses preferencs.
 * Moreover, we will not have to deal with KEY names
 */
public class MemeMakerApplicationSettings {
    SharedPreferences mSharedPreferences;

    public MemeMakerApplicationSettings(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /** Returns preferred storage type.
     * By default use Internal storage */
    public String getStoragePreference() {
        // by default use Internal storage
        return mSharedPreferences.getString("Storage", StorageType.INTERNAL);
    }

    /** Set preferred storage type */
    public void setSharedPreference(String storageType) {
        mSharedPreferences
                .edit()
                .putString("Storage", storageType)
                .apply();
        // we could use .commit(), which is sycnrhonous (.apply() is asynchronous)
    }
}

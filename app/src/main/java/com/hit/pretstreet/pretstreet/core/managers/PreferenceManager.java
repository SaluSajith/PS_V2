package com.hit.pretstreet.pretstreet.core.managers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;

import java.util.HashMap;

/**
 * Created by User on 6/20/2017.
 */

public class PreferenceManager {
    public static PreferenceManager preferenceManager;
    private SharedPreferencesHelper mPreferencesHelper;

    public static PreferenceManager getInstance() {
        if (preferenceManager == null) {
            return preferenceManager = new PreferenceManager();
        }
        return preferenceManager;
    }

    public SharedPreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public void setPreferencesHelper(Context context) {
        this.mPreferencesHelper = new SharedPreferencesHelper(context);
    }
}

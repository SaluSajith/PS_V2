package com.hit.pretstreet.pretstreet.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import java.util.HashMap;

/**
 * Created by User on 6/20/2017.
 */

public class SharedPreferencesHelper {

    Context mContext;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SessionPref";
    public static final String IS_LOGIN = "session";

    public static final String KEY_REG_ID = "regid";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_FNAME = "fname";
    public static final String KEY_LNAME = "lname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_SOCIALID = "social_id";
    public static final String KEY_SOCIALTYPE = "social_type";
    public static final String KEY_PIC = "profile_pic";

    public SharedPreferencesHelper(Context mContext) {
        if (mContext != null)
            this.mContext = mContext;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void putInt(String key, int value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public void putBoolean(String key, boolean val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, val);
        edit.apply();
    }

    public void putString(String key, String val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, val);
        edit.apply();
    }

    public void putFloat(String key, float val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putFloat(key, val);
        edit.apply();
    }

    public void putLong(String key, long val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(key, val);
        edit.apply();
    }

    public long getLong(String key, long _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return preferences.getLong(key, _default);
    }

    public float getFloat(String key, float _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return preferences.getFloat(key, _default);
    }

    public String getString(String key, String _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return preferences.getString(key, _default);
    }

    public int getInt(String key, int _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return preferences.getInt(key, _default);
    }

    public boolean getBoolean(String key, boolean _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(key, _default);
    }

    public void clearPreferences() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        preferences.edit().clear().apply();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(LoginSession loginSession) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_REG_ID, loginSession.getRegid());
        editor.putString(KEY_MOBILE, loginSession.getMobile());
        editor.putString(KEY_FNAME, loginSession.getFname());
        editor.putString(KEY_LNAME, loginSession.getLname());
        editor.putString(KEY_EMAIL, loginSession.getEmail());
        editor.putString(KEY_GENDER, loginSession.getGender());
        editor.putString(KEY_SOCIALID, loginSession.getSocial_id());
        editor.putString(KEY_SOCIALTYPE, loginSession.getSocial_type());
        editor.putString(KEY_PIC, loginSession.getProfile_pic());

        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
        }
    }

    /**
     * Get stored session data
     * */
    public LoginSession getUserDetails(){
       LoginSession userDetails = new LoginSession();
        userDetails.setRegid(pref.getString(KEY_REG_ID, null));
        userDetails.setMobile(pref.getString(KEY_MOBILE, null));
        userDetails.setFname(pref.getString(KEY_FNAME, null));
        userDetails.setLname(pref.getString(KEY_LNAME, null));
        userDetails.setEmail(pref.getString(KEY_EMAIL, null));
        userDetails.setGender(pref.getString(KEY_GENDER, null));
        userDetails.setSocial_id(pref.getString(KEY_SOCIALID, null));
        userDetails.setSocial_type(pref.getString(KEY_SOCIALTYPE, null));
        userDetails.setProfile_pic(pref.getString(KEY_PIC, null));

        return userDetails;
    }

    /**
     * Clear session details
     * */
    public void logoutUser() {
        editor.putBoolean(IS_LOGIN, false);
        editor.remove(KEY_REG_ID);
        editor.remove(KEY_MOBILE);
        editor.remove(KEY_FNAME);
        editor.remove(KEY_LNAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_SOCIALID);
        editor.remove(KEY_SOCIALTYPE);
        editor.remove(KEY_PIC);
//        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}

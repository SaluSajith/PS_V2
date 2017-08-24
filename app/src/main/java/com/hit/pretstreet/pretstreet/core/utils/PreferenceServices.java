package com.hit.pretstreet.pretstreet.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 20/07/2017.
 */
public class PreferenceServices {
    public static final String LOGIN_STATUS = "loginStatus";
    public static final String LOGIN_STATUS_SUCCESS = "1";
    public static final String LOGIN_STATUS_FAIL = "0";
    public static final String DEFAULT_LOGIN_STATUS = "-1";

    public static final String USERID = "USERID";
    public static final String USERON = "USERON";
    public static final String USERNAME = "USERNAME";
    public static final String CURLOCATION = "LOCATION";
    public static final String LATITUTE = "LATITUTE";
    public static final String LONGITUTE = "LONGITUTE";
    public static final String LOGINTYPE = "LOGINTYPE";
    public static final String DEVICESIZE = "DEVICESIZE";


    public static final String HOMEMAINCATLIST = "HOMEMAINCATLIST";
    public static final String SUBMAINCATLIST = "SUBMAINCATLIST";
    public static final String TRENDINGCATLIST = "TRENDINGCATLIST";
    public static final String THESHOPSCATLIST = "THESHOPSCATLIST";
    public static final String THEDESIGNERSCATLIST = "THEDESIGNERSCATLIST";
    public static final String THEJEWEKKARYTRENDINGCATLIST = "THEJEWEKKARYTRENDINGCATLIST";
    public static final String THERETAILSCATLIST = "THERETAILSCATLIST";


    public static final String SPLASHSCREEN = "SPLASHSCREEN";
    public static final String HEADERIMAGE = "HEADERIMAGE";
    public static final String BASEIMAGE = "BASEIMAGE";

    public static String SHARE_QUERYPARAM = "SHARE_QUERYPARAM";
    public static String ID_QUERYPARAM = "ID_QUERYPARAM";

    private static final String PREFS_NAME = "FashionSharedPref";
    private static PreferenceServices mSingleton = new PreferenceServices();
    private static Context mContext;

    private PreferenceServices() {
    }

    public static PreferenceServices instance() {
        return mSingleton;
    }

    public static PreferenceServices getInstance() {
        return mSingleton;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getShareQueryparam() {
        return getPrefs().getString(SHARE_QUERYPARAM, "");
    }

    public void setShareQueryparam(String shareQueryparam) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(SHARE_QUERYPARAM, shareQueryparam);
        editor.commit();
    }

    public String getIdQueryparam() {
        return getPrefs().getString(ID_QUERYPARAM, "");
    }

    public void setIdQueryparam(String idQueryparam) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(ID_QUERYPARAM, idQueryparam);
        editor.commit();
    }

    public String geUsertId() {
        return getPrefs().getString(USERID, "");
    }

    public void saveUserId(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(USERID, id);
        editor.commit();
    }
    public String geUsertName() {
        return getPrefs().getString(USERNAME, "");
    }

    public void saveUserName(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(USERNAME, id);
        editor.commit();
    }

    public String getCurrentLocation() {
        return getPrefs().getString(CURLOCATION, "No Location Found");
    }

    public void saveCurrentLocation(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(CURLOCATION, id);
        editor.commit();
    }

    public String getLatitute() {
        return getPrefs().getString(LATITUTE, "");
    }

    public void saveLatitute(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(LATITUTE, id);
        editor.commit();
    }

    public String getLongitute() {
        return getPrefs().getString(LONGITUTE, "");
    }

    public void saveLongitute(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(LONGITUTE, id);
        editor.commit();
    }

    public String getLoginType() {
        return getPrefs().getString(LOGINTYPE, "");
    }

    public void saveLoginType(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(LOGINTYPE, id);
        editor.commit();
    }

    public int getDeviceSize() {
        return getPrefs().getInt(DEVICESIZE, 0);
    }

    public void saveDeviceSize(int id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putInt(DEVICESIZE, id);
        editor.commit();
    }

    public String getHomeMainCatList() {
        return getPrefs().getString(HOMEMAINCATLIST, "");
    }

    public void saveHomeMainCatList(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(HOMEMAINCATLIST, id);
        editor.commit();
    }

    public String getHomeSubCatList() {
        return getPrefs().getString(SUBMAINCATLIST, "");
    }

    public void saveHomeSubCatList(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(SUBMAINCATLIST, id);
        editor.commit();
    }

    public String getTrendingCatList() {
        return getPrefs().getString(TRENDINGCATLIST, "");
    }

    public void saveTrendingCatList(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(TRENDINGCATLIST, id);
        editor.commit();
    }

    public String getTheshopscatlist() {
        return getPrefs().getString(THESHOPSCATLIST, "");
    }

    public void saveTheshopscatlist(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(THESHOPSCATLIST, id);
        editor.commit();
    }

    public String getThedesignerscatlist() {
        return getPrefs().getString(THEDESIGNERSCATLIST, "");
    }

    public void saveThedesignerscatlist(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(THEDESIGNERSCATLIST, id);
        editor.commit();
    }

    public String getThejewekkarytrendingcatlist() {
        return getPrefs().getString(THEJEWEKKARYTRENDINGCATLIST, "");
    }

    public void saveThejewekkarytrendingcatlist(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(THEJEWEKKARYTRENDINGCATLIST, id);
        editor.commit();
    }

    public String getTHERETAILSCATLIST() {
        return getPrefs().getString(THERETAILSCATLIST, "");
    }

    public void saveTHERETAILSCATLIST(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(THERETAILSCATLIST, id);
        editor.commit();
    }

    public String getSplashScreen() {
        return getPrefs().getString(SPLASHSCREEN, "");
    }

    public void saveSplashScreen(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(SPLASHSCREEN, id);
        editor.commit();
    }

    public String getBaseImage() {
        return getPrefs().getString(BASEIMAGE, "");
    }

    public void saveBaseImage(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(BASEIMAGE, id);
        editor.commit();
    }

    public String getHeaderImage() {
        return getPrefs().getString(HEADERIMAGE, "");
    }

    public void saveHeaderImage(String id) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(HEADERIMAGE, id);
        editor.commit();
    }
}

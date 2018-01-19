package com.hit.pretstreet.pretstreet.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 20/07/2017.
 * Shared preference Constants with getters and setters
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
    public static final String LOCATIONTYPE = "LOCATIONTYPE";
    public static final String LATITUTE = "LATITUTE";
    public static final String LONGITUTE = "LONGITUTE";
    public static final String LOGINTYPE = "LOGINTYPE";
    public static final String DEVICESIZE = "DEVICESIZE";
    public static final String NOTIFCOUNT = "NOTIFCOUNT";


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
    public static String TYPE_QUERYPARAM = "TYPE_QUERYPARAM";
    public static String ID_QUERYPARAM = "ID_QUERYPARAM";
    public static String UTM_QUERYPARAM = "UTM_QUERYPARAM";

    public static String currentloc = "CURRENTLOCATION";
    public static String dropdownloc = "DROPDOWNLOC";

    private static final String PREFS_NAME = "FashionSharedPref";
    private static final String IS_FIRST_TIME_LAUNCH = "ISFIRSTTIMELAUNCH";
    private static PreferenceServices mSingleton = new PreferenceServices();
    private static Context mContext;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

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
        pref = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getTypeQueryparam() {
        return pref.getString(TYPE_QUERYPARAM, "");
    }

    public void setTypeQueryparam(String typeQueryparam) {
        editor.putString(TYPE_QUERYPARAM, typeQueryparam);
        editor.apply();
    }

    public String getShareQueryparam() {
        return pref.getString(SHARE_QUERYPARAM, "");
    }

    public void setShareQueryparam(String shareQueryparam) {
        editor.putString(SHARE_QUERYPARAM, shareQueryparam);
        editor.apply();
    }

    public String getIdQueryparam() {
        return pref.getString(ID_QUERYPARAM, "");
    }

    public void setIdQueryparam(String idQueryparam) {
        editor.putString(ID_QUERYPARAM, idQueryparam);
        editor.apply();
    }


    public String getUTMQueryparam() {
        return pref.getString(UTM_QUERYPARAM, "");
    }

    public void setUTMQueryparam(String idQueryparam) {
        editor.putString(UTM_QUERYPARAM, idQueryparam);
        editor.apply();
    }

    public int getNotifCOunt() {
        return pref.getInt(NOTIFCOUNT, 0);
    }

    public void updateNotif(int value) {
        editor.putInt(NOTIFCOUNT, value);
        editor.apply();
    }

    public String geUsertId() {
        return pref.getString(USERID, "");
    }

    public void saveUserId(String id) {
        editor.putString(USERID, id);
        editor.apply();
    }
    public String geUsertName() {
        return pref.getString(USERNAME, "");
    }

    public void saveUserName(String id) {
        editor.putString(USERNAME, id);
        editor.apply();
    }

    public String getCurrentLocation() {
        return pref.getString(CURLOCATION, "No Location Found");
    }

    public void saveCurrentLocation(String location) {
        editor.putString(CURLOCATION, location);
        editor.apply();
    }

    public boolean isAutoDetect(){
        return (pref.getString(LOCATIONTYPE, "").equals(currentloc));
    }

    public void saveLocationType(String type) {
        editor.putString(LOCATIONTYPE, type);
        editor.apply();
    }

    public String getLatitute() {
        return pref.getString(LATITUTE, "");
    }

    public void saveLatitute(String id) {
        editor.putString(LATITUTE, id);
        editor.apply();
    }

    public String getLongitute() {
        return pref.getString(LONGITUTE, "");
    }

    public void saveLongitute(String id) {
        editor.putString(LONGITUTE, id);
        editor.apply();
    }

    public String getLoginType() {
        return pref.getString(LOGINTYPE, "");
    }

    public void saveLoginType(String id) {
        editor.putString(LOGINTYPE, id);
        editor.commit();
    }

    public int getDeviceSize() {
        return pref.getInt(DEVICESIZE, 0);
    }

    public void saveDeviceSize(int id) {
        editor.putInt(DEVICESIZE, id);
        editor.apply();
    }

    public String getHomeMainCatList() {
        return pref.getString(HOMEMAINCATLIST, "");
    }

    public void saveHomeMainCatList(String id) {
        editor.putString(HOMEMAINCATLIST, id);
        editor.apply();
    }

    public String getHomeSubCatList() {
        return pref.getString(SUBMAINCATLIST, "");
    }

    public void saveHomeSubCatList(String id) {
        editor.putString(SUBMAINCATLIST, id);
        editor.apply();
    }

    public String getTrendingCatList() {
        return pref.getString(TRENDINGCATLIST, "");
    }

    public void saveTrendingCatList(String id) {
        editor.putString(TRENDINGCATLIST, id);
        editor.apply();
    }

    public String getTheshopscatlist() {
        return pref.getString(THESHOPSCATLIST, "");
    }

    public void saveTheshopscatlist(String id) {
        editor.putString(THESHOPSCATLIST, id);
        editor.apply();
    }

    public String getThedesignerscatlist() {
        return pref.getString(THEDESIGNERSCATLIST, "");
    }

    public void saveThedesignerscatlist(String id) {
        editor.putString(THEDESIGNERSCATLIST, id);
        editor.apply();
    }

    public String getThejewekkarytrendingcatlist() {
        return pref.getString(THEJEWEKKARYTRENDINGCATLIST, "");
    }

    public void saveThejewekkarytrendingcatlist(String id) {
        editor.putString(THEJEWEKKARYTRENDINGCATLIST, id);
        editor.apply();
    }

    public String getTHERETAILSCATLIST() {
        return pref.getString(THERETAILSCATLIST, "");
    }

    public void saveTHERETAILSCATLIST(String id) {
        editor.putString(THERETAILSCATLIST, id);
        editor.apply();
    }

    public String getSplashScreen() {
        return pref.getString(SPLASHSCREEN, "");
    }

    public void saveSplashScreen(String id) {
        editor.putString(SPLASHSCREEN, id);
        editor.apply();
    }

    public String getBaseImage() {
        return pref.getString(BASEIMAGE, "");
    }

    public void saveBaseImage(String id) {
        editor.putString(BASEIMAGE, id);
        editor.apply();
    }

    public String getHeaderImage() {
        return pref.getString(HEADERIMAGE, "");
    }

    public void saveHeaderImage(String id) {
        editor.putString(HEADERIMAGE, id);
        editor.apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}

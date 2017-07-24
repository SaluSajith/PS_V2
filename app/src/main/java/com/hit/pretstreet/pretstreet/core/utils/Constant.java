package com.hit.pretstreet.pretstreet.core.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by hit on 14/3/16.
 */
public class Constant {

    public static final String GOOGLE_API_KEY = "AIzaSyCAq0CP83saVHVc93LDs_m3xUdpFna0c2c";
    public static final String API_KEY_BROWSER = "AIzaSyApozNhHGX5MrTZr63wVRlTbBLo5QRptVA";

    //public static final String COMMON_URL = "http://52.77.174.143/admin/";
    public static final String COMMON_URL = "http://54.179.131.57/newpretstreetapp/index.php/mobileapp/";
    public static final String REGISTRATION_OTP_URL = COMMON_URL + "customer/customerregistergenerateOtp";
    public static final String REGISTRATION_URL = COMMON_URL + "customer/customerregister";
    public static final String LOGIN_OTP_URL = COMMON_URL + "customer/customerlogingenerateOtp";
    public static final String LOGIN_URL = COMMON_URL + "customer/customerloginbyMobile";
    public static final String SOCIAL_LOGIN_URL = COMMON_URL + "customer/customerloginbySocial";
    public static final String HOMEPAGE_URL = COMMON_URL + "homepage/homepage";

    //updated after 5/9/2017
    public static final String TRENDING_API = COMMON_URL + "index.php/trendingpage/mobileapp/";
    public static final String EXHIBITION_API = COMMON_URL + "index.php/exhibitionpage/mobileapp/";
    public static final String INDEX_PATH = COMMON_URL + "index.php/trendingpage/mobileapp/";

    public static final String API = "MAPcabpnPawmassmjrc";
    public static final String LIMIT = "10";
    public static final String TAG = "Pretstreetv2";
    public static final int TIMEOUT_LIMIT = 15000;

    //Pagetype constants
    public static final String HOMEPAGE = "1";
    public static final String SUBCATPAGE = "2";
    public static final String STORELISTINGPAGE = "3";
    public static final String STOREDETAILSPAGE = "5";
    public static final String MULTISTOREPAGE = "6";
    public static final String WEBPAGE = "7";
    public static final String TRENDINGPAGE = "8";
    public static final String ARTICLEPAGE = "9";
    public static final String EXHIBITIONPAGE = "10";
    public static final String EXARTICLEPAGE = "11";
    public static final String FOLLOWINGPAGE = "12";

}

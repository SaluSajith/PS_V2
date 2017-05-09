package com.hit.pretstreet.pretstreet;

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

    //public static final String COMMON_URL = "http://doctronics.co.in/fashionapp/";
    public static final String COMMON_URL = "http://52.77.174.143/admin/";
    public static final String REGISTRATION_URL = COMMON_URL + "customercreates.php?";
    public static final String LOGIN_URL = COMMON_URL + "customerlogin.php?";
    public static final String FASHION_API = COMMON_URL + "fashion_api.php?";
    public static final String TRENDING_API = COMMON_URL + "index.php/trendingpage/mobileapp/";
    public static final String API = "Mpjdfanknavfrnefjndjbf";
    public static final String LIMIT = "10";

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void hide_keyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static int notificationID = 0;
    public static int marketImageheight = 0;
    public static String tag_json_obj = "json_obj_req";
    public static String deviceType;
    public static Bitmap imageupload;

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
}

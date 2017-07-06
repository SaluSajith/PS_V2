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

    //public static final String COMMON_URL = "http://doctronics.co.in/fashionapp/";
    public static final String COMMON_URL = "http://52.77.174.143/admin/";
    public static final String REGISTRATION_URL = COMMON_URL + "customercreates.php?";
    public static final String LOGIN_URL = COMMON_URL + "customerlogin.php?";
    public static final String FASHION_API = COMMON_URL + "fashion_api.php?";

    //updated after 5/9/2017
    public static final String TRENDING_API = COMMON_URL + "index.php/trendingpage/mobileapp/";
    public static final String EXHIBITION_API = COMMON_URL + "index.php/exhibitionpage/mobileapp/";
    public static final String INDEX_PATH = COMMON_URL + "index.php/trendingpage/mobileapp/";

    public static final String API = "Mpjdfanknavfrnefjndjbf";
    public static final String LIMIT = "10";
    public static final String TAG = "Pretstreetv2";
    public static final int TIMEOUT_LIMIT = 15000;

    public static int notificationID = 0;
    public static int marketImageheight = 0;
    public static String subCategories = "{\n" +
            "    \"success\": true,\n" +
            "    \"parent_id\": \"109\",\n" +
            "    \"parent_image\": \"http:\\/\\/52.77.174.143\\/admin\\/media\\/catalog\\/category\\/fixed_category.png\",\n" +
            "    \"types\": [{\n" +
            "        \"category_id\": \"112\",\n" +
            "        \"name\": \"Bags\",\n" +
            "        \"image\": \"http:\\/\\/52.77.174.143\\/admin\\/media\\/catalog\\/category\\/bags_bg_1.png\"\n" +
            "    }, {\n" +
            "        \"category_id\": \"111\",\n" +
            "        \"name\": \"Shoes\",\n" +
            "        \"image\": \"http:\\/\\/52.77.174.143\\/admin\\/media\\/catalog\\/category\\/shoes_bg_1.png\"\n" +
            "    }, {\n" +
            "        \"category_id\": \"114\",\n" +
            "        \"name\": \"Accessories\",\n" +
            "        \"image\": \"http:\\/\\/52.77.174.143\\/admin\\/media\\/catalog\\/category\\/accessesories_bg_1.png\"\n" +
            "    }]\n" +
            "}";
    public static String deviceType = "1";
    public static Bitmap imageupload;

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
}

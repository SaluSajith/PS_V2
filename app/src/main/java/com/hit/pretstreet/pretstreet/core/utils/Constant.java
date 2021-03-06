package com.hit.pretstreet.pretstreet.core.utils;

import android.content.Context;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.sociallogin.TokenService;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 14/6/17.
 * Static values in the application
 */
public class Constant {

    public static final String GOOGLE_API_KEY = "AIzaSyCAq0CP83saVHVc93LDs_m3xUdpFna0c2c";
    public static final String API_KEY_BROWSER = "AIzaSyApozNhHGX5MrTZr63wVRlTbBLo5QRptVA";

    /** Main URL
     * From this URL we will get the BAse URL which should be attached in all the URLs*/
    public static final String CHECKIP_URL = "http://35.154.65.154/extrafiles/checkip.php";

    //public static final String COMMON_URL = "http://54.179.131.57/newpretstreetapp/index.php/mobileapp/";  //not working
    //public static final String COMMON_URL = "http://13.126.57.85/pretstreetvtwobeta/index.php/mobileapp/";   //test
    //public static final String COMMON_URL = "http://35.154.65.154/index.php/mobileapp/";

    public static final String COMMON_URL = "";
    public static final String REGISTRATION_OTP_URL = COMMON_URL + "customer/customerregistergenerateOtp";
    public static final String REGISTRATION_URL = COMMON_URL + "customer/customerregister";
    public static final String LOGIN_OTP_URL = COMMON_URL + "customer/customerlogingenerateOtp";
    public static final String LOGIN_URL = COMMON_URL + "customer/customerloginbyMobile";
    public static final String SOCIAL_LOGIN_URL = COMMON_URL + "customer/customerloginbySocial";
    public static final String HOMEPAGE_URL = COMMON_URL + "homepage/homepage";
    public static final String SUBCAT_URL = COMMON_URL + "category/getSubCategory";
    public static final String STORELISTING_URL = COMMON_URL + "store/storelisting/";
    public static final String UPDATEFOLLOWSTATUS_URL = COMMON_URL + "store/followStore/";
    public static final String STOREDETAILS_URL = COMMON_URL + "store/storedetails/";
    public static final String TRENDING_URL = COMMON_URL + "trending/listing/";
    public static final String GIVEAWAY_URL = COMMON_URL + "giveaway/listing/";
    public static final String TRENDINGLIKE_URL = COMMON_URL + "trending/trendinglike/";
    public static final String EXHIBITION_URL = COMMON_URL + "exhibition/listing/";
    public static final String EXHIBITIONLIKE_URL = COMMON_URL + "exhibition/exhibitionlike/";
    public static final String EXHIBITIONREGISTER_URL = COMMON_URL + "exhibition/exhibitionRegister/";
    public static final String EXHIBITIONREGISTEROTP_URL = COMMON_URL + "exhibition/customerregistergenerateOtp";
    public static final String EXHIBITIONARTICLE_URL = COMMON_URL +  "exhibition/article/";
    public static final String EXHIBITIONSEARCH_URL = COMMON_URL +  "exhibition/exhibitionSearch/";
    public static final String TRENDINGARTICLE_URL = COMMON_URL +  "trending/article/";
    public static final String GIVEAWAYARTICLE_URL = COMMON_URL +"giveaway/article/";
    public static final String MULTISTORE_URL = COMMON_URL + "store/multistorelisting";
    public static final String AUTOSEARCH_URL = COMMON_URL + "category/autoSearch";
    public static final String SEARCH_URL = COMMON_URL + "category/search";
    public static final String RECENTSEARCH_URL = COMMON_URL + "category/searchCustomerPage";
    public static final String FOLLOWING_URL = COMMON_URL + "customer/customerFollowingStores";
    public static final String FILTER_URL = COMMON_URL + "store/storelistingFilter/";
    public static final String CONTACTUS_URL = COMMON_URL + "customer/customerContactUs";
    public static final String FEEDBACK_URL = COMMON_URL + "customer/customerFeedback";
    public static final String ADDSTORE_URL = COMMON_URL + "customer/customerAddStore";
    public static final String BOOK_APPOINTMENT_URL = COMMON_URL + "customer/customerAppointment";
    public static final String REPORT_ERROR_URL = COMMON_URL + "customer/storeError";
    public static final String UPDATE_ACCOUNT_URL = COMMON_URL + "customer/customerprofileedit";
    public static final String UPDATEPASSWORD_ACCOUNT_URL = COMMON_URL + "customer/customerresetpassword";
    public static final String PRIVACYPOLICY_URL = COMMON_URL + "customer/staticpagePrivacyPolicy";
    public static final String TC_URL = COMMON_URL + "customer/staticpageTnC";
    public static final String ABOUT_URL = COMMON_URL + "customer/staticpageAbout";
    public static final String TRACK_URL = COMMON_URL + "customer/storeDetailsTracking";

    //UPDATED AFTER 5/9/2017

    public static final String INDEX_PATH = COMMON_URL + "store/trendingArticle";
    public static final String GETLOCATION_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true_or_false";

    public static final String API = "MAPcabpnPawmassmjrc";
    public static final String LIMIT = "20";
    public static final String LIMIT_S = "10";
    public static final String TAG = "Pretstreet";
    public static final int TIMEOUT_LIMIT = 10000;
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String PUSH_NOTIFICATION = "pushNotification";

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
    public static final String SEARCHPAGE = "14";
    public static final String FILTERPAGE = "15";
    public static final String SIGNUPPAGE = "16";
    public static final String GIVEAWAYPAGE = "18";
    public static final String GIVEAWAYARTICLEPAGE = "19";
    public static final String EXHIBITIONSEARCH = "20";

    //Clicktype constants
    public static final String HOMEPAGELINK = "1";
    public static final String CATEGORYLINK = "2";
    public static final String SUBCATLINK = "3";
    public static final String SHIPBUPROLINK = "5";
    public static final String SHOPBYMOODLINK = "6";
    public static final String BANNERLINK = "7";
    public static final String SLIDERLINK = "9";
    public static final String MALLSLINK = "10";
    public static final String STORELISTINGLINK = "11";
    public static final String STOREDETAILSLINK = "12";
    public static final String CALLLINK = "14";
    public static final String CALLEDLINK = "15";
    public static final String VIEWADDRESSLINK = "16";
    public static final String VIEWONMAPLINK = "17";
    public static final String ABOUTLINK = "18";
    public static final String TRENDINGLISTLINK = "19";
    public static final String TRENDARTICLELINK = "20";
    public static final String EXHIBITIONLINK = "21";
    public static final String EXHIBITIONARTICLELINK = "22";
    public static final String MULTILINK = "23";
    public static final String AUTOSEARCHLINK = "24";
    public static final String SEARCHLISTLINK = "25";
    public static final String FOLLOWLINK = "27";

    public static final String TRENDINGARTICLELIKELINK = "29";
    public static final String TRENDINGARTICLEUNLIKELINK = "30";
    public static final String EXLIKELINK = "31";
    public static final String EXUNLIKELINK = "32";
    public static final String SHARE = "33";
    public static final String EXINTERESTLINK = "34";
    public static final String EXNOTINTERESTEDLINK = "35";
    public static final String EXGOINGLINK = "36";
    public static final String EXNOTGOINGLINK = "37";
    public static final String NOTIFICATIONKEY = "38";
    public static final String EXARTICLEREGISTER = "39";
    public static final String INSTALLREFERRERKEY = "40";
    public static final String GIVEAWAYLISTLINK = "41";

    //Trape types
    public static final String TRAPE = "1";
    public static final String SLIDER = "2";
    public static final String SHOPBYPRO = "3";
    public static final String SHOPBYMOODS = "4";
    public static final String MALLS = "5";

    //Constants
    public static final String PRE_PAGE_KEY = "pagetype";
    public static final String ID_KEY = "id";
    public static final String URL_KEY = "url";
    public static final String CLICKTYPE_KEY = "clicktype";
    public static final String PARCEL_KEY = "data";
    public static final String POSITION_KEY = "position";
    public static final String SOCIALLOGIN = "social";
    public static final String NORMALLOGIN = "position";
    public static final int REQUEST_INVITE = 400;

    //Notification keys
    public static final String NOTIF_ID = "id";
    public static final String NOTIF_TITLE = "title";
    public static final String NOTIF_BODY = "body";
    public static final String NOTIF_SHARE = "share";
    public static final String NOTIF_IMAGE = "image";
    public static final String NOTIF_ACTION_RECIEVE = "RECEIVE_NOTIFICATION";

    //location leys
    public static final int REQUEST_APP_SETTINGS = 168;
    public final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;

    //Referral keys
    public static final String REFERAL_ID = "id";
    public static final String REFERAL_SHARE = "share";

    //Fragments
    public static final int ACCOUNT_FRAGMENT = 0;
    public static final int FOLLOWING_FRAGMENT = 1;
    public static final int ABOUT_FRAGMENT = 2;
    public static final int ADDSTORE_FRAGMENT = 3;
    public static final int CONTACTUS_FRAGMENT = 4;
    public static final int FEEDBACK_FRAGMENT = 5;
    public static final int ABOUTUS_FRAGMENT = 6;
    public static final int PRIVACY_FRAGMENT = 7;
    public static final int TERMS_FRAGMENT = 8;
    public static final int TRENDING_FRAGMENT = 10;
    public static final int EXHIBITION_FRAGMENT = 11;
    public static final int TRENDINGARTICLE_FRAGMENT = 12;
    public static final int EXHIBITION_DETAILS = 13;
    public static final int REFER_EARN_FRAGMENT = 14;
    public static final int NOTIFICATION_FRAGMENT = 15;
    public static final int GIVEAWAY_FRAGMENT = 16;
    public static final int CHANGEPASSWORD_FRAGMENT = 17;
    public static final int ABOUTDESIGNER_FRAGMENT = 101;
    public static final int PICK_IMAGE_REQUEST = 111;
    public static final int PLACE_PICKER_REQUEST = 112;
    public static final int EXHIBITION_REQUEST = 113;

    public static final String[] popularPlacesHeader = {"Mumbai", "New Delhi", "Hyderabad"};
    public static final String[][] popularPlaces = {{"Bandra", "Colaba", "Vile Parle"}, {"Sainik Farm", "Shahdara", "Dwarka"}
            , {"Secunderabad", "HITEC City", "Banjara Hills"}};
    /**Add constants to the jsonobject - for POSTing
     * Have to attach this to all apis
     *
     * @param jsonObject JSON object which is ready to POST
     * @param context ACtivity context
     * @return jsonObject having Useris and sessionid*/
    public static JSONObject addConstants(JSONObject jsonObject, Context context){

        JSONObject jsonBody = jsonObject;
        try {
            jsonBody.put("Latitude", PreferenceServices.getInstance().getLatitute());
            jsonBody.put("Longitude", PreferenceServices.getInstance().getLongitute());
            jsonBody.put("ApiKey", Constant.API);
            //jsonBody.put("DeviceId", PretStreet.getDeviceId());
            jsonBody.put("DeviceType", "1");

            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
            LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
            jsonBody.put("UserId", loginSession.getRegid());
            jsonBody.put("SessionId", loginSession.getSessionid());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    /**Add constants to the jsonobject - for POSTing
     * Have to attach this to all apis
     *
     * @param jsonObject JSON object which is ready to POST
     * @param context ACtivity context
     * @return new jsonObject having Token*/
    public static JSONObject addDeviceId(JSONObject jsonObject, Context context){

        JSONObject jsonBody = jsonObject;
        try {
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
            /** FCM TOKEN*/
            if (sharedPreferencesHelper.getString("TOKEN", "").trim().length()==0) {
                TokenService tokenService = new TokenService();
                tokenService.onTokenRefresh();
            }
            jsonBody.put("DeviceId", sharedPreferencesHelper.getString("TOKEN", ""));
        } catch (JSONException e) {
        } catch (Exception e) {}
        return jsonBody;
    }

    /** Update baseURL whenever we call Welcome page*/
    public static String getBaseUrl(){
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(PretStreet.getInstance());
        String baseUrl = sharedPreferencesHelper.getString("BASEURL", "");
        return baseUrl;
    }
}

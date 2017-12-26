package com.hit.pretstreet.pretstreet.core.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.INSTALLREFERRERKEY;

/**
 * Created by User on 27/09/2017
 */

public class InstallReferrerReceiver extends BroadcastReceiver {

    public final static String[] EXPECTED_PARAMETERS = {"utm_source",
            "utm_medium", "utm_term", "utm_content", "utm_campaign", "share", "id"};

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceServices.init(context);
        try {
                /*PreferenceServices.init(context);
                String referrer = intent.getStringExtra("referrer");
                referrer = referrer.replace("referrer=", "");
                String[] sepStr = referrer.split("/");
                Log.e("InstallReferrerReceiver TAG", "data saved "+referrer);
                Log.e("InstallReferrerReceiver TAG", "data saved "+"  "+sepStr[0]+"  "+sepStr[1]);
                PreferenceServices.getInstance().setIdQueryparam(sepStr[1]); //id
                PreferenceServices.getInstance().setShareQueryparam(sepStr[0]); //storeparam*/

            Map<String, String> referralParams = new HashMap<String, String>();
            if (intent == null) {
                return;
            }
            if (!intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
                return;
            }
            String referrer = intent.getStringExtra("referrer");
            PreferenceServices.getInstance().setUTMQueryparam(referrer); //utm param*/
            if (referrer == null || referrer.length() == 0) {
                return;
            }
            try {
                referrer = URLDecoder.decode(referrer, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return;
            }
            try {
                String[] params = referrer.split("&");
                for (String param : params) {
                    String[] pair = param.split("=");
                    if (pair.length == 1) {
                        referralParams.put(pair[0], "AndroidApp");
                    } else if (pair.length == 2) {
                        referralParams.put(pair[0], pair[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            storeReferralParams(referralParams);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        /***************  Update googleAnalytics  ***************/
        new CampaignTrackingReceiver().onReceive(context, intent);
    }

    public static void storeReferralParams(Map<String, String> params) {
       for (String key : EXPECTED_PARAMETERS) {
            String value = params.get(key);
            Log.e("InstallReferrerReceiver TAG","value - "+value);
            PreferenceServices.getInstance().setTypeQueryparam(INSTALLREFERRERKEY);
            if (key.equals("share")) {
                PreferenceServices.getInstance().setShareQueryparam(value); //storeparam*/
            }
            else if(key.equals("id")){
                PreferenceServices.getInstance().setIdQueryparam(value); //id
            }
        }
    }
}
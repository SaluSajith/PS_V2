package com.hit.pretstreet.pretstreet.core.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;

/**
 * Created by User on 27/09/2017
 */

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String referrer = intent.getStringExtra("referrer");
            referrer = referrer.replace("referrer=", "");
            String[] sepStr = referrer.split("/");
            PreferenceServices.getInstance().setIdQueryparam(sepStr[1]); //id
            PreferenceServices.getInstance().setShareQueryparam(sepStr[0]); //storeparam
        }
        catch (Exception e){ e.printStackTrace();}

        //TODO
        // When you're done, pass the intent to the Google Analytics receiver.
        //new CampaignTrackingReceiver().onReceive(context, intent);
    }
}
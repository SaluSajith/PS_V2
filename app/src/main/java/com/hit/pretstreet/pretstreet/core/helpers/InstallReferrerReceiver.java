package com.hit.pretstreet.pretstreet.core.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
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

        try {
            PretStreet pretStreet = (PretStreet) context.getApplicationContext();
            Tracker mTracker = pretStreet.tracker();
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("New installation ")
                    .setAction(mTracker.get("UserTrack"))
                    .setLabel(mTracker.get("User Track"))
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // When you're done, pass the intent to the Google Analytics receiver.
        new CampaignTrackingReceiver().onReceive(context, intent);
    }
}
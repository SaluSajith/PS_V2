package com.hit.pretstreet.pretstreet.facebook;

import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookBroadcastReceiver;

/**
 * Created by hiral on 10/8/2015.
 */
public class PretStreetFacebookBroadcastReceiver extends
        FacebookBroadcastReceiver {

    @Override
    protected void onSuccessfulAppCall(String appCallId, String action,
                                       Bundle extras) {
        // A real app could update UI or notify the user that their photo was
        // uploaded.
        Log.d("StraboFacebook",
                String.format("Photo uploaded by call " + appCallId
                        + " succeeded."));
    }

    @Override
    protected void onFailedAppCall(String appCallId, String action,
                                   Bundle extras) {
        // A real app could update UI or notify the user that their photo was
        // not uploaded.
        Log.d("StraboFacebook",
                String.format("Photo uploaded by call " + appCallId
                        + " failed."));
    }
}

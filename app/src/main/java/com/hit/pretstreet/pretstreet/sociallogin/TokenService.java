package com.hit.pretstreet.pretstreet.sociallogin;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;

/**
 * Created by User on 28/08/2017.
 */

public class TokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("notification", refreshedToken);
        saveToken(refreshedToken);
    }

    private void saveToken(String token) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(PretStreet.getInstance());
        sharedPreferencesHelper.putString("TOKEN", token);
    }
}
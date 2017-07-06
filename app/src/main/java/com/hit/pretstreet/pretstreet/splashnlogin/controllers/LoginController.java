package com.hit.pretstreet.pretstreet.splashnlogin.controllers;

import android.app.Application;
import android.provider.Settings;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hit.pretstreet.pretstreet.PretStreet;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by User on 7/4/2017.
 */

public class LoginController {

    private static final int PROFILE_PIC_SIZE = 400;

    public static JSONObject getFacebookLoginData(JSONObject jsonObject) {

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("social_id", jsonObject.getString("id").toString());
            jsonBody.put("social_type", "facebook");
            jsonBody.put("profile_pic", URLEncoder.encode("https://graph.facebook.com/" +
                    jsonObject.getString("id").toString() + "/picture?type=large", "UTF-8"));
            if (jsonObject.has("email")) {
                jsonBody.put("email", jsonObject.getString("email").toString());
            } else {
                jsonBody.put("email", "");
            }
            if (jsonObject.has("name")) {
                jsonBody.put("fname", URLEncoder.encode(jsonObject.getString("name").toString(), "UTF-8"));
            } else {
                jsonBody.put("fname", "");
            }
            if (jsonObject.has("gender")) {
                jsonBody.put("gender", jsonObject.getString("gender").toString());
            } else {
                jsonBody.put("gender", "");
            }
            jsonBody.put("device", "1");
            jsonBody.put("deviceid", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getGoogleLoginDetails(GoogleSignInAccount account) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("social_id", account.getId());
            jsonBody.put("social_type", "google");
            String googleImageUrl = String.valueOf(account.getPhotoUrl());
            jsonBody.put("profile_pic", googleImageUrl.substring(0, googleImageUrl.length() - 2) + PROFILE_PIC_SIZE);
            jsonBody.put("email", account.getEmail());
            jsonBody.put("fname", account.getGivenName());
            jsonBody.put("lname", account.getFamilyName());
            jsonBody.put("gender", "");
            jsonBody.put("device", "1");
            jsonBody.put("deviceid", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }
}
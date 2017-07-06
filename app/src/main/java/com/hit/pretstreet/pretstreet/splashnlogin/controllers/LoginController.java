package com.hit.pretstreet.pretstreet.splashnlogin.controllers;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by User on 7/4/2017.
 */

public class LoginController {

    private static final int PROFILE_PIC_SIZE = 400;
    private static LoginCallbackInterface loginCallbackInterface;
    private static Context context;

    public LoginController(LoginCallbackInterface loginCallbackInterface, Context context){
        this.loginCallbackInterface = loginCallbackInterface;
        this.context = context;
    }

    public static JSONObject getFacebookLoginData(JSONObject jsonObject) {

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("social_id", URLEncoder.encode(jsonObject.getString("id").toString(), "UTF-8"));
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
            jsonBody.put("social_id", URLEncoder.encode(account.getId(), "UTF-8"));
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

    public static void validateTokenFields(EdittextPret et_name, EdittextPret et_pass) {
        String message = "Fields cannot be empty";
        EdittextPret edittextPret = new EdittextPret(context);

        String email = et_name.getText().toString();
        String password = et_pass.getText().toString();
        if (email.length() < 1) {
            edittextPret = et_name;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message);
            return;
        }  else if (Utility.isValidEmail(email)) {
            edittextPret = et_name;
            message = "Invalid email";
            loginCallbackInterface.validateCallback(edittextPret, message);
            return;
        }  else if (password.length() < 1) {
            edittextPret = et_pass;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message);
            return;
        } else {
            //TODO login
            //login(name, password);
        }
    }
}
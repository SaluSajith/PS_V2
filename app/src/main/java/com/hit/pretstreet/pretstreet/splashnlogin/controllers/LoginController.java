package com.hit.pretstreet.pretstreet.splashnlogin.controllers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

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

    private static int SIGNUP = 0;
    private static int LOGIN = 1;

    public LoginController(LoginCallbackInterface loginCallbackInterface, Context context){
        this.loginCallbackInterface = loginCallbackInterface;
        this.context = context;
    }

    public static JSONObject getFacebookLoginData(JSONObject jsonObject) {

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("UserSocialId", URLEncoder.encode(jsonObject.getString("id").toString(), "UTF-8"));
            jsonBody.put("UserSocialType", "facebook");
            jsonBody.put("profile_pic", URLEncoder.encode("https://graph.facebook.com/" +
                    jsonObject.getString("id").toString() + "/picture?type=large", "UTF-8"));
            if (jsonObject.has("email")) {
                jsonBody.put("UserEmail", jsonObject.getString("email").toString());
            } else {
                jsonBody.put("UserEmail", "");
            }
            if (jsonObject.has("name")) {
                jsonBody.put("FirstName", URLEncoder.encode(jsonObject.getString("name").toString(), "UTF-8"));
            } else {
                jsonBody.put("FirstName", "");
            }
            jsonBody.put("LastName", "");
            if (jsonObject.has("gender")) {
                jsonBody.put("gender", jsonObject.getString("gender").toString());
            } else {
                jsonBody.put("gender", "");
            }
            jsonBody.put("ApiKey", Constant.API);
            jsonBody.put("DeviceType", "1");
            jsonBody.put("DeviceId", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getGoogleLoginDetails(GoogleSignInAccount account) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserSocialId", URLEncoder.encode(account.getId(), "UTF-8"));
            jsonBody.put("UserSocialType", "google");
            String googleImageUrl = String.valueOf(account.getPhotoUrl());
            jsonBody.put("profile_pic", googleImageUrl.substring(0, googleImageUrl.length() - 2) + PROFILE_PIC_SIZE);
            jsonBody.put("UserEmail", account.getEmail());
            jsonBody.put("FirstName", account.getGivenName());
            jsonBody.put("LastName", account.getFamilyName());
            jsonBody.put("ApiKey", Constant.API);
            jsonBody.put("gender", "");
            jsonBody.put("DeviceType", "1");
            jsonBody.put("DeviceId", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getNormalLoginDetails(LoginSession loginSession) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("FirstName", loginSession.getFname());
            jsonBody.put("LastName", loginSession.getLname());
            jsonBody.put("UserEmail", loginSession.getEmail());
            jsonBody.put("UserMobile", loginSession.getMobile());
            jsonBody.put("UserPassword", loginSession.getPassword());
            jsonBody.put("Gender", 1);
            jsonBody.put("ApiKey", Constant.API);
            jsonBody.put("DeviceType", "1");
            jsonBody.put("DeviceId", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getSocialLoginDetails(String phone) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("social_id", "");
            jsonBody.put("social_type", "");
            jsonBody.put("profile_pic", "");
            jsonBody.put("email", "");
            jsonBody.put("fname", "");
            jsonBody.put("lname", "");
            jsonBody.put("gender", "");
            jsonBody.put("DeviceType", "1");
            jsonBody.put("DeviceId", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }


    public static JSONObject getOTPVerificationJson(String phone, String email) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserMobile", phone);
            jsonBody.put("UserEmail", email);
            jsonBody.put("ApiKey", Constant.API);
            jsonBody.put("DeviceType", "1");
            jsonBody.put("DeviceId", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static void validateLoginFields(EdittextPret et_number) {
        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        String number = et_number.getText().toString();
        if (number.length() < 1) {
            edittextPret = et_number;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, LOGIN);
            return;
        }  else if (!Utility.validCellPhone(number)) {
            edittextPret = et_number;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, LOGIN);
            return;
        } else {
            loginCallbackInterface.validationSuccess(number);
        }
    }

    public static void validateRegisterFields(EdittextPret edt_firstname,
                                              EdittextPret edt_lastname,
                                              EdittextPret edt_mobile,
                                              EdittextPret edt_email,
                                              EdittextPret edt_password) {

        String firstname = edt_firstname.getText().toString().trim();
        String lname = edt_lastname.getText().toString().trim();
        String mobile = edt_mobile.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        if (firstname.length() < 1) {
            edittextPret = edt_firstname;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        } else if (lname.length() < 1) {
            edittextPret = edt_lastname;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (!Utility.validCellPhone(mobile)) {
            edittextPret = edt_mobile;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (email.length() < 1) {
            edittextPret = edt_email;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (!Utility.isValidEmail(email)) {
            edittextPret = edt_email;
            message = "Invalid email";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (password.length() < 6) {
            edittextPret = edt_password;
            message = "Password length should be more than 6";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        } else {
            LoginSession loginSession = new LoginSession();
            loginSession.setFname(firstname);
            loginSession.setLname(lname);
            loginSession.setMobile(mobile);
            loginSession.setEmail(email);
            loginSession.setPassword(password);
            loginCallbackInterface.validationSuccess(loginSession);
        }
    }
}
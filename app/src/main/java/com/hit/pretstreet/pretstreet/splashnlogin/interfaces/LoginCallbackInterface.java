package com.hit.pretstreet.pretstreet.splashnlogin.interfaces;

import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import org.json.JSONObject;

/**
 * Created by User on 7/6/2017.
 */

public interface LoginCallbackInterface {
    void validateCallback(EdittextPret editText, String message, int type);
    void validationSuccess(String phonenumber);
    void validationSuccess(LoginSession loginSession);
    void validationSuccess(JSONObject jsonObject, int type);
}

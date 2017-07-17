package com.hit.pretstreet.pretstreet.splashnlogin.interfaces;

import android.widget.EditText;

import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

/**
 * Created by User on 7/6/2017.
 */

public interface LoginCallbackInterface {
    void validateCallback(EdittextPret editText, String message, int type);
    void validationSuccess(String phonenumber);
    void validationSuccess(LoginSession loginSession);
}

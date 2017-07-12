package com.hit.pretstreet.pretstreet.splashnlogin.interfaces;

import android.widget.EditText;

import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;

/**
 * Created by User on 7/6/2017.
 */

public interface LoginCallbackInterface {
    void validateCallback(EdittextPret editText, String message);
    void validationSuccess(String phonenumber);
}

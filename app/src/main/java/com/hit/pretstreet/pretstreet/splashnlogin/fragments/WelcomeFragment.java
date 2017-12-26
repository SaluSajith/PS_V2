package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.sociallogin.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.sociallogin.GoogleLoginActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;

import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by User on 7/10/2017.
 */

public class WelcomeFragment extends AbstractBaseFragment<WelcomeActivity> {

    private static final int SIGNUP_CLICK = 1;
    private static final int LOGIN_CLICK = 2;

    ButtonClickCallback buttonClickCallback;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        init(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            buttonClickCallback = (ButtonClickCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onViewSelected");
        }
    }

    private void init(View view){
        ButterKnife.bind(this, view);
        PreferenceServices.init(getActivity());
    }

    @OnClick(R.id.btn_sign_up)
    public void onSignuppressed() {
        buttonClickCallback.buttonClick(SIGNUP_CLICK);
    }

    @OnClick(R.id.btn_login)
    public void onLoginPressed() {
        buttonClickCallback.buttonClick(LOGIN_CLICK);
    }

    @OnClick(R.id.btn_facebook)
    public void onFacebookPressed() {
        ((WelcomeActivity)getActivity()).facebookClick();
    }

    @OnClick(R.id.btn_google)
    public void onGooglePressed() {
        ((WelcomeActivity)getActivity()).googleClick();
    }
}
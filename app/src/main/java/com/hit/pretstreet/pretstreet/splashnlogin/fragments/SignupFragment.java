package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/10/2017.
 */

public class SignupFragment extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.tv_heading) TextViewPret tv_heading;
    @BindView(R.id.edt_firstname) EdittextPret edt_firstname;
    @BindView(R.id.edt_lastname) EdittextPret edt_lastname;
    @BindView(R.id.edt_mobile) EdittextPret edt_mobile;
    @BindView(R.id.edt_email) EdittextPret edt_email;
    @BindView(R.id.edt_password) EdittextPret edt_password;
    @BindView(R.id.btn_signup)ButtonPret btn_signup;

    LoginController loginController;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        ButterKnife.bind(this, view);
        PreferenceServices.init(getActivity());
        loginController = new LoginController((LoginCallbackInterface) getActivity(), getContext());
    }

    public void onValidationError(EdittextPret editText, String message){
        editText.setError(message);
    }

    @OnClick(R.id.btn_signup)
    public void onSignupPressed() {
        btn_signup.setEnabled(false);
        loginController.validateRegisterFields(edt_firstname, edt_lastname, edt_mobile, edt_email, edt_password);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btn_signup.setEnabled(true);
            }
        }, 2000);
    }

    @OnClick(R.id.btn_facebook)
    public void onFacebookPressed() {
        ((WelcomeActivity)getActivity()).facebookClick();
    }

    @OnClick(R.id.btn_google)
    public void onGooglePressed() {
        ((WelcomeActivity)getActivity()).googleClick();
    }

    @OnClick(R.id.txt_conditon)
    public void onTermsPressed() {
        ((WelcomeActivity)getActivity()).termsClick();
    }
}
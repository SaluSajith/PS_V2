package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/7/2017.
 */

public class LoginFragment extends AbstractBaseFragment<WelcomeActivity>{

    @BindView(R.id.tv_heading)TextViewPret tv_heading;
    @BindView(R.id.edt_email_number)EdittextPret edt_email_number;
    @BindView(R.id.btn_login)ButtonPret btn_login;

    LoginController loginController;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        tv_heading.setPaintFlags(tv_heading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginController = new LoginController((LoginCallbackInterface) getActivity(), getContext());
    }

    @OnClick(R.id.btn_login)
    public void onLoginPressed() {
        loginController.validateLoginFields(edt_email_number);
    }

    @OnClick(R.id.btn_facebook)
    public void onFacebookPressed() {
        ((WelcomeActivity)getActivity()).facebookClick();
    }

    @OnClick(R.id.btn_google)
    public void onGooglePressed() {
        ((WelcomeActivity)getActivity()).googleClick();
    }

    public void onValidationError(EdittextPret editText, String message) {
        try {
            editText.setError(message);
        }catch (Exception e){}
    }
}

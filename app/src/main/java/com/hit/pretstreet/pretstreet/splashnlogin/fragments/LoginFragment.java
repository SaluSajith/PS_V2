package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.sociallogin.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.sociallogin.GoogleLoginActivity;
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

    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;

    @BindView(R.id.tv_heading)TextViewPret tv_heading;
    @BindView(R.id.edt_email_number)EdittextPret edt_email_number;

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
        Intent facebookLoginIntent = new Intent(getActivity(), FacebookLoginScreen.class);
        facebookLoginIntent.putExtra("cat", "Login");
        facebookLoginIntent.putExtra("Type", "FirstLogin");
        getActivity().startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
    }

    @OnClick(R.id.btn_google)
    public void onGooglePressed() {
        if (ContextCompat.checkSelfPermission(PretStreet.getInstance(), Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            Intent googleLoginIntent = new Intent(getActivity(), GoogleLoginActivity.class);
            getActivity().startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
        } else {
            askCompactPermission(Manifest.permission.GET_ACCOUNTS, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    Intent googleLoginIntent = new Intent(getActivity(), GoogleLoginActivity.class);
                    getActivity().startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                }

                @Override
                public void permissionDenied() {
                }
            });
        }
    }

    public void onValidationError(EdittextPret editText, String message) {
        editText.setError(message);
    }
}

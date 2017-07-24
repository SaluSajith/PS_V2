package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
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
 * Created by User on 7/10/2017.
 */

public class SignupFragment extends AbstractBaseFragment<WelcomeActivity> {

    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;

    @BindView(R.id.tv_heading) TextViewPret tv_heading;
    @BindView(R.id.edt_firstname) EdittextPret edt_firstname;
    @BindView(R.id.edt_lastname) EdittextPret edt_lastname;
    @BindView(R.id.edt_mobile) EdittextPret edt_mobile;
    @BindView(R.id.edt_email) EdittextPret edt_email;
    @BindView(R.id.edt_password) EdittextPret edt_password;

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
        loginController.validateRegisterFields(edt_firstname, edt_lastname, edt_mobile, edt_email, edt_password);
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

}
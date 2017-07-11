package com.hit.pretstreet.pretstreet.splashnlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.sociallogin.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.LoginFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SignupFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.WelcomeFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hit on 10/3/16.
 */
public class WelcomeScreen extends AbstractBaseAppCompatActivity implements ApiListenerInterface, ButtonClickCallback{

    private static final int WELCOME_FRAGMENT = 0;
    private static final int LOGIN_FRAGMENT = 1;
    private static final int SIGNUP_FRAGMENT = 2;
    private static final int FORGETPASSWORD_FRAGMENT = 3;
    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;
    private static final int SIGNUP_CLICK_CODE = 1;
    private static final int LOGIN_CLICK_CODE = 2;
    private int currentFragment = 0;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;

    JsonRequestController jsonRequestController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        changeFragment(new WelcomeFragment(), false);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);

    }

    private void changeFragment(Fragment fragment, boolean addBackstack) {

        FragmentManager fm = getSupportFragmentManager();       /*Removing stack*/
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        fl_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content, fragment);
        if (addBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }


    /*@Override
    public void onBackPressed() {
        if (currentFragment == FORGETPASSWORD_FRAGMENT) {
            currentFragment = WELCOME_FRAGMENT;
            changeFragment(new WelcomeFragment(), false);
        } else if (currentFragment == SIGNUP_FRAGMENT) {
            currentFragment = WELCOME_FRAGMENT;
            changeFragment(new WelcomeFragment(), false);
        } else if (currentFragment == LOGIN_FRAGMENT) {
            currentFragment = WELCOME_FRAGMENT;
            changeFragment(new WelcomeFragment(), false);
        } else if (currentFragment == WELCOME_FRAGMENT) {
            finish();
        }
    }*/

    private void setupSocialLogin(String stringJSON){
        try {
            JSONObject responseJSON = new JSONObject(stringJSON);
            if(responseJSON!=null) {
                JSONObject responseObject = responseJSON;
                JSONObject resultJson = LoginController.getFacebookLoginData(responseObject);
                //TODO :  api call
                this.showProgressDialog(getResources().getString(R.string.loading));
                jsonRequestController.test();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getGoogleResponse(GoogleSignInAccount signInAccount) {
        JSONObject resultJson = LoginController.getGoogleLoginDetails(signInAccount);
        //TODO :  api call
        jsonRequestController.test();
        // getCheckFBLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FACEBOOK_LOGIN_REQUEST_CODE:
                if (resultCode == FacebookLoginScreen.FACEBOOK_LOGIN_RESULT_CODE_SUCCESS) {
                    setupSocialLogin(data.getStringExtra("responsejson"));
                } else {
                    Log.e("TAG", "Facebook LOGIN FAIL");
                    Snackbar.make( getWindow().getDecorView().getRootView(), "Login failed!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;

            case GOOGLE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    GoogleSignInAccount account = data.getParcelableExtra("responsejson");
                    getGoogleResponse(account);
                } else {
                    Log.e("TAG", "Google  LOGIN FAIL");
                    Snackbar.make( getWindow().getDecorView().getRootView(), "Login failed!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.e("Volley", response.toString());
        /*boolean responseSuccess = false;
        String userId = null, strSuccess = null;
        try {
            System.out.println("\n");
            strSuccess = response.getString("success");
            if (strSuccess.equals("false")) {
                responseSuccess = false;
            } else {
                responseSuccess = true;
                userId = response.getString("user_id");
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (responseSuccess) {
            hidepDialog();
            PreferenceServices.instance().saveUserId(userId);
            PreferenceServices.instance().saveLoginType("Social");
                    *//*if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }*//*
            finish();
        } else {
            hidepDialog();
            Snackbar.make( getWindow().getDecorView().getRootView(), "Facebook Login Failed.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }*/
    }

    @Override
    public void onError(String error) {
        this.destroyDialog();
        Snackbar.make( getWindow().getDecorView().getRootView(), error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void buttonClick(int id) {
        if(id == SIGNUP_CLICK_CODE){
            currentFragment = SIGNUP_FRAGMENT;
            changeFragment(new SignupFragment(), true);
        }
        else if(id == LOGIN_CLICK_CODE){
            currentFragment = SIGNUP_FRAGMENT;
            changeFragment(new LoginFragment(), true);
        }
    }
}

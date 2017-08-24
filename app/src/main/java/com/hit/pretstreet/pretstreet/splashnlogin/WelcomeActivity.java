package com.hit.pretstreet.pretstreet.splashnlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.sociallogin.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.LoginFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SignupFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SplashFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.WelcomeFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 20/07/2017.
 */
public class WelcomeActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, LoginCallbackInterface {

    private int currentFragment = 0;
    private static final int WELCOME_FRAGMENT = 0;
    private static final int LOGIN_FRAGMENT = 1;
    private static final int SIGNUP_FRAGMENT = 2;
    private static final int FORGETPASSWORD_FRAGMENT = 3;
    private static final int SPLASH_FRAGMENT = 4;
    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;
    private static final int SIGNUP_CLICK_CODE = 1;
    private static final int LOGIN_CLICK_CODE = 2;
    private static int SIGNUP = 0;
    private static int LOGIN = 1;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.content_splash) FrameLayout fl_content_splash;

    JsonRequestController jsonRequestController;
    LoginController loginController;

    private static int DURATION;
    private Handler splashHandler;
    private static final int SPLASH_DURATION_END = 2000;

    private String otpValue;
    Dialog popupDialog;
    String mProfilePic;
    JSONObject registerJson, loginJson;
    private static final int PROFILE_PIC_SIZE = 400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        changeFragment(new SplashFragment(), false, SPLASH_FRAGMENT);
        fl_content_splash.bringToFront();

    }

    @Override
    protected void setUpController() {
        loginController = new LoginController(this);
        jsonRequestController = new JsonRequestController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        splashHandler = new Handler();
        DURATION = Integer.valueOf(getString(R.string.splash_duration));
        splashHandler.postDelayed(mChangeSplash, DURATION);
        splashHandler.postDelayed(mEndSplash, SPLASH_DURATION_END);
    }

    private void changeFragment(Fragment fragment, boolean addBackstack, int content) {

        FragmentManager fm = getSupportFragmentManager();       /*Removing stack*/
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        fl_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if(content==SPLASH_FRAGMENT)
            ft.add(R.id.content_splash, fragment);
        else {
            fl_content_splash.removeAllViews();
            ft.replace(R.id.content, fragment);
        }
        if (addBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void setupSocialLogin(String stringJSON){
        try {
            JSONObject responseJSON = new JSONObject(stringJSON);
            if(responseJSON!=null) {
                mProfilePic = URLEncoder.encode("https://graph.facebook.com/" +
                        responseJSON.getString("id").toString() + "/picture?type=large", "UTF-8");
                JSONObject resultJson = loginController.getFacebookLoginData(responseJSON);
                this.showProgressDialog(getResources().getString(R.string.loading));
                jsonRequestController.sendRequest(this, resultJson, Constant.SOCIAL_LOGIN_URL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getGoogleResponse(GoogleSignInAccount signInAccount) {
        JSONObject resultJson = loginController.getGoogleLoginDetails(signInAccount);
        String googleImageUrl = String.valueOf(signInAccount.getPhotoUrl());
        mProfilePic = googleImageUrl.substring(0, googleImageUrl.length() - 2) + PROFILE_PIC_SIZE;
        jsonRequestController.sendRequest(this, resultJson, Constant.SOCIAL_LOGIN_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        displayLogMessage("resultCode ", resultCode+"");

        switch (requestCode) {
            case FACEBOOK_LOGIN_REQUEST_CODE:
                if (resultCode == FacebookLoginScreen.FACEBOOK_LOGIN_RESULT_CODE_SUCCESS) {
                    setupSocialLogin(data.getStringExtra("responsejson"));
                } else {
                    displaySnackBar("Login failed!");
                }
                break;

            case GOOGLE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    GoogleSignInAccount account = data.getParcelableExtra("responsejson");
                    getGoogleResponse(account);
                } else {
                    Log.e("TAG", "Google  LOGIN FAIL");
                    displaySnackBar("Login failed!");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        this.hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar( error);
    }

    @Override
    public void buttonClick(int id) {
        if(id == SIGNUP_CLICK_CODE){
            currentFragment = SIGNUP_FRAGMENT;
            changeFragment(new SignupFragment(), true, WELCOME_FRAGMENT);
        }
        else if(id == LOGIN_CLICK_CODE){
            currentFragment = SIGNUP_FRAGMENT;
            changeFragment(new LoginFragment(), true, LOGIN_FRAGMENT);
        }
    }

    @Override
    public void validateCallback(EdittextPret editText, String message, int type) {
        if(type == SIGNUP) {
            SignupFragment signupFragment = new SignupFragment();
            signupFragment.onValidationError(editText, message);
        }else if(type == LOGIN){
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.onValidationError(editText, message);
        }
    }

    @Override
    public void validationSuccess(String phonenumber) {
        LoginSession loginSession = new LoginSession();
        loginSession.setMobile(phonenumber);
        loginJson = loginController.getNormalLoginDetails(loginSession);
        JSONObject otpObject = loginController.getOTPVerificationJson(phonenumber, "");
        showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, otpObject, Constant.LOGIN_OTP_URL);
    }

    @Override
    public void validationSuccess(LoginSession loginSession) {
        registerJson = loginController.getNormalLoginDetails(loginSession);
        showProgressDialog(getResources().getString(R.string.loading));
        JSONObject otpObject = loginController.getOTPVerificationJson(loginSession.getMobile(), loginSession.getEmail());
        jsonRequestController.sendRequest(this, otpObject, Constant.REGISTRATION_OTP_URL);
    }

    @Override
    public void validationSuccess(JSONObject jsonObject, int type) {

    }

    private void handleResponse(JSONObject response){
        String strsuccess = null;
        try {
            String url = response.getString("URL");
            strsuccess = response.getString("Status");
            if (strsuccess.equals("1")) {
                displaySnackBar(response.getString("CustomerMessage"));
                switch (url){
                    case Constant.REGISTRATION_OTP_URL:
                        otpValue = response.getJSONObject("Data").getString("OTP");
                        showOTPScreem(registerJson, Constant.REGISTRATION_URL);
                        break;
                    case Constant.REGISTRATION_URL:
                        setupSession(response, "", "");
                        break;
                    case Constant.LOGIN_OTP_URL:
                        otpValue = response.getJSONObject("Data").getString("OTP");
                        showOTPScreem(loginJson, Constant.LOGIN_URL);
                        break;
                    case Constant.LOGIN_URL:
                        setupSession(response, "", "");
                        break;
                    case Constant.SOCIAL_LOGIN_URL:
                        setupSession(response, "social", mProfilePic);
                        break;
                    default: break;
                }
            } else {
                displaySnackBar(response.getString("Message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupSession(JSONObject response, String loginType, String pic){

        try {
            JSONObject object = response.getJSONObject("Data");

            LoginSession loginSession = new LoginSession();
            loginSession.setRegid(object.getString("UserId"));
            loginSession.setProfile_pic(pic);
            loginSession.setFname(object.getString("UserFirstName"));
            loginSession.setLname(object.getString("UserLastName"));
            loginSession.setEmail(object.getString("UserEmail"));
            loginSession.setSessionid(object.getString("UserSessionId"));

            loginSession.setMobile(object.getString("UserMobile"));
            if(object.has("UserProfilePicture")) {
                loginSession.setProfile_pic(object.getString("UserProfilePicture"));
            }
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
            sharedPreferencesHelper.createLoginSession(loginSession);
            PreferenceServices.instance().saveUserId(object.getString("UserId"));
            PreferenceServices.instance().saveUserName(object.getString("UserFirstName")+" "+object.getString("UserLastName"));
            PreferenceServices.instance().saveLoginType(loginType);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(getApplicationContext(), DefaultLocationActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    finish();
                }
            }, 1500);

        } catch (JSONException e) {
            e.printStackTrace();
            this.displaySnackBar(e.toString());
        }
    }


    private Runnable mEndSplash = new Runnable() {
        public void run() {
            if (!isFinishing()) {
                splashHandler.removeCallbacks(this);
                if (PreferenceServices.getInstance().geUsertId().equalsIgnoreCase("")) {
                    changeFragment(new WelcomeFragment(), false, WELCOME_FRAGMENT);
                } else {
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(getApplicationContext(), DefaultLocationActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    finish();
                }
            }
        }
    };

    private Runnable mChangeSplash = new Runnable() {
        public void run() {
            if (!isFinishing()) {
                splashHandler.removeCallbacks(this);
            }
        }
    };


    public void showOTPScreem(final JSONObject jsonObject, final String url) {

        popupDialog = new Dialog(this);
        popupDialog.setCanceledOnTouchOutside(false);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_otp_screen, null);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        final EdittextPret edt_otp = (EdittextPret) view.findViewById(R.id.edt_otp);
        ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);

        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.popup_bundle);
        rl.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        rl.setLayoutParams(lp);
        popupDialog.setContentView(view);

        popupDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) popupDialog.getWindow().getAttributes();
        popupDialog.getWindow().setAttributes(params);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupDialog.show();

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_otp.getText().toString().length() < 1) {
                    displaySnackBar("Enter OTP value");
                    edt_otp.requestFocus();
                }
                else {
                    popupDialog.dismiss();
                    if (otpValue.equals(edt_otp.getText().toString())) {
                        jsonRequestController.sendRequest(WelcomeActivity.this, jsonObject, url);
                    } else {
                        displaySnackBar("Wrong OTP");
                    }
                }
            }
        });

    }
}

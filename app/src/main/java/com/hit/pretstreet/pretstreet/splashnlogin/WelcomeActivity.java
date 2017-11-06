package com.hit.pretstreet.pretstreet.splashnlogin;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.hit.pretstreet.pretstreet.BuildConfig;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.helpers.DatabaseHelper;
import com.hit.pretstreet.pretstreet.core.helpers.IncomingSms;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.navigation.ExhibitionDetailsActivity;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.TrendingArticleActivity;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.sociallogin.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.sociallogin.GoogleLoginActivity;
import com.hit.pretstreet.pretstreet.sociallogin.TokenService;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.LoginFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SignupFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.SplashFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.WelcomeFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.SmsListener;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.DEEPLINKINGKEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.LOGIN_OTP_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.LOGIN_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REGISTRATION_OTP_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.REGISTRATION_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SIGNUPPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SOCIAL_LOGIN_URL;

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
    private static final int SIGNUP = 0;
    private static final int LOGIN = 1;
    private static final int SOCIAL_LOGIN = 2;
    private static final int TERMS_FRAGMENT = 8;

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.content_splash) FrameLayout fl_content_splash;

    JsonRequestController jsonRequestController;
    LoginController loginController;

    private static int DURATION;
    private Handler splashHandler;
    private static final int SPLASH_DURATION_END = 1500;

    String otpValue;
    Dialog popupDialog;
    Context context;
    JSONObject registerJson, loginJson;

    SignupFragment signupFragment;
    LoginFragment loginFragment;
    EdittextPret edittextPret;
    ButtonPret buttonPret;

    boolean notif = false;

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
        popupDialog = new Dialog(this);
        context = getApplicationContext();
        DURATION = Integer.valueOf(getString(R.string.splash_duration));
        splashHandler.postDelayed(mChangeSplash, DURATION);
        splashHandler.postDelayed(mEndSplash, SPLASH_DURATION_END);

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        try {
            if (sharedPreferencesHelper.getString("TOKEN", "").equalsIgnoreCase("")) {
                TokenService tokenService = new TokenService();
                tokenService.onTokenRefresh();
            }
            System.out.println("TOKEN" + sharedPreferencesHelper.getString("TOKEN", ""));
        }catch (Exception e){}

        if (getIntent().getExtras() != null && notif == false) {
            notif = true;
            //for (String key : getIntent().getExtras().keySet()) {
            // String value = getIntent().getExtras().getString(key);
            // Log.d("TOKEN", "Key: " + key + " Value: " + value);
            try {
                if(getIntent().getExtras().containsKey("image")){
                    saveNotification(getIntent());
                }else if(getIntent().getExtras().containsKey("share")){
                    String valueOne = getIntent().getExtras().getString("share");
                    String id = getIntent().getExtras().getString("id");
                    Log.d("TOKEN", "valueOne: " + valueOne + " id: " + id);
                    if (valueOne.trim().length() != 0 && id.trim().length() != 0) {
                        forwardDeepLink(valueOne, id, DEEPLINKINGKEY);
                        finish();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void saveNotification(Intent intent){
        try {
            TrendingItems trendingItems = new TrendingItems();
            trendingItems.setId(intent.getExtras().getString("id"));
            trendingItems.setTitle(intent.getExtras().getString("title"));
            trendingItems.setArticle(intent.getExtras().getString("body"));
            trendingItems.setShareUrl(intent.getExtras().getString("share"));
            trendingItems.setLogoImage("");
            ArrayList arrayList = new ArrayList();
            arrayList.add(intent.getExtras().getString("image"));
            trendingItems.setImagearray(arrayList);

            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.saveNotif(trendingItems);

            int size = PreferenceServices.getInstance().getNotifCOunt();
            PreferenceServices.getInstance().updateNotif(size + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeFragment(Fragment fragment, boolean addBackstack, int content) {

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSocialLogin(String stringJSON){
        try {
            JSONObject responseJSON = new JSONObject(stringJSON);
            if(responseJSON!=null) {
                JSONObject resultJson = loginController.getFacebookLoginData(responseJSON);
                this.showProgressDialog(getResources().getString(R.string.loading));
                jsonRequestController.sendRequest(this, resultJson, SOCIAL_LOGIN_URL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getGoogleResponse(GoogleSignInAccount signInAccount) {
        JSONObject resultJson = loginController.getGoogleLoginDetails(signInAccount);
        //String googleImageUrl = String.valueOf(signInAccount.getPhotoUrl());
        showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, SOCIAL_LOGIN_URL);
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
            signupFragment = new SignupFragment();
            changeFragment(signupFragment, true, WELCOME_FRAGMENT);
        }
        else if(id == LOGIN_CLICK_CODE){
            currentFragment = LOGIN_FRAGMENT;
            loginFragment = new LoginFragment();
            changeFragment(loginFragment, true, LOGIN_FRAGMENT);
        }
    }

    @Override
    public void validateCallback(EdittextPret editText, String message, int type) {
        if(type == SIGNUP) {
            signupFragment.onValidationError(editText, message);
        }else if(type == LOGIN){
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
        jsonRequestController.sendRequest(this, otpObject, LOGIN_OTP_URL);
    }

    @Override
    public void validationSuccess(LoginSession loginSession) {
        registerJson = loginController.getNormalLoginDetails(loginSession);
        showProgressDialog(getResources().getString(R.string.loading));
        JSONObject otpObject = loginController.getOTPVerificationJson(loginSession.getMobile(), loginSession.getEmail());
        jsonRequestController.sendRequest(this, otpObject, REGISTRATION_OTP_URL);
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
                    case REGISTRATION_OTP_URL:
                        otpValue = response.getJSONObject("Data").getString("OTP");
                        showOTPScreem(registerJson, REGISTRATION_URL);
                        break;
                    case REGISTRATION_URL:
                        setupSession(response, "", currentFragment);
                        break;
                    case LOGIN_OTP_URL:
                        otpValue = response.getJSONObject("Data").getString("OTP");
                        showOTPScreem(loginJson, LOGIN_URL);
                        break;
                    case LOGIN_URL:
                        setupSession(response, "", currentFragment);
                        break;
                    case SOCIAL_LOGIN_URL:
                        setupSession(response, "social", SOCIAL_LOGIN);
                        break;
                    default:
                        break;
                }
            } else {
                displaySnackBar(response.getString("Message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupSession(JSONObject response, String loginType, int type){
        showProgressDialog(getResources().getString(R.string.loading));
        try {
            JSONObject object = response.getJSONObject("Data");
            LoginSession loginSession = new LoginSession();
            loginSession.setRegid(object.getString("UserId"));
            loginSession.setFname(object.getString("UserFirstName"));
            loginSession.setLname(object.getString("UserLastName"));
            loginSession.setEmail(object.getString("UserEmail"));
            loginSession.setSessionid(object.getString("UserSessionId"));

            loginSession.setMobile(object.getString("UserMobile"));
            if(object.has("UserProfilePicture")) {
                String url = "";
                try {
                    url = URLDecoder.decode(object.getString("UserProfilePicture"), "UTF-8")+"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                loginSession.setProfile_pic(url);
            }
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
            sharedPreferencesHelper.createLoginSession(loginSession);
            PreferenceServices.instance().saveUserId(object.getString("UserId"));
            PreferenceServices.instance().saveUserName(object.getString("UserFirstName")+" "+object.getString("UserLastName"));
            PreferenceServices.instance().saveLoginType(loginType);

            if (BuildConfig.DEBUG){ }
            else {
                PretStreet pretStreet = (PretStreet) getApplication();
                Tracker mTracker = pretStreet.tracker();
                switch (type) {
                    case SIGNUP:
                        mTracker.set("UserTrack", "New Registration " + object.getString("UserEmail"));
                        break;
                    case LOGIN:
                        mTracker.set("UserTrack", "Login " + object.getString("UserEmail"));
                        break;
                    case SOCIAL_LOGIN:
                        mTracker.set("UserTrack", "SocialLogin " + object.getString("UserEmail"));
                        break;
                    default:
                        break;
                }
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("WelcomePage")
                        .setAction(mTracker.get("UserTrack"))
                        .setLabel(mTracker.get("UserTrack"))
                        .build());
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(context, DefaultLocationActivity.class));
                    } else {
                        startActivity(new Intent(context, HomeActivity.class));
                    }
                    hideDialog();
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
                //if (PreferenceServices.getInstance().geUsertId().equalsIgnoreCase("")) {
                SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
                LoginSession loginSession = sharedPreferencesHelper.getUserDetails();
                if(loginSession.getSessionid().trim().length()==0||loginSession.getRegid().trim().length()==0) {
                    changeFragment(new WelcomeFragment(), false, WELCOME_FRAGMENT);
                    setupOTPReceiver();
                }
                else {
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(context, DefaultLocationActivity.class));
                    } else {
                        startActivity(new Intent(context, HomeActivity.class));
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

    public void googleClick(){
        if (ContextCompat.checkSelfPermission(PretStreet.getInstance(), Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            Intent googleLoginIntent = new Intent(context, GoogleLoginActivity.class);
            startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
        } else {
            askCompactPermission(Manifest.permission.GET_ACCOUNTS, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    Intent googleLoginIntent = new Intent(context, GoogleLoginActivity.class);
                    startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                }
                @Override
                public void permissionDenied() {
                }
            });
        }
    }

    public void facebookClick(){
        Intent facebookLoginIntent = new Intent(context, FacebookLoginScreen.class);
        facebookLoginIntent.putExtra("cat", "Login");
        facebookLoginIntent.putExtra("Type", "FirstLogin");
        startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
    }

    public void termsClick(){
        Intent intent = new Intent(context, NavigationItemsActivity.class);
        intent.putExtra(PRE_PAGE_KEY, SIGNUPPAGE);
        intent.putExtra("fragment", TERMS_FRAGMENT);
        startActivity(intent);
    }

    private void showOTPScreem(final JSONObject jsonObject, final String url) {

        if(!popupDialog.isShowing()) {
            popupDialog = new Dialog(this);
            popupDialog.setCanceledOnTouchOutside(false);
            popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = li.inflate(R.layout.popup_otp_screen, null);
            ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
            final EdittextPret edt_otp = (EdittextPret) view.findViewById(R.id.edt_otp);
            edittextPret = edt_otp;
            ButtonPret btn_send = (ButtonPret) view.findViewById(R.id.btn_send);
            buttonPret = btn_send;

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
                    } else {
                        popupDialog.dismiss();
                        if (otpValue.equals(edt_otp.getText().toString())) {
                            showProgressDialog(getResources().getString(R.string.loading));
                            jsonRequestController.sendRequest(WelcomeActivity.this, jsonObject, url);
                        } else {
                            displaySnackBar("Wrong OTP");
                        }
                    }
                }
            });
            final TextViewPret tv_resend = (TextViewPret) view.findViewById(R.id.tv_resend);
            tv_resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_resend.setClickable(false);
                    tv_resend.setTextColor(ContextCompat.getColor(WelcomeActivity.this, R.color.dark_gray));
                    edt_otp.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(popupDialog.isShowing())
                                    tv_resend.setText("Seems like mobile network is not available. Please try using Google or Facebook login.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 10000);

                    showProgressDialog(getResources().getString(R.string.loading));
                    JSONObject otpObject = null;
                    try {
                        if (url.contains("login")) {
                            otpObject = loginController.getOTPVerificationJson(jsonObject.getString("UserMobile"), "");
                            jsonRequestController.sendRequest(WelcomeActivity.this, otpObject, LOGIN_OTP_URL);
                        } else {
                            otpObject = loginController.getOTPVerificationJson(jsonObject.getString("UserMobile"),
                                    jsonObject.getString("UserEmail"));
                            jsonRequestController.sendRequest(WelcomeActivity.this, otpObject, REGISTRATION_OTP_URL);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setupOTPReceiver(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            askCompactPermission(Manifest.permission.RECEIVE_SMS, new PermissionResult() {
                @Override
                public void permissionGranted() {
                }
                @Override
                public void permissionDenied() {
                }
            });
        }
        IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if(edittextPret!=null) {
                        edittextPret.setText(messageText);
                        buttonPret.performClick();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        //to avoid crashing in some devices
    }
}

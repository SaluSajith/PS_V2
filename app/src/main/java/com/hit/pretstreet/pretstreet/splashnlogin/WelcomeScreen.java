package com.hit.pretstreet.pretstreet.splashnlogin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.sociallogin.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.sociallogin.GoogleLoginActivity;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hit on 10/3/16.
 */
public class WelcomeScreen extends AbstractBaseAppCompatActivity implements ApiListenerInterface{

    @BindView(R.id.btn_sign_up) Button btn_sign_up;
    private ProgressDialog pDialog;
    JsonRequestController jsonRequestController;

    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Log.d("deviceid",PretStreet.getDeviceId());

    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setContentView(R.layout.loading_view);
            pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @OnClick(R.id.btn_sign_up)
    public void onSignupPressed() {
    }

    @OnClick(R.id.btn_login)
    public void onLoginPressed() {
    }

    @OnClick(R.id.btn_facebook)
    public void onFacebookPressed() {
        Intent facebookLoginIntent = new Intent(WelcomeScreen.this, FacebookLoginScreen.class);
        facebookLoginIntent.putExtra("cat", "Login");
        facebookLoginIntent.putExtra("Type", "FirstLogin");
        startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
    }

    @OnClick(R.id.btn_google)
    public void onGooglePressed() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            Intent googleLoginIntent = new Intent(WelcomeScreen.this, GoogleLoginActivity.class);
            startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
        } else {
            askCompactPermission(Manifest.permission.GET_ACCOUNTS, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    Intent googleLoginIntent = new Intent(WelcomeScreen.this, GoogleLoginActivity.class);
                    startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                }

                @Override
                public void permissionDenied() {
                }
            });
        }
    }

    private void setupSocialLogin(String stringJSON){
        try {
            JSONObject responseJSON = new JSONObject(stringJSON);
            if(responseJSON!=null) {
                JSONObject responseObject = responseJSON;
                JSONObject resultJson = LoginController.getFacebookLoginData(responseObject);
                //TODO :  api call
                showpDialog();
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
        hidepDialog();
        Snackbar.make( getWindow().getDecorView().getRootView(), error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}

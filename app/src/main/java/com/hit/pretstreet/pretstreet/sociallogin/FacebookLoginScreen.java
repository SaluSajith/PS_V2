package com.hit.pretstreet.pretstreet.sociallogin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.hit.pretstreet.pretstreet.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by User on 20/07/2017.
 */
public class FacebookLoginScreen extends Activity {

    public static final int FACEBOOK_LOGIN_RESULT_CODE_SUCCESS = 1;
    public static final int FACEBOOK_LOGIN_RESULT_CODE_FAILURE = 2;
    public static final int FACEBOOK_SHARE_RESULT_CODE_SUCCESS = 4;
    public static final int FACEBOOK_SHARE_RESULT_CODE_FAILURE = 5;

    String Type, text /*imageUrl*/;
    public static String imageUrl;

    public static ArrayList<Bitmap> shareImage = new ArrayList<>();

    private CallbackManager callbackManager;

    private final String PENDING_ACTION_BUNDLE_KEY = "hit.com.fashionapp.facebook:PendingAction";

    private PendingAction pendingAction = PendingAction.NONE;

    private enum PendingAction {
        NONE, POST_PHOTO, POST_STATUS_UPDATE
    }

    private ShareDialog shareDialog;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    public static Profile profile;

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            FacebookLoginScreen.this.setResult(FACEBOOK_SHARE_RESULT_CODE_FAILURE);
            FacebookLoginScreen.this.finish();
        }

        @Override
        public void onError(FacebookException error) {
            FacebookLoginScreen.this.setResult(FACEBOOK_SHARE_RESULT_CODE_FAILURE);
            FacebookLoginScreen.this.finish();
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            /*Profile*/
            profile = Profile.getCurrentProfile();
            Log.e("Profile pic: ", profile + "");

            if (result.getPostId() != null) {
                FacebookLoginScreen.this.setResult(FACEBOOK_SHARE_RESULT_CODE_SUCCESS);
                FacebookLoginScreen.this.finish();
            } else {
                FacebookLoginScreen.this.setResult(FACEBOOK_SHARE_RESULT_CODE_FAILURE);
                FacebookLoginScreen.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult result) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            GraphRequest request = GraphRequest.newMeRequest(
                                    result.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object,
                                                                GraphResponse response) {
                                            Log.v("Response", response.toString());
                                            readResponse(object);
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,first_name,last_name,gender");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("FbError", error.getMessage());
                        if (pendingAction != PendingAction.NONE && error instanceof FacebookAuthorizationException) {
                            pendingAction = PendingAction.NONE;
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.e("FbError", String.valueOf(FACEBOOK_LOGIN_RESULT_CODE_FAILURE));
                        FacebookLoginScreen.this.setResult(FACEBOOK_LOGIN_RESULT_CODE_FAILURE);
                        FacebookLoginScreen.this.finish();
                        if (pendingAction != PendingAction.NONE) {
                            pendingAction = PendingAction.NONE;
                        }
                    }
                });

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        setContentView(R.layout.facebook_login_screen);

        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(SharePhotoContent.class);

        if (getIntent().getStringExtra("cat") != null) {
            if ("Login".equals(getIntent().getStringExtra("cat").toString())) {
                Type = getIntent().getStringExtra("Type").toString();
                LoginManager.getInstance().logInWithReadPermissions(FacebookLoginScreen.this,
                        Arrays.asList("public_profile", "email"));
            } else {
                Type = getIntent().getStringExtra("Type").toString();
                // We can do the action right
                // away.
                if (Type.equals("image")) {
                    pendingAction = PendingAction.POST_PHOTO;
                } else {
                    text = getIntent().getStringExtra("Text").toString();
                    imageUrl = getIntent().getStringExtra("ImageUrl").toString();
                    pendingAction = PendingAction.POST_STATUS_UPDATE;
                }
                // handlePendingAction();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    private void readResponse(JSONObject responseObject) {

        if (responseObject == null) {
            FacebookLoginScreen.this.setResult(FACEBOOK_LOGIN_RESULT_CODE_FAILURE);
            FacebookLoginScreen.this.finish();
        } else {
            /*WelcomeActivity welcomeScreen = new WelcomeActivity();
            welcomeScreen.setupSocialLogin(responseObject);*/
            Intent intent = getIntent();
            intent.putExtra("responsejson", responseObject.toString());
            FacebookLoginScreen.this.setResult(FACEBOOK_LOGIN_RESULT_CODE_SUCCESS, intent);
            FacebookLoginScreen.this.finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in
        // analytics and advertising
        // reporting. Do so in the onResume methods of the primary Activities
        // that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);

    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in
        // analytics and advertising
        // reporting. Do so in the onPause methods of the primary Activities
        // that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }
}

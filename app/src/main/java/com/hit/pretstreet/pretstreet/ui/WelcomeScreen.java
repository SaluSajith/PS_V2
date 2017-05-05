package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.facebook.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.facebook.GoogleLoginActivity;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.ActivityManagePermission;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hit on 10/3/16.
 */
public class WelcomeScreen extends ActivityManagePermission implements View.OnClickListener {

    private Button btn_sign_up, btn_login, btn_facebook, btn_google;
    private RelativeLayout rel, rl_fixed_top;
    private ProgressDialog pDialog;
    private Typeface font;
    private Bitmap bitmap;
    private String encodedImage, baseImage, headerImage;

    public static String facebookResponse;
    String strEmail, strPassword, facebookId, strName, strGender, googleImageUrl, firstname, strlocation,
            lastname, googleId, strSocialId, strSocialType, strProfilePic;

    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;
    private static final int PROFILE_PIC_SIZE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        PreferenceServices.init(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        init();
        getFixedImages("header");
        //getAppKeyHash();
        btn_sign_up.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_google.setOnClickListener(this);
    }

    private void init() {
        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_google = (Button) findViewById(R.id.btn_google);
        rel = (RelativeLayout) findViewById(R.id.rel);
        rl_fixed_top = (RelativeLayout) findViewById(R.id.rl_fixed_top);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        btn_sign_up.setTypeface(font);
        btn_login.setTypeface(font);
        btn_facebook.setTypeface(font);
        btn_google.setTypeface(font);

        headerImage = PreferenceServices.getInstance().getHeaderImage();
        baseImage = PreferenceServices.getInstance().getBaseImage();
       /* if (headerImage.equalsIgnoreCase("")) {
        } else {
            Glide.with(getApplicationContext())
                    .load(headerImage)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable dr = new BitmapDrawable(resource);
                            rl_fixed_top.setBackgroundDrawable(dr);
                        }
                    });
        }*/

        if (baseImage.equalsIgnoreCase("")) {
        } else {
            Glide.with(getApplicationContext())
                    .load(baseImage)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable dr = new BitmapDrawable(resource);
                            rel.setBackgroundDrawable(dr);
                        }
                    });
        }

    }

    private void getFixedImages(final String id) {
        String urlJsonObj = Constant.FASHION_API + "route=" + id;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley WElcome", response.toString());
                boolean responseSuccess = false;
                String strsuccess, imgUrl = null;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        responseSuccess = true;
                        imgUrl = response.getString("url");
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    if (id.equalsIgnoreCase("header")) {
                        if (headerImage.equalsIgnoreCase(imgUrl)) {
                        } else {
                            PreferenceServices.instance().saveHeaderImage(imgUrl);
                        }
                        getFixedImages("loginscreen");
                    } else {
                        if (baseImage.equalsIgnoreCase(imgUrl)) {
                        } else {
                            PreferenceServices.instance().saveBaseImage(imgUrl);
                            Glide.with(getApplicationContext())
                                    .load(imgUrl)
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            Drawable dr = new BitmapDrawable(resource);
                                            rel.setBackgroundDrawable(dr);
                                        }
                                    });
                        }
                    }
                } else {
                    if (id.equalsIgnoreCase("header")) {
                        getFixedImages("loginscreen");
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                System.out.println("HASH  " + something);
                showSignedHashKey(something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public void showSignedHashKey(String hashKey) {
        Log.e("HAsh Key", hashKey);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Note Signed Hash Key");
        adb.setMessage(hashKey);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        adb.show();
    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setContentView(R.layout.progress_activity);
            pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_sign_up:
                startActivity(new Intent(WelcomeScreen.this, RegisterScreen.class));
                break;

            case R.id.btn_login:
                startActivity(new Intent(WelcomeScreen.this, LoginScreen.class));
                break;

            case R.id.btn_facebook:
                Intent facebookLoginIntent = new Intent(WelcomeScreen.this, FacebookLoginScreen.class);
                facebookLoginIntent.putExtra("cat", "Login");
                facebookLoginIntent.putExtra("Type", "FirstLogin");
                startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
                break;

            case R.id.btn_google:
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
                break;

            default:
                break;

        }
    }

    private void getFacebookLogin() {
        JSONObject jObject;
        try {
            jObject = new JSONObject(facebookResponse.toString());
            facebookId = jObject.getString("id").toString();
            strSocialId = facebookId;
            strProfilePic = URLEncoder.encode("https://graph.facebook.com/" + strSocialId + "/picture?type=large", "UTF-8");
            strSocialType = "facebook";
            if (jObject.has("email")) {
                strEmail = jObject.getString("email").toString();
            } else {
                strEmail = "";
            }
            if (jObject.has("name")) {
                strName = jObject.getString("name").toString();
            } else {
                strName = "";
            }
            if (jObject.has("gender")) {
                strGender = jObject.getString("gender").toString();
            } else {
                strGender = "";
            }
            strPassword = "";
            Log.e("FB Details: ", strSocialId + " " + strName + " " + strEmail + " " + strGender);
            getCheckFBLogin();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getGoogleResponse() {
        strName = GoogleLoginActivity.username;
        //firstname = GoogleLoginActivity.firstName;
        //lastname = GoogleLoginActivity.lastName;
        googleId = GoogleLoginActivity.userId;
        googleImageUrl = GoogleLoginActivity.imageUrl;
        strSocialId = googleId;
        strSocialType = "google";
        strEmail = GoogleLoginActivity.email;
        // by default the profile url gives 50x50 px
        // image only
        // we can replace the value with whatever
        // dimension we want by
        // replacing sz=X
        strProfilePic = googleImageUrl.substring(0, googleImageUrl.length() - 2) + PROFILE_PIC_SIZE;
        getCheckFBLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FACEBOOK_LOGIN_REQUEST_CODE:
                if (resultCode == FacebookLoginScreen.FACEBOOK_LOGIN_RESULT_CODE_SUCCESS) {
                    getFacebookLogin();
                } else {
                    Log.e("TAG", "Facebook LOGIN FAIL");
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();
                }
                break;

            case GOOGLE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    getGoogleResponse();
                } else {
                    Log.e("TAG", "Google  LOGIN FAIL");
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    public void getCheckFBLogin() {
        showpDialog();
        try {
            strSocialId = URLEncoder.encode(strSocialId, "UTF-8");
            strSocialType = URLEncoder.encode(strSocialType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String url;
        try {
            //strProfilePic = URLEncoder.encode("https://graph.facebook.com/" + strSocialId + "/picture?type=large", "UTF-8");
            url = Constant.FASHION_API + "route=social_login&email=" + strEmail + "&social_id=" + strSocialId + "&social_type=" + strSocialType
                    + "&fname=" + URLEncoder.encode(strName, "UTF-8") + "&profile_pic=" + strProfilePic;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            url = Constant.FASHION_API;
        }
        Log.e("URL Details: ", url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
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
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    finish();
                } else {
                    hidepDialog();
                    Toast.makeText(WelcomeScreen.this, "Facebook Login Failed.", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Volley", "Error: " + error.getMessage());
                hidepDialog();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(WelcomeScreen.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }
}

package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.facebook.FacebookLoginScreen;
import com.hit.pretstreet.pretstreet.facebook.GooglePlusLogin;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.ActivityManagePermission;
import com.hit.pretstreet.pretstreet.marshmallowpermissions.marshmallowpermissions.PermissionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by hit on 15/3/16.
 */
public class LoginScreen extends ActivityManagePermission implements View.OnClickListener {

    private Button btn_facebook, btn_google, btn_login;
    private TextView txt_forgot_password;
    private EditText edt_email_number, edt_password;
    private CheckBox checkbox;
    private ScrollView scroll;
    private LinearLayout rl_fixed_top;
    private Typeface font;
    private ProgressDialog pDialog;
    String strEmail, strPassword, facebookId, strName, strGender, googleImageUrl, firstname, strlocation,
            lastname, googleId, strSocialId, strSocialType, strProfilePic, baseImage, headerImage;
    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;
    private static final int PROFILE_PIC_SIZE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        init();
        PreferenceServices.init(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btn_facebook.setOnClickListener(this);
        btn_google.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_forgot_password.setOnClickListener(this);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                edt_password.setTypeface(font);
                edt_password.setSelection(edt_password.length());
            }
        });
    }

    private void init() {
        btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_google = (Button) findViewById(R.id.btn_google);
        btn_login = (Button) findViewById(R.id.btn_login);
        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setPaintFlags(txt_forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        edt_email_number = (EditText) findViewById(R.id.edt_email_number);
        edt_password = (EditText) findViewById(R.id.edt_password);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        scroll = (ScrollView) findViewById(R.id.scroll);
        rl_fixed_top = (LinearLayout) findViewById(R.id.rl_fixed_top);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_forgot_password.setTypeface(font);
        edt_email_number.setTypeface(font);
        edt_password.setTypeface(font);
        checkbox.setTypeface(font);

        headerImage = PreferenceServices.getInstance().getHeaderImage();
        baseImage = PreferenceServices.getInstance().getBaseImage();
        if (baseImage.equalsIgnoreCase("")) {
        } else {
            Glide.with(getApplicationContext())
                    .load(baseImage)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable dr = new BitmapDrawable(resource);
                            scroll.setBackgroundDrawable(dr);
                        }
                    });
        }
        /*if (headerImage.equalsIgnoreCase("")) {
        } else {
            Glide.with(getApplicationContext()).load(headerImage).into(img_header);
        }*/

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

            case R.id.btn_facebook:
                Intent facebookLoginIntent = new Intent(LoginScreen.this, FacebookLoginScreen.class);
                facebookLoginIntent.putExtra("cat", "Login");
                facebookLoginIntent.putExtra("Type", "FirstLogin");
                startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
                break;

            case R.id.btn_google:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent googleLoginIntent = new Intent(LoginScreen.this, GooglePlusLogin.class);
                    startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                } else {
                    askCompactPermission(Manifest.permission.GET_ACCOUNTS, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            Intent googleLoginIntent = new Intent(LoginScreen.this, GooglePlusLogin.class);
                            startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                        }

                        @Override
                        public void permissionDenied() {

                        }
                    });
                }
                break;

            case R.id.btn_login:
                String email_number = edt_email_number.getText().toString();
                String password = edt_password.getText().toString();
                if (email_number.length() < 1) {
                    Toast.makeText(this, "Enter Email id", Toast.LENGTH_SHORT).show();
                    edt_email_number.requestFocus();
                } else if (password.length() < 1) {
                    Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();
                    edt_password.requestFocus();
                } else {
                    loginUserInformation(email_number, password);
                }
                break;

            case R.id.txt_forgot_password:
                startActivity(new Intent(LoginScreen.this, ForgotPasswordScreen.class));
                break;

            default:
                break;
        }
    }

    private void loginUserInformation(String email_number, String password) {
        String urlJsonObj = Constant.LOGIN_URL + "email=" + email_number + "&password=" + password;
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley", response.toString());
                String strsuccess, userId, message;
                try {
                    strsuccess = response.getString("status");
                    if (strsuccess.equalsIgnoreCase("success")) {
                        userId = response.getString("userID");
                        PreferenceServices.instance().saveUserId(userId);
                        PreferenceServices.instance().saveLoginType("");
                        Toast.makeText(LoginScreen.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                        if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                                || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                            startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        finish();
                    } else {
                        message = response.getString("message");
                        Toast.makeText(LoginScreen.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getFacebookLogin() {
        JSONObject jObject;
        try {
            jObject = new JSONObject(WelcomeScreen.facebookResponse.toString());
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
            getCheckFBLogin();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                if (resultCode == GooglePlusLogin.GOOGLEPLUS_LOGIN_RESULT_CODE_SUCCESS) {
                    getGoogleResponse();
                } else {
                    Log.d("TAG", "Google  LOGIN FAIL");
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    public void getGoogleResponse() {
        strName = GooglePlusLogin.username;
        //firstname = GooglePlusLogin.firstName;
        //lastname = GooglePlusLogin.lastName;
        googleId = GooglePlusLogin.userId;
        googleImageUrl = GooglePlusLogin.imageUrl;
        strSocialId = googleId;
        strSocialType = "google";
        strEmail = GooglePlusLogin.email;
        // by default the profile url gives 50x50 px
        // image only
        // we can replace the value with whatever
        // dimension we want by
        // replacing sz=X
        strProfilePic = googleImageUrl.substring(0, googleImageUrl.length() - 2) + PROFILE_PIC_SIZE;
        getCheckFBLogin();
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
                    Toast.makeText(LoginScreen.this, "Facebook Login Failed.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginScreen.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }
}

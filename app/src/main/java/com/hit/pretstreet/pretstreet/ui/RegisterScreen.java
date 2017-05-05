package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.hit.pretstreet.pretstreet.facebook.GoogleLoginActivity;
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
 * Created by hit on 14/3/16.
 */
public class RegisterScreen extends ActivityManagePermission implements View.OnClickListener {
    private Button btn_facebook, btn_google, btn_addstore;
    private ImageView btn_login, btn_sign_up;
    private TextView txt_sign_number, txt_pretstreet_app, txt_conditon, txt_and, txt_privacy;
    private EditText edt_name, edt_number, edt_email, edt_password;
    private ScrollView scroll;
    private LinearLayout rl_fixed_top;
    private Spinner spinnewr_code;
    private ProgressDialog pDialog;
    private Typeface font;
    private String selectedArea, baseImage, headerImage;

    String strEmail, strPassword, facebookId, strName, strGender, googleImageUrl, firstname, strlocation,
            lastname, googleId, strSocialId, strSocialType, strProfilePic;
    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;

    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;
    private static final int PROFILE_PIC_SIZE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);
        init();
        PreferenceServices.init(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btn_facebook.setOnClickListener(this);
        btn_google.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        btn_addstore.setOnClickListener(this);
        txt_conditon.setOnClickListener(this);
        txt_privacy.setOnClickListener(this);
        spinnewr_code.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private void init() {
        btn_addstore = (Button) findViewById(R.id.btn_addstore);
        btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_google = (Button) findViewById(R.id.btn_google);
        btn_sign_up = (ImageView) findViewById(R.id.btn_sign_up);
        btn_login = (ImageView) findViewById(R.id.btn_login);

        txt_sign_number = (TextView) findViewById(R.id.txt_sign_number);
        txt_pretstreet_app = (TextView) findViewById(R.id.txt_pretstreet_app);
        txt_conditon = (TextView) findViewById(R.id.txt_conditon);
        txt_and = (TextView) findViewById(R.id.txt_and);
        txt_privacy = (TextView) findViewById(R.id.txt_privacy);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_number = (EditText) findViewById(R.id.edt_number);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        spinnewr_code = (Spinner) findViewById(R.id.spinnewr_code);

        scroll = (ScrollView) findViewById(R.id.scroll);
        rl_fixed_top = (LinearLayout) findViewById(R.id.rl_fixed_top);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_privacy.setPaintFlags(txt_privacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_conditon.setPaintFlags(txt_conditon.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txt_sign_number.setTypeface(font);
        txt_pretstreet_app.setTypeface(font);
        txt_conditon.setTypeface(font);
        txt_and.setTypeface(font);
        txt_privacy.setTypeface(font);
        edt_name.setTypeface(font);
        edt_number.setTypeface(font);
        edt_email.setTypeface(font);
        edt_password.setTypeface(font);

        headerImage = PreferenceServices.getInstance().getHeaderImage();
        baseImage = PreferenceServices.getInstance().getBaseImage();
        /*  if (headerImage.equalsIgnoreCase("")) {
            img_header.setImageDrawable(getResources().getDrawable(R.drawable.fixed_top));
        } else {
            Glide.with(getApplicationContext()).load(headerImage).into(img_header);
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
                            scroll.setBackgroundDrawable(dr);
                        }
                    });
        }
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = parent.getItemAtPosition(position).toString();
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTypeface(font);
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            selectedArea = selectedItem;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
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
                Intent facebookLoginIntent = new Intent(RegisterScreen.this, FacebookLoginScreen.class);
                facebookLoginIntent.putExtra("cat", "Login");
                facebookLoginIntent.putExtra("Type", "FirstLogin");
                startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
                break;

            case R.id.btn_google:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent googleLoginIntent = new Intent(RegisterScreen.this, GoogleLoginActivity.class);
                    startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                } else {
                    askCompactPermission(Manifest.permission.GET_ACCOUNTS, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            Intent googleLoginIntent = new Intent(RegisterScreen.this, GoogleLoginActivity.class);
                            startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                        }

                        @Override
                        public void permissionDenied() {

                        }
                    });
                }
                break;

            case R.id.btn_sign_up:
                String email = edt_email.getText().toString().trim();
                String firstname = edt_name.getText().toString().trim();
                String password = edt_password.getText().toString();
                String mobile = edt_number.getText().toString().trim();
                if (firstname.length() < 1) {
                    Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                    edt_name.requestFocus();
                } else if (mobile.length() != 10) {
                    Toast.makeText(this, "Enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                    edt_number.requestFocus();
                } else if (mobile.length() < 3) {
                    Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                    edt_number.requestFocus();
                } else if (!Constant.isValidEmail(email)) {
                    Toast.makeText(this, "Enter Correct Email id", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                } else if (password.length() < 5) {
                    Toast.makeText(this, "Enter Minimum 6 Character Password", Toast.LENGTH_SHORT).show();
                    edt_password.requestFocus();
                } else {
                    registerUserInformation(firstname, mobile, email, password);
                }
                break;

            case R.id.btn_login:
                startActivity(new Intent(this, LoginScreen.class));
                break;

            case R.id.btn_addstore:
                startActivity(new Intent(this, AddStoreScreen.class));
                break;

            case R.id.txt_conditon:
                startActivity(new Intent(RegisterScreen.this, PrivacyandConditionsScreen.class).putExtra("screen", "conditions"));
                break;

            case R.id.txt_privacy:
                startActivity(new Intent(RegisterScreen.this, PrivacyandConditionsScreen.class).putExtra("screen", "privacy"));
                break;

            default:
                break;
        }
    }

    private void registerUserInformation(String firstname, String mobile, String email, String password) {
        Log.e("mobile=", selectedArea + mobile);
        //http://doctronics.co.in/fashionapp/customercreates.php?email=test94@test3.com&firstname=testing&password=12345678&mobile=96547238
        String urlJsonObj;
        try {
            urlJsonObj = Constant.REGISTRATION_URL + "email=" + email + "&firstname=" + URLEncoder.encode(firstname, "UTF-8")
                    + "&password=" + password + "&mobile=" + mobile;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            urlJsonObj = Constant.FASHION_API;
        }
        Log.e("URL: ", urlJsonObj);
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
                        userId = response.getString("user_id");
                        PreferenceServices.instance().saveUserId(userId);
                        PreferenceServices.instance().saveLoginType("");
                        Toast.makeText(RegisterScreen.this, "Registeration Successfull", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterScreen.this, UploadPhotoScreen.class));
                        finish();
                    } else {
                        message = response.getString("message");
                        Toast.makeText(RegisterScreen.this, message, Toast.LENGTH_SHORT).show();
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
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
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
                    Log.d("TAG", "Google  LOGIN FAIL");
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
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

    public void getGoogleResponse() {
        strName = GoogleLoginActivity.username;
        //firstname = GoogleLoginActivity.firstName;
        //lastname = GoogleLoginActivity.lastName;
        googleId = GoogleLoginActivity.userId;
        googleImageUrl = GoogleLoginActivity.imageUrl;
        strSocialId = googleId;
        strSocialType = "google";
        strEmail = GoogleLoginActivity.email;
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
                    Log.e("USer ID:", userId);
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    finish();
                } else {
                    hidepDialog();
                    Toast.makeText(RegisterScreen.this, "Facebook Login Failed.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterScreen.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }
}




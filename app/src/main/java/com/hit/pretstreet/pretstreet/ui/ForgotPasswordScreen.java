package com.hit.pretstreet.pretstreet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by hit on 16/3/16.
 */
public class ForgotPasswordScreen extends ActivityManagePermission implements View.OnClickListener {
    private TextView txt_forgot_password;
    private EditText edt_email;
    private Button btn_facebook, btn_google, btn_send;
    private ScrollView scroll;
    private LinearLayout rl_fixed_top;
    private Typeface font;
    private ProgressDialog pDialog;
    String strOtp, strUserEmail;

    String strEmail, strPassword, facebookId, strName, strGender, googleImageUrl, firstname, strlocation,
            lastname, googleId, strSocialId, strSocialType, strProfilePic, baseImage, headerImage;
    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 1;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 2;
    private static final int PROFILE_PIC_SIZE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        init();
        btn_facebook.setOnClickListener(this);
        btn_google.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    private void init() {
        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setPaintFlags(txt_forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        edt_email = (EditText) findViewById(R.id.edt_email);
        btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_google = (Button) findViewById(R.id.btn_google);
        btn_send = (Button) findViewById(R.id.btn_send);

        scroll = (ScrollView) findViewById(R.id.scroll);
        rl_fixed_top = (LinearLayout) findViewById(R.id.rl_fixed_top);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_forgot_password.setTypeface(font);
        edt_email.setTypeface(font);

        headerImage = PreferenceServices.getInstance().getHeaderImage();

        ///showOTPScreem();
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
                Intent facebookLoginIntent = new Intent(ForgotPasswordScreen.this, FacebookLoginScreen.class);
                facebookLoginIntent.putExtra("cat", "Login");
                facebookLoginIntent.putExtra("Type", "FirstLogin");
                startActivityForResult(facebookLoginIntent, FACEBOOK_LOGIN_REQUEST_CODE);
                break;

            case R.id.btn_google:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent googleLoginIntent = new Intent(ForgotPasswordScreen.this, GoogleLoginActivity.class);
                    startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                } else {
                    askCompactPermission(Manifest.permission.GET_ACCOUNTS, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            Intent googleLoginIntent = new Intent(ForgotPasswordScreen.this, GoogleLoginActivity.class);
                            startActivityForResult(googleLoginIntent, GOOGLE_LOGIN_REQUEST_CODE);
                        }

                        @Override
                        public void permissionDenied() {

                        }
                    });
                }
                break;

            case R.id.btn_send:
                String text = edt_email.getText().toString();
                boolean digitsOnly = TextUtils.isDigitsOnly(text);
                /*if (digitsOnly) {
                    if (text.length() == 10) {
                        Toast.makeText(getApplicationContext(), "json calling for send otp from mobile number...", Toast.LENGTH_LONG).show();
                        showOTPScreem();// if json calling success then call this function.
                    } else if (text.length() == 0) {
                        edt_email.requestFocus();
                        Toast.makeText(getApplicationContext(), "field can't be empty.", Toast.LENGTH_LONG).show();
                    } else {
                        edt_email.requestFocus();
                        Toast.makeText(getApplicationContext(), "enter 10 digits mobile number.", Toast.LENGTH_LONG).show();
                    }
                } else {*/
                if (text.length() == 0) {//remove if when uncommented above code
                    edt_email.requestFocus();
                    Toast.makeText(getApplicationContext(), "field can't be empty.", Toast.LENGTH_LONG).show();
                } else if (!Constant.isValidEmail(text)) {
                    Toast.makeText(this, "Please Enter a Valid Email Id", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                } else {
                    //forgotPasswordJSON(text);
                    strUserEmail = edt_email.getText().toString();
                    sendOtpRequest(text);
                }
                break;

            default:
                break;
        }
    }

    public void showOTPScreem() {
        final Dialog popupDialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_otp_screen, null);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        final EditText edt_otp = (EditText) view.findViewById(R.id.edt_otp);
        ImageView btn_send = (ImageView) view.findViewById(R.id.btn_send);

        edt_otp.setTypeface(font);

        popupDialog.setCanceledOnTouchOutside(false);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                    Toast.makeText(getApplicationContext(), "Enter OTP value", Toast.LENGTH_SHORT).show();
                    edt_otp.requestFocus();
                }
                else {
                    popupDialog.dismiss();
                    if (strOtp.equals(edt_otp.getText().toString())) {
                        showChangePassword();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void showChangePassword() {
        final Dialog popupDialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.popup_change_password, null);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        final EditText edt_new = (EditText) view.findViewById(R.id.edt_new);
        final EditText edt_confirm = (EditText) view.findViewById(R.id.edt_confirm);
        ImageView btn_send = (ImageView) view.findViewById(R.id.btn_send);

        edt_new.setTypeface(font);
        edt_confirm.setTypeface(font);

        popupDialog.setCanceledOnTouchOutside(false);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                if (edt_new.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    edt_new.requestFocus();
                }
                else if(edt_confirm.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password", Toast.LENGTH_SHORT).show();
                    edt_confirm.requestFocus();
                }
                else if(!edt_new.getText().toString().equals(edt_confirm.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                    edt_confirm.requestFocus();
                }
                else {
                    popupDialog.dismiss();
                    sendResetPassword(strUserEmail, edt_confirm.getText().toString());
                }

            }
        });
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
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
                }
                break;

            case GOOGLE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    getGoogleResponse();
                } else {
                    Log.d("TAG", "Google  LOGIN FAIL");
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
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
        String deviceId = Settings.System.getString(getApplicationContext().getContentResolver(),Settings.System.ANDROID_ID);
        try {
            url = Constant.FASHION_API + "route=social_login&email=" + strEmail + "&social_id=" + strSocialId + "&social_type=" + strSocialType
                    + "&fname=" + URLEncoder.encode(strName, "UTF-8") + "&profile_pic=" + strProfilePic + "&device=1" + "&deviceid=" + deviceId;
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
                    Toast.makeText(ForgotPasswordScreen.this, "Facebook Login Failed.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ForgotPasswordScreen.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }



    private void sendOtpRequest(String email){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL;
            URL = Constant.TRENDING_API + "otpForgotpasswordGenerate";
            Log.d("URL", URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserEmail",email);
            jsonBody.put("ApiKey", Constant.API);
            final String requestBody = jsonBody.toString();

            showpDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("OTP_api_response", String.valueOf(response));
                            try {
                                JSONObject jsonObject =  new JSONObject(response);
                                String strsuccess;
                                try {
                                    strsuccess = jsonObject.getString("Status");
                                    if (strsuccess.equals("1")) {
                                        strOtp = jsonObject.getString("OTP");
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                        showOTPScreem();
                                    } else {
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hidepDialog();
                    Log.d("Like_api_error", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendResetPassword(String email, String password){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL;
            URL = Constant.TRENDING_API + "setnewpassword";
            Log.d("URL", URL);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("UserPassword",password);
            jsonBody.put("UserEmail",email);
            jsonBody.put("ApiKey", Constant.API);
            final String requestBody = jsonBody.toString();

            showpDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Resetpassword_api_response", String.valueOf(response));
                            try {
                                JSONObject jsonObject =  new JSONObject(response);
                                String strsuccess;
                                try {
                                    strsuccess = jsonObject.getString("Status");
                                    if (strsuccess.equals("1")) {
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hidepDialog();
                    Log.d("Like_api_error", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

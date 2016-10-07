package com.hit.pretstreet.pretstreet.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
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
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends Activity {

    private ImageView img_splash;
    private static int DURATION;
    private Handler splashHandler;
    private static final int SPLASH_DURATION_END = 1000;
    private String savedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        img_splash = (ImageView) findViewById(R.id.img_splash);
        PreferenceServices.init(this);
        splashHandler = new Handler();
        DURATION = Integer.valueOf(getString(R.string.splash_duration));
        splashHandler.postDelayed(mChangeSplash, DURATION);
        splashHandler.postDelayed(mEndSplash, SPLASH_DURATION_END);
        savedImage = PreferenceServices.getInstance().getSplashScreen();
        if (savedImage.equalsIgnoreCase("")) {
            img_splash.setImageDrawable(getResources().getDrawable(R.drawable.splash));
        } else {
            Glide.with(getApplicationContext()).load(savedImage).into(img_splash);
        }
        getSplashImage();
    }

    private void getSplashImage() {
        String urlJsonObj = Constant.FASHION_API + "route=homescreen";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                    PreferenceServices.instance().saveSplashScreen(imgUrl);
                    Glide.with(getApplicationContext()).load(imgUrl).into(img_splash);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private Runnable mEndSplash = new Runnable() {
        public void run() {
            if (!isFinishing()) {
                splashHandler.removeCallbacks(this);
                if (PreferenceServices.getInstance().geUsertId().equalsIgnoreCase("")) {
                    Intent i = new Intent(SplashScreen.this, WelcomeScreen.class);
                    startActivity(i);
                    SplashScreen.this.finish();
                } else {
                    if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                            || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                        startActivity(new Intent(getApplicationContext(), DefaultLocation.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    SplashScreen.this.finish();
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
}

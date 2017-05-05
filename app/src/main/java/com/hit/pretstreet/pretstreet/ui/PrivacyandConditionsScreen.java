package com.hit.pretstreet.pretstreet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.Items.MyTagHandler;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hit on 30/3/16.
 */
public class PrivacyandConditionsScreen extends Activity {
    private ImageView img_icon_menu;
    private TextView txt_cat, txt_content;
    private String screen;
    private Typeface font;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_conditions_screen);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        img_icon_menu = (ImageView) findViewById(R.id.img_icon_menu);
        txt_cat = (TextView) findViewById(R.id.txt_cat_name);
        txt_content = (TextView) findViewById(R.id.txt_content);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_cat.setTypeface(font);
        txt_content.setTypeface(font);
        screen = getIntent().getStringExtra("screen");
        fetchContentData();
    }

    private void fetchContentData() {
        String urlJsonObj = null;
        if (screen.equalsIgnoreCase("conditions")) {
            txt_cat.setText("Conditions of use");
            urlJsonObj = Constant.FASHION_API + "route=conditions";
        } else if (screen.equalsIgnoreCase("privacy")) {
            txt_cat.setText("Privacy Notice");
            urlJsonObj = Constant.FASHION_API + "route=privacy";
        }
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlJsonObj, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley", response.toString());
                String strsuccess, content;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        content = response.getString("content");
                        txt_content.setText(Html.fromHtml(content, null, new MyTagHandler()));
                    } else {
                        PrivacyandConditionsScreen.this.finish();
                        Toast.makeText(getApplicationContext(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

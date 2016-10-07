package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
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
import com.hit.pretstreet.pretstreet.ui.HomeActivity;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Jesal on 06-Sep-16.
 */
public class ContactUsFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu, img_submit, img_cancel;
    private TextView txt_cat_name;
    private EditText edt_name, edt_email, edt_mobile, edt_msg;
    private ScrollView scroll;
    private String Screen, id, sname, saddress, baseImage;
    private Typeface font;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.claim_business_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_submit = (ImageView) rootView.findViewById(R.id.img_submit);
        img_cancel = (ImageView) rootView.findViewById(R.id.img_cancel);

        txt_cat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);
        edt_name = (EditText) rootView.findViewById(R.id.edt_name);
        edt_email = (EditText) rootView.findViewById(R.id.edt_email);
        edt_mobile = (EditText) rootView.findViewById(R.id.edt_mobile);
        edt_msg = (EditText) rootView.findViewById(R.id.edt_msg);
        scroll = (ScrollView) rootView.findViewById(R.id.scroll);

        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat_name.setTypeface(font);
        edt_name.setTypeface(font);
        edt_email.setTypeface(font);
        edt_mobile.setTypeface(font);
        edt_msg.setTypeface(font);

        Bundle b = this.getArguments();
        Screen = b.getString("screen");
        if (Screen.equalsIgnoreCase("feedback")) {
            txt_cat_name.setText("FEEDBACK");
            img_cancel.setVisibility(View.VISIBLE);
        } else {
            txt_cat_name.setText("CONTACT US");
            img_cancel.setVisibility(View.GONE);
        }

        baseImage = PreferenceServices.getInstance().getBaseImage();
        if (baseImage.equalsIgnoreCase("")) {
        } else {
            Glide.with(getActivity())
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

        img_icon_menu.setOnClickListener(this);
        img_submit.setOnClickListener(this);
        img_cancel.setOnClickListener(this);
        return rootView;
    }

    private void sendContactData(String name, String email, String mobile, String msg) {
        String urlJsonObj = null;
        try {
            if (Screen.equalsIgnoreCase("feedback")) {
                //http://52.77.174.143/fashion_api.php?route=feedback&name=praveen&mob=9167624578&email=praveen@handsintechnology.com&message=Demo%20Message
                urlJsonObj = Constant.FASHION_API + "route=feedback&name=" + URLEncoder.encode(name, "UTF-8") + "&mob=" + mobile +
                        "&email=" + email + "&message=" + URLEncoder.encode(msg, "UTF-8");
            } else {
                urlJsonObj = Constant.FASHION_API + "route=contact&name=" + URLEncoder.encode(name, "UTF-8") + "&mob=" + mobile + "&email=" + email + "&message=" + URLEncoder.encode(msg, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            urlJsonObj = Constant.FASHION_API;
        }
        showpDialog();
        Log.e("url:", urlJsonObj);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley", response.toString());
                String strsuccess, message;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        message = response.getString("message");
                        Constant.hide_keyboard(getActivity());
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        edt_name.setText("");
                        edt_email.setText("");
                        edt_mobile.setText("");
                        edt_msg.setText("");
                    } else {
                        message = response.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
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
            case R.id.img_icon_menu:
                //startActivity(new Intent(getActivity(), HomeActivity.class));
                break;

            case R.id.img_cancel:
                getFragmentManager().popBackStack();
                break;

            case R.id.img_submit:
                String name, mobile, email, msg;
                name = edt_name.getText().toString();
                mobile = edt_mobile.getText().toString();
                email = edt_email.getText().toString();
                msg = edt_msg.getText().toString();
                if (name.length() < 1) {
                    Toast.makeText(getActivity(), "Enter your name.", Toast.LENGTH_SHORT).show();
                    edt_name.requestFocus();
                } else if (!Constant.isValidEmail(email)) {
                    Toast.makeText(getActivity(), "Enter valid email id.", Toast.LENGTH_SHORT).show();
                    edt_email.requestFocus();
                } else if (mobile.length() != 10) {
                    Toast.makeText(getActivity(), "Enter 10 digit mobile number.", Toast.LENGTH_SHORT).show();
                    edt_mobile.requestFocus();
                } else if (msg.length() < 1) {
                    Toast.makeText(getActivity(), "Enter your message.", Toast.LENGTH_SHORT).show();
                    edt_msg.requestFocus();
                } else {
                    sendContactData(name, email, mobile, msg);
                }
                break;

            default:
                break;
        }
    }
}

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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu, img_submit, img_back;
    private TextView txt_cat;
    private RelativeLayout rel;
    private String baseImage;
    private EditText edt_current_password, edt_new_password, edt_confirm_password;
    private Typeface font;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.change_password_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_submit = (ImageView) rootView.findViewById(R.id.img_submit);
        img_back = (ImageView) rootView.findViewById(R.id.img_back);
        rel = (RelativeLayout) rootView.findViewById(R.id.rel);

        txt_cat = (TextView) rootView.findViewById(R.id.txt_cat_name);
        edt_current_password = (EditText) rootView.findViewById(R.id.edt_current_password);
        edt_new_password = (EditText) rootView.findViewById(R.id.edt_new_password);
        edt_confirm_password = (EditText) rootView.findViewById(R.id.edt_confirm_password);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat.setText("CHANGE PASSWORD");
        txt_cat.setTypeface(font);
        edt_current_password.setTypeface(font);
        edt_new_password.setTypeface(font);
        edt_confirm_password.setTypeface(font);
        img_icon_menu.setOnClickListener(this);
        img_submit.setOnClickListener(this);
        img_back.setOnClickListener(this);

        return rootView;
    }

    private void changePassword(String curr, String newp) {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=change_password&user_id=16&old_pwd=123456&new_pwd=123456
        String urlJsonObj = Constant.FASHION_API + "route=change_password&user_id=" + PreferenceServices.getInstance().geUsertId() + "&old_pwd=" + curr + "&new_pwd=" + newp;
        showpDialog();
        Log.e("url", urlJsonObj);
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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.img_back:
                getActivity().onBackPressed();
                //startActivity(new Intent(this, HomeActivity.class));
                break;

            case R.id.img_submit:
                String curr = edt_current_password.getText().toString();
                String newp = edt_new_password.getText().toString();
                String confirm = edt_confirm_password.getText().toString();
                if (curr.length() < 1) {
                    Toast.makeText(getActivity(), "Enter your current password.", Toast.LENGTH_SHORT).show();
                    edt_current_password.requestFocus();
                } else if (newp.length() < 6) {
                    Toast.makeText(getActivity(), "Password should be 6 character", Toast.LENGTH_SHORT).show();
                    edt_new_password.requestFocus();
                } else if (!confirm.equalsIgnoreCase(newp)) {
                    Toast.makeText(getActivity(), "Password not matching.", Toast.LENGTH_SHORT).show();
                    edt_confirm_password.requestFocus();
                } else {
                    changePassword(curr, newp);
                }
                break;

            default:
                break;
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
}

package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hit.pretstreet.pretstreet.ui.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jesal on 06-Sep-16.
 */
public class About_TnCFragment extends Fragment {

    private ImageView img_icon_menu, img_back;
    private TextView txt_cat, txt_content;
    private String screen;
    private Typeface font;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.privacy_conditions_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_back = (ImageView) rootView.findViewById(R.id.img_back);
        txt_cat = (TextView) rootView.findViewById(R.id.txt_cat_name);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat.setTypeface(font);
        txt_content.setTypeface(font);
        Bundle b = this.getArguments();
        screen = b.getString("screen");
        fetchContentData();

        img_icon_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screen.equalsIgnoreCase("terms")) {
                    getFragmentManager().popBackStack();
                }
                if (screen.equalsIgnoreCase("aboutus")) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

    private void fetchContentData() {
        String urlJsonObj = null;
        if (screen.equalsIgnoreCase("conditions")) {
            txt_cat.setText("CONDITIONS OF USE");
            urlJsonObj = Constant.FASHION_API + "route=conditions";
        } else if (screen.equalsIgnoreCase("privacy")) {
            txt_cat.setText("PRIVACY NOTICE");
            urlJsonObj = Constant.FASHION_API + "route=privacy";
        } else if (screen.equalsIgnoreCase("aboutus")) {
            txt_cat.setText("ABOUT US");
            urlJsonObj = Constant.FASHION_API + "route=about";
        } else if (screen.equalsIgnoreCase("terms")) {
            txt_cat.setText("TERMS OF SERVICES");
            urlJsonObj = Constant.FASHION_API + "route=terms";
        } else {
            txt_cat.setText("LICENSES");
            urlJsonObj = Constant.FASHION_API + "route=licence";
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
                        getFragmentManager().popBackStack();
                        Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
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
                VolleyLog.e("Volley", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
}

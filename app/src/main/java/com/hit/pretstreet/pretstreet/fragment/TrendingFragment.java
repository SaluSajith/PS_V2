package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.Items.CategoryItem;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Jesal on 23-Sep-16.
 */

public class TrendingFragment extends Fragment implements View.OnClickListener {

    private ImageView img_icon_menu, img_notification, img_filter, img_search, img_trending;
    private TextView txt_location, txt_cat_name;
    RelativeLayout ll;
    private String mainCAtId, name, image;
    private Typeface font;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trending_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        Bundle bundle = this.getArguments();
        mainCAtId = bundle.getString("main_cat_id");
        name = bundle.getString("main_cat_name");
        image = bundle.getString("image");
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_notification = (ImageView) rootView.findViewById(R.id.img_notification);
        img_filter = (ImageView) rootView.findViewById(R.id.img_filter);
        img_search = (ImageView) rootView.findViewById(R.id.img_search);
        img_trending = (ImageView) rootView.findViewById(R.id.img_trending);

        ll = (RelativeLayout) rootView.findViewById(R.id.ll);
        ll.bringToFront();

        txt_location = (TextView) rootView.findViewById(R.id.txt_location);
        txt_cat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);
        txt_location.setTypeface(font);
        txt_cat_name.setTypeface(font);
        txt_cat_name.setText(name);

        txt_location.setOnClickListener(this);
        img_icon_menu.setOnClickListener(this);
        img_notification.setOnClickListener(this);
        img_search.setOnClickListener(this);
        img_filter.setOnClickListener(this);

        getTrendingData();
        /*Glide.with(getActivity()).load(image).asBitmap()
                .into(new BitmapImageViewTarget(img_trending) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        img_trending.setImageBitmap(resource);
                        img_trending.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                });*/
        return rootView;
    }

    private void getTrendingData() {
        //http://52.77.174.143/fashion_api.php?route=trending&catid=3storeid=1
        String urlJson = Constant.FASHION_API + "route=trending&catid=" + mainCAtId + "&storeid=1";
        Log.e("URL", urlJson);
        showpDialog();
        final ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                boolean responseSuccess = false;
                String strsuccess;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray jsonArray = new JSONArray(response.getString("categories"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LinkedHashMap<String, String> item = new LinkedHashMap<>();
                            item.put("category_id", jsonObject.getString("category_id"));
                            item.put("name", jsonObject.getString("name"));
                            item.put("url_key", jsonObject.getString("url_key"));
                            item.put("image", jsonObject.getString("image"));
                            list.add(item);
                        }
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    for (int i = 0; i < list.size(); i++) {
                        Glide.with(getActivity()).load(list.get(0).get("image")).asBitmap()
                                .into(new BitmapImageViewTarget(img_trending) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        img_trending.setImageBitmap(resource);
                                        img_trending.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    }
                                });
                        break;
                    }
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Volley", "Error: " + error.getMessage());
                String message = null;
                hidepDialog();
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
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        Bundle bundle;
        switch (viewId) {

            case R.id.img_icon_menu:
                //startActivity(new Intent(getActivity(), HomeActivity.class));
                break;

            case R.id.txt_location:
                startActivity(new Intent(getActivity(), SelectLocation.class).putExtra("location", "default"));
                break;

            case R.id.img_notification:
                Fragment f3 = new FollowersFragment();
                Bundle b3 = new Bundle();
                b3.putString("screen", "notifications");
                f3.setArguments(b3);
                FragmentTransaction t3 = getFragmentManager().beginTransaction();
                t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t3.add(R.id.frame_container, f3);
                t3.addToBackStack(null);
                t3.commit();
                break;

            case R.id.img_search:
                Fragment f1 = new SearchFragment();
                FragmentTransaction t1 = getFragmentManager().beginTransaction();
                t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t1.add(R.id.frame_container, f1);
                t1.addToBackStack(null);
                t1.commit();
                break;

            case R.id.img_filter:
                Fragment f2 = new FilterFragment();
                bundle = new Bundle();
                bundle.putString("mainCatId", mainCAtId);
                f2.setArguments(bundle);
                FragmentTransaction t2 = getFragmentManager().beginTransaction();
                t2.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t2.add(R.id.frame_container, f2);
                t2.addToBackStack(null);
                t2.commit();
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

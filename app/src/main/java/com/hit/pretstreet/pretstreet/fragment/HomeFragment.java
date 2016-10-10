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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.Items.CategoryItem;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by hit on 18/3/16.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private ImageView img_notification, img_search;
    public TextView txt_location;
    private TextView txt_user_name, txt_bags, txt_shoes, txt_eyewear, txt_watches, txt_accessories;
    private LinearLayout ll_main_cat, ll_bags, ll_shoes, ll_eyewear, ll_watches, ll_accesories;
    private RelativeLayout rl_location_search, rl_sub_cat1;
    private String parentID, SavedMAinCaTList, SavedSubCatList, baseImage;
    private JsonObjectRequest jsonObjReqMain, jsonObjReqSub;
    private ArrayList<CategoryItem> listsubcat;
    int deviceSize;
    private Typeface font;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");

        img_notification = (ImageView) rootView.findViewById(R.id.img_notification);
        img_search = (ImageView) rootView.findViewById(R.id.img_search);

        ll_main_cat = (LinearLayout) rootView.findViewById(R.id.ll_main_cat);
        ll_bags = (LinearLayout) rootView.findViewById(R.id.ll_bags);
        ll_shoes = (LinearLayout) rootView.findViewById(R.id.ll_shoes);
        ll_eyewear = (LinearLayout) rootView.findViewById(R.id.ll_eyewear);
        ll_watches = (LinearLayout) rootView.findViewById(R.id.ll_watches);
        ll_accesories = (LinearLayout) rootView.findViewById(R.id.ll_accesories);

        rl_sub_cat1 = (RelativeLayout) rootView.findViewById(R.id.rl_sub_cat1);
        rl_location_search = (RelativeLayout) rootView.findViewById(R.id.rl_location_search);

        LinearLayout ll_top = (LinearLayout) rootView.findViewById(R.id.ll_top);

        txt_user_name = (TextView) rootView.findViewById(R.id.txt_user_name);
        txt_location = (TextView) rootView.findViewById(R.id.txt_location);

        txt_bags = (TextView) rootView.findViewById(R.id.txt_bags);
        txt_shoes = (TextView) rootView.findViewById(R.id.txt_shoes);
        txt_eyewear = (TextView) rootView.findViewById(R.id.txt_eyewear);
        txt_watches = (TextView) rootView.findViewById(R.id.txt_watches);
        txt_accessories = (TextView) rootView.findViewById(R.id.txt_accessories);

        txt_user_name.setTypeface(font);
        txt_location.setTypeface(font);

        txt_bags.setTypeface(font);
        txt_shoes.setTypeface(font);
        txt_eyewear.setTypeface(font);
        txt_watches.setTypeface(font);
        txt_accessories.setTypeface(font);

        ll_bags.setOnClickListener(this);
        ll_shoes.setOnClickListener(this);
        ll_eyewear.setOnClickListener(this);
        ll_watches.setOnClickListener(this);
        ll_accesories.setOnClickListener(this);

        txt_bags.setOnClickListener(this);
        txt_shoes.setOnClickListener(this);
        txt_eyewear.setOnClickListener(this);
        txt_watches.setOnClickListener(this);
        txt_accessories.setOnClickListener(this);

        txt_location.setOnClickListener(this);
        img_notification.setOnClickListener(this);
        img_search.setOnClickListener(this);

        // rl_location_search.bringToFront();
        ll_top.bringToFront();
        if (PreferenceServices.getInstance().geUsertName().equalsIgnoreCase("")) {
            getUseerName();
        }
        deviceSize = PreferenceServices.getInstance().getDeviceSize();
        Log.e("deveice:", deviceSize + "");
        // set saved data of the main catrgoty list
        SavedMAinCaTList = PreferenceServices.getInstance().getHomeMainCatList();
        if (SavedMAinCaTList.length() > 1) {
            ll_main_cat.setVisibility(View.VISIBLE);
            final ArrayList<HashMap<String, String>> list = new ArrayList<>();
            JSONObject response = null;
            try {
                response = new JSONObject(SavedMAinCaTList);
                JSONArray jsonArray = new JSONArray(response.getString("categories"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("category_id", jsonObject.getString("category_id"));
                    hashMap.put("name", jsonObject.getString("name"));
                    hashMap.put("image", jsonObject.getString("image"));
                    hashMap.put("url_key", jsonObject.getString("url_key"));
                    list.add(hashMap);
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ll_main_cat.removeAllViews();
            for (int i = 0; i < list.size(); i++) {
                LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view;
                if (i % 2 == 0) {
                    view = infl.inflate(R.layout.row_sub_cat_list1, null);
                } else {
                    view = infl.inflate(R.layout.row_sub_cat_list2, null);
                }
                RelativeLayout rl_dd = (RelativeLayout) view.findViewById(R.id.rl_dd);
                TextView txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
                final ImageView mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                txt_cat_name.setMaxLines(1);

                if (i == 0) {
                    txt_cat_name.setVisibility(View.INVISIBLE);
                } else {
                    txt_cat_name.setVisibility(View.VISIBLE);
                }

                txt_cat_name.setTypeface(font);
                LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                if (deviceSize == DisplayMetrics.DENSITY_LOW) {//TODO: 120
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -25, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -30, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_MEDIUM) {//TODO: 160
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -30, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -37, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_TV) {//TODO: 213
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -40, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -55, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_HIGH) {//TODO: 240
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -45, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -55, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_XHIGH) {//TODO: 320
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -60, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -75, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_420) {//TODO: 420
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -86, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -110, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_XXHIGH) {//TODO: 480
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -85, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -110, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_560) {//TODO: 560
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -115, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -145, 0, 0);
                    }
                } else if (deviceSize == DisplayMetrics.DENSITY_XXXHIGH) {//TODO: 640
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else {
                            relativeParams.setMargins(0, -115, 0, 0);
                        }
                    } else {
                        relativeParams.setMargins(0, -150, 0, 0);
                    }
                } else {//TODO: Default Size
                    if (i % 2 == 0) {
                        if (i == 0) {
                        } else
                            relativeParams.setMargins(0, -90, 0, 0);
                    } else {
                        relativeParams.setMargins(0, -100, 0, 0);
                    }
                }
                rl_dd.setLayoutParams(relativeParams);
                rl_dd.requestLayout();
                txt_cat_name.setText(list.get(i).get("name"));

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                final int dwidth = displaymetrics.widthPixels;
                final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

                final int finalI = i;
                Glide.with(getActivity()).load(list.get(i).get("image")).asBitmap()
                        .into(new BitmapImageViewTarget(mImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int width = resource.getWidth();
                                int height = resource.getHeight();
                                float scaleWidth = ((float) dwidth) / width;
                                float scaleHeight = ((float) dheight) / height;
                                Matrix matrix = new Matrix();
                                matrix.postScale(scaleWidth, scaleHeight);
                                Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);
                                Bitmap mask;
                                if (finalI % 2 == 0) {
                                    mask = BitmapFactory.decodeResource(getResources(), R.drawable.brand1);
                                } else {
                                    mask = BitmapFactory.decodeResource(getResources(), R.drawable.brand2);
                                }
                                Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas mCanvas = new Canvas(result);
                                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                                //mCanvas.drawBitmap(resource, 0, 0, null);
                                mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                                mCanvas.drawBitmap(mask, 0, 0, paint);
                                paint.setXfermode(null);
                                mImageView.setImageBitmap(result);
                                mImageView.setScaleType(ImageView.ScaleType.CENTER);
                            }
                        });

                final int finalI1 = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(finalI1).get("category_id").equalsIgnoreCase("3")) {
                            Fragment f3 = new TrendingFragment();
                            Bundle b3 = new Bundle();
                            b3.putString("main_cat_id", list.get(finalI1).get("category_id"));
                            b3.putString("main_cat_name", list.get(finalI1).get("name"));
                            b3.putString("image", list.get(finalI1).get("image"));
                            f3.setArguments(b3);
                            FragmentTransaction t3 = getFragmentManager().beginTransaction();
                            t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                            t3.add(R.id.frame_container, f3);
                            t3.addToBackStack(null);
                            t3.commit();
                        } else if (list.get(finalI1).get("category_id").equalsIgnoreCase("55")) {
                            Fragment f3 = new TwoSubCategoryFragment();
                            Bundle b3 = new Bundle();
                            b3.putString("main_cat_id", list.get(finalI1).get("category_id"));
                            b3.putString("main_cat_name", list.get(finalI1).get("name"));
                            f3.setArguments(b3);
                            FragmentTransaction t3 = getFragmentManager().beginTransaction();
                            t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                            t3.add(R.id.frame_container, f3);
                            t3.addToBackStack(null);
                            t3.commit();
                        } else {
                            Fragment f3 = new ThreeSubCategoryFragment();
                            Bundle b3 = new Bundle();
                            b3.putString("main_cat_id", list.get(finalI1).get("category_id"));
                            b3.putString("main_cat_name", list.get(finalI1).get("name"));
                            f3.setArguments(b3);
                            FragmentTransaction t3 = getFragmentManager().beginTransaction();
                            t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                            t3.add(R.id.frame_container, f3);
                            t3.addToBackStack(null);
                            t3.commit();
                        }
                    }
                });
                ll_main_cat.addView(view);
            }
        } else {
            ll_main_cat.setVisibility(View.GONE);
        }

        // set saved data of the sub catrgoty list
        SavedSubCatList = PreferenceServices.getInstance().getHomeSubCatList();
        if (SavedSubCatList.length() > 1) {
            rl_sub_cat1.setVisibility(View.VISIBLE);
            final ArrayList<HashMap<String, String>> list = new ArrayList<>();
            JSONObject response = null;
            try {
                response = new JSONObject(SavedSubCatList);
                parentID = response.getString("parent_id");
                JSONArray jsonArray = new JSONArray(response.getString("types"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("category_id", jsonObject.getString("category_id"));
                    hashMap.put("name", jsonObject.getString("name"));
                    hashMap.put("image", jsonObject.getString("image"));
                    list.add(hashMap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).get("category_id").equalsIgnoreCase("112")) {
                    ll_bags.setTag(list.get(i).get("category_id"));
                    txt_bags.setText(list.get(i).get("name"));
                }
                if (list.get(i).get("category_id").equalsIgnoreCase("111")) {
                    ll_shoes.setTag(list.get(i).get("category_id"));
                    txt_shoes.setText(list.get(i).get("name"));
                }
                if (list.get(i).get("category_id").equalsIgnoreCase("113")) {
                    ll_eyewear.setTag(list.get(i).get("category_id"));
                    txt_eyewear.setText(list.get(i).get("name"));
                }
                if (list.get(i).get("category_id").equalsIgnoreCase("110")) {
                    ll_watches.setTag(list.get(i).get("category_id"));
                    txt_watches.setText(list.get(i).get("name"));
                }
                if (list.get(i).get("category_id").equalsIgnoreCase("114")) {
                    ll_accesories.setTag(list.get(i).get("category_id"));
                    txt_accessories.setText(list.get(i).get("name"));
                }
            }
        } else {
            rl_sub_cat1.setVisibility(View.GONE);
        }

        getMainCategoryList();
        getSubCategoryList();
        return rootView;
    }

    private void getUseerName() {
        String urlJsonObj = Constant.FASHION_API + "route=user_name" + "&user_id=" + PreferenceServices.getInstance().geUsertId();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Volley", response.toString());
                String strsuccess, name, text;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equalsIgnoreCase("true")) {
                        name = response.getString("name");
                        txt_user_name.setText("Hi, " + name);
                        PreferenceServices.instance().saveUserName(name);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onPause() {
        super.onPause();
        jsonObjReqMain.cancel();
        jsonObjReqSub.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        String name = (PreferenceServices.getInstance().geUsertName().length() > 12 ?
                PreferenceServices.getInstance().geUsertName().substring(0, 10) + "..." : PreferenceServices.getInstance().geUsertName());
        txt_user_name.setText("Hi, " + name);
    }

    private void getMainCategoryList() {
        String urlJson = Constant.FASHION_API + "route=homepage";
        if (SavedMAinCaTList.length() > 1) {
            ll_main_cat.setVisibility(View.VISIBLE);
        } else {
            ll_main_cat.setVisibility(View.GONE);
            showpDialog();
        }
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        jsonObjReqMain = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, category;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray jsonArray = new JSONArray(response.getString("categories"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("category_id", jsonObject.getString("category_id"));
                            hashMap.put("name", jsonObject.getString("name"));
                            hashMap.put("image", jsonObject.getString("image"));
                            hashMap.put("url_key", jsonObject.getString("url_key"));
                            list.add(hashMap);
                        }
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    if (SavedMAinCaTList.length() > 1) {
                    } else {
                        ll_main_cat.setVisibility(View.VISIBLE);
                    }
                    PreferenceServices.instance().saveHomeMainCatList(response.toString());
                    if (response.toString().equalsIgnoreCase(SavedMAinCaTList)) {
                    } else {
                        ll_main_cat.removeAllViews();
                        for (int i = 0; i < list.size(); i++) {
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view;
                            if (i % 2 == 0) {
                                view = inflater.inflate(R.layout.row_sub_cat_list1, null);
                            } else {
                                view = inflater.inflate(R.layout.row_sub_cat_list2, null);
                            }
                            RelativeLayout rl_dd = (RelativeLayout) view.findViewById(R.id.rl_dd);
                            TextView txt_cat_name = (TextView) view.findViewById(R.id.txt_cat_name);
                            final ImageView mImageView = (ImageView) view.findViewById(R.id.img_cat_image);
                            txt_cat_name.setMaxLines(1);
                            txt_cat_name.setTypeface(font);
                            if (i == 0) {
                                txt_cat_name.setVisibility(View.INVISIBLE);
                            } else {
                                txt_cat_name.setVisibility(View.VISIBLE);
                            }
                            LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));

                            if (deviceSize == DisplayMetrics.DENSITY_LOW) {//TODO: 120
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -25, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -30, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_MEDIUM) {//TODO: 160
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -30, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -37, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_TV) {//TODO: 213
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -40, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -55, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_HIGH) {//TODO: 240
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -45, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -55, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_XHIGH) {//TODO: 320
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -60, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -75, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_420) {//TODO: 420
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -86, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -110, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_XXHIGH) {//TODO: 480
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -85, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -110, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_560) {//TODO: 560
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -115, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -145, 0, 0);
                                }
                            } else if (deviceSize == DisplayMetrics.DENSITY_XXXHIGH) {//TODO: 640
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else {
                                        relativeParams.setMargins(0, -115, 0, 0);
                                    }
                                } else {
                                    relativeParams.setMargins(0, -150, 0, 0);
                                }
                            } else {
                                //default device
                                if (i % 2 == 0) {
                                    if (i == 0) {
                                    } else
                                        relativeParams.setMargins(0, -90, 0, 0);
                                } else {
                                    relativeParams.setMargins(0, -100, 0, 0);
                                }
                            }
                            rl_dd.setLayoutParams(relativeParams);
                            rl_dd.requestLayout();

                            txt_cat_name.setText(list.get(i).get("name"));

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                            final int dwidth = displaymetrics.widthPixels;
                            final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

                            final int finalI = i;
                            Glide.with(getActivity()).load(list.get(i).get("image")).asBitmap()
                                    .into(new BitmapImageViewTarget(mImageView) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            int width = resource.getWidth();
                                            int height = resource.getHeight();
                                            float scaleWidth = ((float) dwidth) / width;
                                            float scaleHeight = ((float) dheight) / height;
                                            Matrix matrix = new Matrix();
                                            matrix.postScale(scaleWidth, scaleHeight);
                                            Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);
                                            Bitmap mask;
                                            if (finalI % 2 == 0) {
                                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.brand1);
                                            } else {
                                                mask = BitmapFactory.decodeResource(getResources(), R.drawable.brand2);
                                            }
                                            Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                                            Canvas mCanvas = new Canvas(result);
                                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                                            //mCanvas.drawBitmap(resource, 0, 0, null);
                                            mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                                            mCanvas.drawBitmap(mask, 0, 0, paint);
                                            paint.setXfermode(null);
                                            mImageView.setImageBitmap(result);
                                            mImageView.setScaleType(ImageView.ScaleType.CENTER);
                                        }
                                    });

                            final int finalI1 = i;
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (list.get(finalI1).get("category_id").equalsIgnoreCase("3")) {
                                        Fragment f3 = new TrendingFragment();
                                        Bundle b3 = new Bundle();
                                        b3.putString("main_cat_id", list.get(finalI1).get("category_id"));
                                        b3.putString("main_cat_name", list.get(finalI1).get("name"));
                                        b3.putString("image", list.get(finalI1).get("image"));
                                        f3.setArguments(b3);
                                        FragmentTransaction t3 = getFragmentManager().beginTransaction();
                                        t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                                        t3.add(R.id.frame_container, f3);
                                        t3.addToBackStack(null);
                                        t3.commit();
                                    } else if (list.get(finalI1).get("category_id").equalsIgnoreCase("55")) {
                                        Fragment f3 = new TwoSubCategoryFragment();
                                        Bundle b3 = new Bundle();
                                        b3.putString("main_cat_id", list.get(finalI1).get("category_id"));
                                        b3.putString("main_cat_name", list.get(finalI1).get("name"));
                                        f3.setArguments(b3);
                                        FragmentTransaction t3 = getFragmentManager().beginTransaction();
                                        t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                                        t3.add(R.id.frame_container, f3);
                                        t3.addToBackStack(null);
                                        t3.commit();
                                    } else {
                                        Fragment f3 = new ThreeSubCategoryFragment();
                                        Bundle b3 = new Bundle();
                                        b3.putString("main_cat_id", list.get(finalI1).get("category_id"));
                                        b3.putString("main_cat_name", list.get(finalI1).get("name"));
                                        f3.setArguments(b3);
                                        FragmentTransaction t3 = getFragmentManager().beginTransaction();
                                        t3.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                                        t3.add(R.id.frame_container, f3);
                                        t3.addToBackStack(null);
                                        t3.commit();
                                    }
                                }
                            });
                            ll_main_cat.addView(view);
                        }
                    }
                } else {
                    if (SavedMAinCaTList.length() > 1) {
                    } else {
                        Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }
                if (SavedMAinCaTList.length() > 1) {
                } else {
                    hidepDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Volley", "Error: " + error.getMessage());
                if (SavedMAinCaTList.length() > 1) {
                } else {
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
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjReqMain.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReqMain, Constant.tag_json_obj);
    }

    private void getSubCategoryList() {
        String urlJson = Constant.FASHION_API + "route=types";
        Log.e("Home Others URL: ", urlJson);
        if (SavedSubCatList.length() > 1) {
            rl_sub_cat1.setVisibility(View.VISIBLE);
        } else {
            rl_sub_cat1.setVisibility(View.GONE);
            showpDialog();
        }
        listsubcat = new ArrayList<>();
        jsonObjReqSub = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        parentID = response.getString("parent_id");
                        JSONArray jsonArray = new JSONArray(response.getString("types"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CategoryItem item = new CategoryItem();
                            item.setId(jsonObject.getString("category_id"));
                            item.setCat_name(jsonObject.getString("name"));
                            item.setImage(jsonObject.getString("image"));
                            listsubcat.add(item);
                        }
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    if (SavedSubCatList.length() > 1) {
                    } else {
                        rl_sub_cat1.setVisibility(View.VISIBLE);
                    }
                    PreferenceServices.instance().saveHomeSubCatList(response.toString());
                    for (int i = 0; i < listsubcat.size(); i++) {
                        if (listsubcat.get(i).getId().equalsIgnoreCase("112")) {
                            ll_bags.setTag(listsubcat.get(i).getId());
                            txt_bags.setText(listsubcat.get(i).getCat_name());
                        }
                        if (listsubcat.get(i).getId().equalsIgnoreCase("111")) {
                            ll_shoes.setTag(listsubcat.get(i).getId());
                            txt_shoes.setText(listsubcat.get(i).getCat_name());
                        }
                        if (listsubcat.get(i).getId().equalsIgnoreCase("113")) {
                            ll_eyewear.setTag(listsubcat.get(i).getId());
                            txt_eyewear.setText(listsubcat.get(i).getCat_name());
                        }
                        if (listsubcat.get(i).getId().equalsIgnoreCase("110")) {
                            ll_watches.setTag(listsubcat.get(i).getId());
                            txt_watches.setText(listsubcat.get(i).getCat_name());
                        }
                        if (listsubcat.get(i).getId().equalsIgnoreCase("114")) {
                            ll_accesories.setTag(listsubcat.get(i).getId());
                            txt_accessories.setText(listsubcat.get(i).getCat_name());
                        }
                    }
                } else {
                    if (SavedSubCatList.length() > 1) {
                    } else {
                        Toast.makeText(getActivity(), "Server not responding Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }
                if (SavedSubCatList.length() > 1) {
                } else {
                    hidepDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Volley", "Error: " + error.getMessage());
                if (SavedSubCatList.length() > 1) {
                } else {
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
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonObjReqSub.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReqSub, Constant.tag_json_obj);
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

            case R.id.txt_bags:
            case R.id.ll_bags:
                Fragment f4 = new CategoryWiseStoreListFragment();
                Bundle b4 = new Bundle();
                b4.putString("main_cat_id", parentID);
                b4.putString("sub_cat_id", ll_bags.getTag().toString());
                b4.putSerializable("cat_list", listsubcat);
                b4.putString("isFrom", "SubCategory");
                b4.putString("main_cat_name", txt_bags.getText().toString());
                f4.setArguments(b4);
                FragmentTransaction t4 = getFragmentManager().beginTransaction();
                t4.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t4.add(R.id.frame_container, f4);
                t4.addToBackStack(null);
                t4.commit();
                break;

            case R.id.txt_shoes:
            case R.id.ll_shoes:
                Fragment f5 = new CategoryWiseStoreListFragment();
                Bundle b5 = new Bundle();
                b5.putString("main_cat_id", parentID);
                b5.putString("sub_cat_id", ll_shoes.getTag().toString());
                b5.putSerializable("cat_list", listsubcat);
                b5.putString("isFrom", "SubCategory");
                b5.putString("main_cat_name", txt_shoes.getText().toString());
                f5.setArguments(b5);
                FragmentTransaction t5 = getFragmentManager().beginTransaction();
                t5.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t5.add(R.id.frame_container, f5);
                t5.addToBackStack(null);
                t5.commit();
                break;

            case R.id.txt_eyewear:
            case R.id.ll_eyewear:
                Fragment f6 = new CategoryWiseStoreListFragment();
                Bundle b6 = new Bundle();
                b6.putString("main_cat_id", parentID);
                b6.putString("sub_cat_id", ll_eyewear.getTag().toString());
                b6.putSerializable("cat_list", listsubcat);
                b6.putString("isFrom", "SubCategory");
                b6.putString("main_cat_name", txt_eyewear.getText().toString());
                f6.setArguments(b6);
                FragmentTransaction t6 = getFragmentManager().beginTransaction();
                t6.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t6.add(R.id.frame_container, f6);
                t6.addToBackStack(null);
                t6.commit();
                break;

            case R.id.txt_watches:
            case R.id.ll_watches:
                Fragment f7 = new CategoryWiseStoreListFragment();
                Bundle b7 = new Bundle();
                b7.putString("main_cat_id", parentID);
                b7.putString("sub_cat_id", ll_watches.getTag().toString());
                b7.putSerializable("cat_list", listsubcat);
                b7.putString("isFrom", "SubCategory");
                b7.putString("main_cat_name", txt_watches.getText().toString());
                f7.setArguments(b7);
                FragmentTransaction t7 = getFragmentManager().beginTransaction();
                t7.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t7.add(R.id.frame_container, f7);
                t7.addToBackStack(null);
                t7.commit();
                break;

            case R.id.txt_accessories:
            case R.id.ll_accesories:
                Fragment f8 = new CategoryWiseStoreListFragment();
                Bundle b8 = new Bundle();
                b8.putString("main_cat_id", parentID);
                b8.putString("sub_cat_id", ll_accesories.getTag().toString());
                b8.putSerializable("cat_list", listsubcat);
                b8.putString("isFrom", "SubCategory");
                b8.putString("main_cat_name", txt_accessories.getText().toString());
                f8.setArguments(b8);
                FragmentTransaction t8 = getFragmentManager().beginTransaction();
                t8.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t8.add(R.id.frame_container, f8);
                t8.addToBackStack(null);
                t8.commit();
                break;

            case R.id.txt_location:
                startActivity(new Intent(getActivity(), SelectLocation.class).putExtra("location", "default"));
                break;

            case R.id.img_notification:
                Fragment f11 = new FollowersFragment();
                Bundle b11 = new Bundle();
                b11.putString("screen", "notifications");
                f11.setArguments(b11);
                FragmentTransaction t11 = getFragmentManager().beginTransaction();
                t11.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t11.add(R.id.frame_container, f11);
                t11.addToBackStack(null);
                t11.commit();
                break;

            case R.id.img_search:
                Fragment f9 = new SearchFragment();
                FragmentTransaction t9 = getFragmentManager().beginTransaction();
                t9.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t9.add(R.id.frame_container, f9);
                t9.addToBackStack(null);
                t9.commit();
                break;

            default:
                break;
        }
    }

}

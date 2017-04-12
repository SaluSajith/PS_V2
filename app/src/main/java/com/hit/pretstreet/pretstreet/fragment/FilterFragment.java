package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
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
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class FilterFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu, img_men, img_women, img_kids, img_apply, img_reset,
            img_location, img_like, img_sale, img_new;
    private TextView txt_cat_name, txt_sort;

    private LinearLayout ll_action, ll_popularity, ll_menwomen;
    private String gender = "";

    ArrayList<HashMap<String, String>> listGender;
    ArrayList<HashMap<String, String>> listPopularity;

    ArrayList<Boolean> booleanSelectedArryPopularity = new ArrayList<>();
    ArrayList<String> selectedidsPopularity = new ArrayList<>();
    String selected_ids_popularity = "";

    private Typeface font;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);
        img_men = (ImageView) rootView.findViewById(R.id.img_men);
        img_women = (ImageView) rootView.findViewById(R.id.img_women);
        img_kids = (ImageView) rootView.findViewById(R.id.img_kids);
        img_apply = (ImageView) rootView.findViewById(R.id.img_apply);
        img_reset = (ImageView) rootView.findViewById(R.id.img_reset);
        img_location = (ImageView) rootView.findViewById(R.id.img_location);
        img_like = (ImageView) rootView.findViewById(R.id.img_like);
        img_sale = (ImageView) rootView.findViewById(R.id.img_sale);
        img_new = (ImageView) rootView.findViewById(R.id.img_new);

        ll_action = (LinearLayout) rootView.findViewById(R.id.ll_action);
        ll_popularity = (LinearLayout) rootView.findViewById(R.id.ll_popularity);
        ll_menwomen = (LinearLayout) rootView.findViewById(R.id.ll_menwomen);

        txt_cat_name = (TextView) rootView.findViewById(R.id.txt_cat_name);
        txt_sort = (TextView) rootView.findViewById(R.id.txt_sort);

        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        txt_cat_name.setText("FILTER");
        txt_cat_name.setTypeface(font);
        txt_sort.setTypeface(font);

        img_icon_menu.setOnClickListener(this);
        img_reset.setOnClickListener(this);
        img_apply.setOnClickListener(this);
        getSortingList();
        return rootView;
    }

    private void getSortingList() {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=sort_by
        String urlJson = Constant.FASHION_API + "route=sort_by";
        showpDialog();
        listGender = new ArrayList<>();
        listPopularity = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, msg = null;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        JSONArray ArrayGender = new JSONArray(response.getString("gender"));
                        for (int i = 0; i < ArrayGender.length(); i++) {
                            JSONObject jsonObject = ArrayGender.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", jsonObject.getString("id"));
                            hashMap.put("name", jsonObject.getString("name"));
                            listGender.add(hashMap);
                        }
                        JSONArray ArrayPopularity = new JSONArray(response.getString("popularity"));
                        for (int i = 0; i < ArrayPopularity.length(); i++) {
                            JSONObject jsonObject = ArrayPopularity.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("key_id", jsonObject.getString("key_id"));
                            hashMap.put("name", jsonObject.getString("name"));
                            hashMap.put("key_name", jsonObject.getString("key_name"));
                            hashMap.put("image", jsonObject.getString("image"));
                            hashMap.put("image_selected", jsonObject.getString("image_selected"));
                            hashMap.put("select", "no");
                            listPopularity.add(hashMap);
                        }
                    } else {
                        responseSuccess = false;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    ll_action.setVisibility(View.VISIBLE);
                    ll_popularity.setVisibility(View.VISIBLE);
                    ll_menwomen.setVisibility(View.VISIBLE);
                    if (listGender.isEmpty()) {
                    } else {
                        for (int i = 0; i < listGender.size(); i++) {
                            if (listGender.get(i).get("id").equalsIgnoreCase("79")) {
                                //img_men.setImageResource(R.drawable.male_active_sort);
                                //gender = listGender.get(i).get("id");
                                gender = "";
                                final int finalI = i;
                                img_men.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_men.setImageResource(R.drawable.male_active_sort);
                                        img_women.setImageResource(R.drawable.female_sort);
                                        img_kids.setImageResource(R.drawable.kids_sort);
                                        gender = listGender.get(finalI).get("id");
                                    }
                                });
                            }
                            if (listGender.get(i).get("id").equalsIgnoreCase("78")) {
                                final int finalI1 = i;
                                img_women.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_men.setImageResource(R.drawable.male_sort);
                                        img_women.setImageResource(R.drawable.female_active_sort);
                                        img_kids.setImageResource(R.drawable.kids_sort);
                                        gender = listGender.get(finalI1).get("id");
                                    }
                                });
                            }
                            if (listGender.get(i).get("id").equalsIgnoreCase("114")) {
                                final int finalI1 = i;
                                img_kids.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_men.setImageResource(R.drawable.male_sort);
                                        img_women.setImageResource(R.drawable.female_sort);
                                        img_kids.setImageResource(R.drawable.kids_active_sort);
                                        gender = listGender.get(finalI1).get("id");
                                    }
                                });
                            }
                        }
                    }

                    if (listPopularity.isEmpty()) {
                        ll_popularity.setVisibility(View.GONE);
                    } else {
                        ll_popularity.setVisibility(View.VISIBLE);
                        for (int i = 0; i < listPopularity.size(); i++) {
                            img_location.setImageResource(R.drawable.distance_active_sort);
                            img_like.setImageResource(R.drawable.followers_sort);
                            img_sale.setImageResource(R.drawable.sale_sort);
                            img_new.setImageResource(R.drawable.new_arrival_sort);
                            selected_ids_popularity = listPopularity.get(0).get("key_name");
                            if (listPopularity.get(i).get("key_id").equalsIgnoreCase("96")) {
                                final int finalI = i;
                                img_location.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_location.setImageResource(R.drawable.distance_active_sort);
                                        img_like.setImageResource(R.drawable.followers_sort);
                                        img_sale.setImageResource(R.drawable.sale_sort);
                                        img_new.setImageResource(R.drawable.new_arrival_sort);
                                        selected_ids_popularity = listPopularity.get(finalI).get("key_name");
                                    }
                                });
                            }
                            if (listPopularity.get(i).get("key_id").equalsIgnoreCase("98")) {
                                final int finalI2 = i;
                                img_like.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_location.setImageResource(R.drawable.distance_sort);
                                        img_like.setImageResource(R.drawable.followers_active_sort);
                                        img_sale.setImageResource(R.drawable.sale_sort);
                                        img_new.setImageResource(R.drawable.new_arrival_sort);
                                        selected_ids_popularity = listPopularity.get(finalI2).get("key_name");
                                    }
                                });
                            }
                            if (listPopularity.get(i).get("key_id").equalsIgnoreCase("99")) {
                                final int finalI3 = i;
                                img_sale.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_location.setImageResource(R.drawable.distance_sort);
                                        img_like.setImageResource(R.drawable.followers_sort);
                                        img_sale.setImageResource(R.drawable.sale_active_sort);
                                        img_new.setImageResource(R.drawable.new_arrival_sort);
                                        selected_ids_popularity = listPopularity.get(finalI3).get("key_name");
                                    }
                                });
                            }
                            if (listPopularity.get(i).get("key_id").equalsIgnoreCase("100")) {
                                final int finalI4 = i;
                                img_new.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img_location.setImageResource(R.drawable.distance_sort);
                                        img_like.setImageResource(R.drawable.followers_sort);
                                        img_sale.setImageResource(R.drawable.sale_sort);
                                        img_new.setImageResource(R.drawable.new_arrival_active_sort);
                                        selected_ids_popularity = listPopularity.get(finalI4).get("key_name");
                                    }
                                });
                            }
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
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
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {

            case R.id.img_icon_menu:
                //startActivity(new Intent(getActivity(), HomeActivity.class));
                break;

            case R.id.img_apply:
                if (selected_ids_popularity.isEmpty()) {
                    Toast.makeText(getActivity(), "Select category", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("gender:", gender + ":popul:" + selected_ids_popularity);
                    Fragment f1 = new StoreListByFilterFragment();
                    Bundle b1 = new Bundle();
                    b1.putString("gender", gender);
                    b1.putString("popularity", selected_ids_popularity);
                    b1.putString("mainCatId", getArguments().getString("mainCatId"));
                    f1.setArguments(b1);
                    FragmentTransaction t1 = getFragmentManager().beginTransaction();
                    t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                    t1.add(R.id.frame_container, f1);
                    t1.addToBackStack(null);
                    t1.commit();
                }
                break;

            case R.id.img_reset:
                booleanSelectedArryPopularity = new ArrayList<Boolean>();
                selectedidsPopularity.clear();
                selected_ids_popularity = listPopularity.get(0).get("key_name");

                gender = "";
                img_men.setImageResource(R.drawable.male_sort);
                img_women.setImageResource(R.drawable.female_sort);
                img_kids.setImageResource(R.drawable.kids_sort);

                img_location.setImageResource(R.drawable.distance_active_sort);
                img_like.setImageResource(R.drawable.followers_sort);
                img_sale.setImageResource(R.drawable.sale_sort);
                img_new.setImageResource(R.drawable.new_arrival_sort);
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

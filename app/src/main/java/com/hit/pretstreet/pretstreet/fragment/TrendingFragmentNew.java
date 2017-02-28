package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.customview.DividerDecoration;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by HIT on 22-02-2017.
 */

public class TrendingFragmentNew extends Fragment {

    private String mainCAtId, name, image;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    ArrayList<LinkedHashMap<String, String>> list;
    TrendingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trending_screen_new, null);

        Bundle bundle = this.getArguments();
        mainCAtId = bundle.getString("main_cat_id");
        name = bundle.getString("main_cat_name");
        image = bundle.getString("image");

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(getActivity(), getResources().getColor(R.color.yellow), 5.0f));

        list = new ArrayList<>();

        adapter = new TrendingAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (list != null && !list.isEmpty())
//            ((TrendingAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(0))).txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());

        getTrendingData();
    }

    private void getTrendingData() {
        //http://52.77.174.143/fashion_api.php?route=trending&catid=3storeid=1
        String urlJson = Constant.FASHION_API + "route=trending&catid=" + mainCAtId + "&storeid=1";
        Log.e("URL", urlJson);
        showpDialog();
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
                        if (list == null)
                            list = new ArrayList<>();
                        else
                            list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LinkedHashMap<String, String> item = new LinkedHashMap<>();
                            item.put("category_id", jsonObject.getString("category_id"));
                            item.put("name", jsonObject.getString("name"));
                            item.put("url_key", jsonObject.getString("url_key"));
                            item.put("image", jsonObject.getString("image"));
                            item.put("desc", jsonObject.getString("desc"));
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
                    if (adapter == null) {
                        adapter = new TrendingAdapter(getActivity(), list);
                        recyclerView.setAdapter(adapter);
                    } else
                        adapter.notifyDataSetChanged();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
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

    private class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

        Context context;
        ArrayList<LinkedHashMap<String, String>> list;

        public TrendingAdapter(Context context, ArrayList<LinkedHashMap<String, String>> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (holder.viewType == R.layout.header_store_list) {
                holder.txt_cat_name.setText(name);
                holder.txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());

            } else {
                LinkedHashMap<String, String> trendItem = list.get(position - 1);


                Glide.with(getActivity()).load(trendItem.get("image")).centerCrop().into(holder.img_banner);
                holder.txtTitle.setText(trendItem.get("name"));
                holder.txtDescription.setText(trendItem.get("desc"));
            }

        }

        @Override
        public int getItemCount() {
            return list.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return R.layout.header_store_list;
            else
                return R.layout.row_trending_data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            View view;
            int viewType;
            private ImageView img_icon_menu, img_notification, img_filter, img_search, img_header, img_banner;
            private TextView txt_location, txt_cat_name;
            TextView txtTitle, txtDescription;


            public ViewHolder(View itemView, int viewType) {
                super(itemView);

                view = itemView;
                this.viewType = viewType;
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
                Typeface fontDesc = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");


                if (viewType == R.layout.header_store_list) {

                    img_icon_menu = (ImageView) itemView.findViewById(R.id.img_icon_menu);
                    img_notification = (ImageView) itemView.findViewById(R.id.img_notification);
                    img_filter = (ImageView) itemView.findViewById(R.id.img_filter);
                    img_search = (ImageView) itemView.findViewById(R.id.img_search);
                    img_header = (ImageView) itemView.findViewById(R.id.img);

                    txt_location = (TextView) itemView.findViewById(R.id.txt_location);
                    txt_cat_name = (TextView) itemView.findViewById(R.id.txt_cat_name);
                    txt_location.setTypeface(font);
                    txt_cat_name.setTypeface(font);
                    txt_cat_name.setText(name);

                    txt_location.setOnClickListener(this);
                    img_icon_menu.setOnClickListener(this);
                    img_notification.setOnClickListener(this);
                    img_search.setOnClickListener(this);
                    img_filter.setOnClickListener(this);

                    img_header.setImageResource(R.drawable.fixed_top_trending);
                } else {
                    img_banner = (ImageView) itemView.findViewById(R.id.iv_banner);
                    txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
                    txtTitle = (TextView) itemView.findViewById(R.id.txt_title);

                    txtDescription.setTypeface(fontDesc);
                    txtTitle.setTypeface(font);
                }
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
        }
    }
}

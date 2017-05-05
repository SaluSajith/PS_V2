package com.hit.pretstreet.pretstreet.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.hit.pretstreet.pretstreet.Items.DatabaseHelper;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;
import com.hit.pretstreet.pretstreet.ui.StoreLocationMapScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu;
    private TextView txt_search, txt_recentsearches;
    private RelativeLayout rl_recent_search, rel;
    private ListView list_search, list_recentsearch;
    private SearchListAdapter searchListAdapter;
    private StoreListAdapter storeListAdapter;
    ArrayAdapter<String> dropDownAdapter;

    String[] searchResult, searchID, searchAddress, result;

    private ArrayAdapter<String> searchAdapter;
    private SearchView searchview;
    SearchView.SearchAutoComplete img_searchAuto;
    ArrayList<HashMap<String, String>> list;
    int pageCount, totalPages;
    boolean requestCalled;
    String submitQuery, baseImage;
    private Typeface font, fontM;
    private ProgressDialog pDialog;
    JsonObjectRequest jsonObjReqSearch;
    private DatabaseHelper helper;
    private DisplayMetrics dm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        helper = new DatabaseHelper(getActivity());
        img_icon_menu = (ImageView) rootView.findViewById(R.id.img_icon_menu);

        txt_search = (TextView) rootView.findViewById(R.id.txt_search);
        txt_recentsearches = (TextView) rootView.findViewById(R.id.txt_recentsearches);

        rel = (RelativeLayout) rootView.findViewById(R.id.rel);
        rl_recent_search = (RelativeLayout) rootView.findViewById(R.id.rl_recent_search);
        rl_recent_search.bringToFront();

        list_search = (ListView) rootView.findViewById(R.id.list_search);
        list_recentsearch = (ListView) rootView.findViewById(R.id.list_recentsearch);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        fontM = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");

        searchview = (SearchView) rootView.findViewById(R.id.searchview);
        img_searchAuto = (SearchView.SearchAutoComplete) searchview.findViewById(R.id.search_src_text);
        img_searchAuto.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        img_searchAuto.setHint(" Search PretStreet App");
        img_searchAuto.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
        searchview.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        txt_search.setText("SEARCH FOR:");
        txt_search.setTypeface(font);
        txt_recentsearches.setTypeface(font);

        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

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
                            rel.setBackgroundDrawable(dr);
                        }
                    });
        }

        img_icon_menu.setOnClickListener(this);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitQuery = query;
                pageCount = 0;
                list = null;
                list_search.setAdapter(null);
                showSearchResult(query, true);
                //Constant.hide_keyboard(getActivity());
                searchview.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               /* if (newText.length() == 2) {
                    showDropDownSearchResult(newText);
                } else if (newText.length() == 4) {
                    showDropDownSearchResult(newText);
                } else */
                if (newText.length() > 2) {
                    showDropDownSearchResult(newText.trim());
                }
                return false;
            }
        });

        list_search.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount - 10) {
                    if (pageCount < totalPages) {
                        if (!requestCalled) {
                            requestCalled = true;
                            showSearchResult(submitQuery, false);
                        }
                    }
                }
            }
        });
        ArrayList<HashMap<String, String>> list = helper.fetchSearchList();
        if (list.isEmpty()) {
        } else {
            for (int i = 0; i < list.size(); i++) {
                int count = helper.fetchPlacesCount();
                Log.e("count:", count + "");
                if (count > 5) {
                    helper.deleteFirstRow();
                } else {
                    Collections.reverse(list);
                    list_search.setVisibility(View.INVISIBLE);
                    list_recentsearch.setVisibility(View.VISIBLE);
                    searchListAdapter = new SearchListAdapter(getActivity(), R.layout.row_search, list);
                    list_recentsearch.setAdapter(searchListAdapter);
                    break;
                }
            }
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void showDropDownSearchResult(final String newText) {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=autosearching&searchtext=gaurav
        String urlJson;
        try {
            urlJson = Constant.FASHION_API + "route=autosearching&searchtext=" + URLEncoder.encode(newText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            urlJson = Constant.FASHION_API;
        }
        Log.e("URL: ", urlJson);
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("search dropdown", response.toString());
                boolean responseSuccess = false;
                String strsuccess, category;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        list.clear();
                        JSONArray jsonArray = new JSONArray(response.getString("searchresult"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", jsonObject.getString("id"));
                            hashMap.put("name", jsonObject.getString("name"));
                            hashMap.put("address", jsonObject.getString("address"));
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
                    if (list.isEmpty()) {
                        result = new String[]{"no result found"};
                        if (searchAdapter == null) {
                            searchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, result);
                            img_searchAuto.setAdapter(searchAdapter);
                        } else searchAdapter.notifyDataSetChanged();
                    } else {
                        searchResult = new String[list.size()];
                        searchAddress = new String[list.size()];
                        result = new String[list.size()];
                        searchID = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            searchResult[i] = list.get(i).get("name");
                            searchAddress[i] = list.get(i).get("address");
                            searchID[i] = list.get(i).get("id");
                            result[i] = searchResult[i] + ", " + searchAddress[i];
                        }
                        if(searchAdapter == null) {
                            searchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, result);
                            img_searchAuto.setAdapter(searchAdapter);
                        }else
                            searchAdapter.notifyDataSetChanged();
                        img_searchAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                    if(result[position].trim().equalsIgnoreCase("no result found"))
                                        return;
                                helper.saveSearches(searchID[position], searchResult[position], searchAddress[position]);
                                searchLogTracking(searchResult[position]);
                                Fragment f1 = new StoreDetailFragment();
                                Bundle b1 = new Bundle();
                                b1.putString("cat_id", searchID[position]);
                                b1.putString("cat_name", searchResult[position]);
                                f1.setArguments(b1);
                                FragmentTransaction t1 = getFragmentManager().beginTransaction();
                                t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                                t1.add(R.id.frame_container, f1);
                                //t1.replace(R.id.frame_container, f1);
                                t1.addToBackStack(null);
                                t1.commit();
                            }
                        });
                    }
                }
//                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
//                hidepDialog();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    private void showSearchResult(String query, final boolean first) {
        //http://52.77.174.143/fashion_api.php?route=search&keywords=gaurav&lat=&lon=&start=
        String lat = "", lng = "";
        if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("0.0")
                || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("0.0")) {
            lat = "";
            lng = "";
        } else {
            lat = PreferenceServices.getInstance().getLatitute();
            lng = PreferenceServices.getInstance().getLongitute();
        }
        String urlJson;
        query = query.trim();
        try {
            urlJson = Constant.FASHION_API + "route=search&keywords="
                    + URLEncoder.encode(query, "UTF-8")
                    + "&lat=" + lat + "&long=" + lng
                    + "&user_id=" + PreferenceServices.getInstance().geUsertId()
                    + "&start=" + ++pageCount;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            urlJson = Constant.FASHION_API;
        }
        if (first) {
            list = new ArrayList<>();
            showpDialog();
        }
        Log.e("URL: ", urlJson);
        final String finalQuery = query;
        jsonObjReqSearch = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, msg = null;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
                        if (first) {
                            totalPages = response.getInt("page_count");
                            if (totalPages < 1) {
                                Toast.makeText(getActivity(), "Page not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        JSONArray jsonArray = new JSONArray(response.getString("products"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", jsonObject.getString("id"));
                            hashMap.put("name", jsonObject.getString("name"));
                            hashMap.put("address", jsonObject.getString("address"));
                            hashMap.put("thumb", jsonObject.getString("thumb"));
                            hashMap.put("phone1", jsonObject.getString("phone1"));
                            hashMap.put("phone2", jsonObject.getString("phone2"));
                            hashMap.put("phone3", jsonObject.getString("phone3"));
                            hashMap.put("latitude", jsonObject.getString("latitude"));
                            hashMap.put("longitude", jsonObject.getString("longitude"));
                            hashMap.put("rating_count", jsonObject.getString("rating_count"));
                            hashMap.put("follow_count", jsonObject.getString("follow_count"));
                            hashMap.put("wishlist", jsonObject.getString("wishlist"));
                            hashMap.put("sale", jsonObject.getString("sale"));
                            hashMap.put("arrival", jsonObject.getString("arrival"));
                            hashMap.put("area", jsonObject.getString("area"));
                            list.add(hashMap);
                        }
                    } else {
                        responseSuccess = false;
                        msg = response.getString("message");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    responseSuccess = false;
                }
                if (responseSuccess) {
                    list_search.setVisibility(View.VISIBLE);
                    list_recentsearch.setVisibility(View.INVISIBLE);
                    if (first) {
                        if (!searchview.isIconified())
                            searchview.setIconified(true);
                        searchview.clearFocus();
                        searchLogTracking(finalQuery);
                        Constant.hide_keyboard(getActivity());
                        storeListAdapter = new StoreListAdapter(getActivity(), R.layout.row_list_store1, list);
                        list_search.setAdapter(storeListAdapter);
                    } else
                        storeListAdapter.notifyDataSetChanged();
                    requestCalled = false;
                } else {
                    if (first)
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
                if (first) {
                    hidepDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                if (first) {
                    hidepDialog();
                    if (!searchview.isIconified())
                        searchview.setIconified(true);
                }
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
        jsonObjReqSearch.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReqSearch, Constant.tag_json_obj);
    }

    private void searchLogTracking(String query) {
        String urlJson;
        try {
            urlJson = Constant.FASHION_API + "route=serach_storelogs&keywords=" + URLEncoder.encode(query, "UTF-8") + "&user_id=" + PreferenceServices.getInstance().geUsertId();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            urlJson = Constant.FASHION_API;
        }
        Log.e("URL: ", urlJson);
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.e("Volley", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    public class SearchListAdapter extends BaseAdapter {

        private Context context;
        int layoutResourceId;
        private ArrayList<HashMap<String, String>> mItems;

        public SearchListAdapter(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> data) {
            this.context = context;
            this.mItems = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class RecordHolder {
            TextView txt_store_name, txt_address;
            LinearLayout ll_store;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RecordHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.row_search, parent, false);
                viewHolder = new RecordHolder();
                viewHolder.ll_store = (LinearLayout) convertView.findViewById(R.id.ll_store);
                viewHolder.txt_store_name = (TextView) convertView.findViewById(R.id.txt_store_name);
                viewHolder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
                viewHolder.txt_store_name.setTypeface(font);
                viewHolder.txt_address.setTypeface(font);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RecordHolder) convertView.getTag();
            }
            if (position % 2 == 0)
                viewHolder.ll_store.setBackgroundResource(R.drawable.email);
            else
                viewHolder.ll_store.setBackgroundResource(R.drawable.password);


            if (position == 0) {
                LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins(40, 100, 0, 0);
                viewHolder.txt_store_name.setLayoutParams(relativeParams);
                viewHolder.txt_store_name.requestLayout();
            }

            viewHolder.txt_store_name.setText(mItems.get(position).get("name"));
            viewHolder.txt_address.setText(mItems.get(position).get("address"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment f1 = new StoreDetailFragment();
                    Bundle b1 = new Bundle();
                    b1.putString("cat_id", mItems.get(position).get("id"));
                    b1.putString("cat_name", mItems.get(position).get("name"));
                    f1.setArguments(b1);
                    FragmentTransaction t1 = getFragmentManager().beginTransaction();
                    t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                    t1.add(R.id.frame_container, f1);
                    t1.addToBackStack(null);
                    t1.commit();
                }
            });

            return convertView;
        }

    }

    public class StoreListAdapter extends BaseAdapter {
        private Context context;
        int layoutResourceId;
        private ArrayList<HashMap<String, String>> mItems;
        String phone1, phone2, phone3, storename;
        int followCount;

        public StoreListAdapter(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> data) {
            this.context = context;
            this.mItems = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView img_store_photo, img_call, img_map, img_address, img_sale, img_new_arrival;
            TextView txt_storename, txt_address, txt_folleowercount, img_follow_unfollow, tv_margintop, line;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (position % 2 == 0) {
                convertView = inflater.inflate(R.layout.row_list_store1, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.row_list_store2, parent, false);
            }
            img_store_photo = (ImageView) convertView.findViewById(R.id.img_store_photo);
            img_follow_unfollow = (TextView) convertView.findViewById(R.id.img_follow_unfollow);
            img_call = (ImageView) convertView.findViewById(R.id.img_call);
            img_map = (ImageView) convertView.findViewById(R.id.img_map);
            img_address = (ImageView) convertView.findViewById(R.id.img_address);
            img_sale = (ImageView) convertView.findViewById(R.id.img_sale);
            img_new_arrival = (ImageView) convertView.findViewById(R.id.img_new_arrival);
            txt_storename = (TextView) convertView.findViewById(R.id.txt_storename);
            tv_margintop = (TextView) convertView.findViewById(R.id.tv_margintop);
            txt_address = (TextView) convertView.findViewById(R.id.txt_address);
            txt_folleowercount = (TextView) convertView.findViewById(R.id.txt_folleowercount);
            line = (TextView) convertView.findViewById(R.id.line);
            txt_storename.setTypeface(font);
            txt_address.setTypeface(fontM);
            txt_folleowercount.setTypeface(font);
            img_follow_unfollow.setTypeface(font);
            txt_storename.setText(mItems.get(position).get("name"));
            txt_address.setText(mItems.get(position).get("area"));

            String strFollowCount = list.get(position).get("follow_count");
            if (strFollowCount.length() >= 4) {
                String convertedCountK = strFollowCount.substring(0, strFollowCount.length() - 3);
                if (convertedCountK.length() >= 4) {
                    String convertedCount = convertedCountK.substring(0, convertedCountK.length() - 3);
                    txt_folleowercount.setText(Html.fromHtml(convertedCount + "<sup>M</sup>"));
                } else {
                    txt_folleowercount.setText(Html.fromHtml(convertedCountK + "<sup>K</sup>"));
                }
            } else {
                txt_folleowercount.setText(Html.fromHtml(strFollowCount));
            }
            if (position == 0) {
                LinearLayout.LayoutParams relativeParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins(0, 20, 10, 0);
                tv_margintop.setLayoutParams(relativeParams);
                tv_margintop.requestLayout();
            }
            Glide.with(SearchFragment.this).load(list.get(position).get("thumb")).asBitmap().fitCenter()
                    .into(new BitmapImageViewTarget(img_store_photo) {
                        @Override
                        protected void setResource(Bitmap resource) {

                            Bitmap croppedBmp = Bitmap.createBitmap(resource);
                            final Matrix matrix = img_store_photo.getImageMatrix();
                            matrix.postScale(2, 2);
                            img_store_photo.setImageMatrix(matrix);
                            img_store_photo.setImageBitmap(croppedBmp);
                        }
                    });

            if (mItems.get(position).get("wishlist").equalsIgnoreCase("notin")) {
                img_follow_unfollow.setText("Follow");
            } else {
                img_follow_unfollow.setText("Unfollow");
            }

            if (mItems.get(position).get("sale").equalsIgnoreCase("Yes")) {
                img_sale.setVisibility(View.VISIBLE);
            } else {
                img_sale.setVisibility(View.GONE);
            }

            if (mItems.get(position).get("arrival").equalsIgnoreCase("No")) {
                img_new_arrival.setVisibility(View.GONE);
            } else {
                img_new_arrival.setVisibility(View.VISIBLE);
            }

            img_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phone1 = mItems.get(position).get("phone1");
                    phone2 = mItems.get(position).get("phone2");
                    phone3 = mItems.get(position).get("phone3");
                    storename = mItems.get(position).get("name");
                    if (phone1.equalsIgnoreCase("") && phone2.equalsIgnoreCase("") && phone3.equalsIgnoreCase(""))
                        Toast.makeText(getActivity(), "Number not Found", Toast.LENGTH_SHORT).show();
                    else {
                        clickLogTracking("call", mItems.get(position).get("id"));
                        showPopupPhoneNumber();
                    }
                }
            });

            img_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItems.get(position).get("latitude").equalsIgnoreCase("") ||
                            mItems.get(position).get("longitude").equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Lat Long not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        clickLogTracking("map", mItems.get(position).get("id"));
                        Intent i = new Intent(getActivity(), StoreLocationMapScreen.class);
                        Bundle b = new Bundle();
                        storename = mItems.get(position).get("name");
                        b.putString("name", storename);
                        b.putString("address", mItems.get(position).get("address"));
                        b.putDouble("lat", Double.parseDouble(mItems.get(position).get("latitude")));
                        b.putDouble("long", Double.parseDouble(mItems.get(position).get("longitude")));
                        i.putExtras(b);
                        startActivity(i);
                    }
                }
            });

            img_follow_unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followCount = Integer.parseInt(mItems.get(position).get("follow_count"));
                    if (mItems.get(position).get("wishlist").equalsIgnoreCase("notin")) {
                        addToFollowers(mItems.get(position).get("id"), position);
                    } else {
                        removeFromFollowers(mItems.get(position).get("id"), position);
                    }
                }
            });

            img_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickLogTracking("address", mItems.get(position).get("id"));
                    storename = mItems.get(position).get("name");
                    showAddress(mItems.get(position).get("address"));
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickLogTracking("storeview", mItems.get(position).get("id"));
                    Fragment f1 = new StoreDetailFragment();
                    Bundle b1 = new Bundle();
                    b1.putString("cat_id", mItems.get(position).get("id"));
                    b1.putString("cat_name", mItems.get(position).get("name"));
                    f1.setArguments(b1);
                    FragmentTransaction t1 = getFragmentManager().beginTransaction();
                    t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                    t1.add(R.id.frame_container, f1);
                    t1.addToBackStack(null);
                    t1.commit();
                }
            });
            return convertView;
        }

        private void clickLogTracking(String event, String storeId) {
            String urlJson = Constant.FASHION_API + "route=trackinglogs&action=" + event + "&user_id="
                    + PreferenceServices.getInstance().geUsertId() + "&pid=" + storeId;
            Log.e("URL: ", urlJson);
            final ArrayList<HashMap<String, String>> list = new ArrayList<>();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    Log.e("Volley", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Volley", "Error: " + error.getMessage());
                }
            });
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
        }

        private void removeFromFollowers(String id, final int pos) {
            //http://doctronics.co.in/fashionapp/fashion_api.php?route=remove_follow&store_id=31&user_id=3
            String urlJsonObj = Constant.FASHION_API + "route=remove_follow&store_id=" + id + "&user_id=" + PreferenceServices.getInstance().geUsertId();
            showpDialog();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String strsuccess, message;
                    try {
                        strsuccess = response.getString("success");
                        if (strsuccess.equalsIgnoreCase("true")) {
                            message = response.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            mItems.get(pos).put("wishlist", "notin");
                            mItems.get(pos).put("follow_count", String.valueOf(--followCount));
                            notifyDataSetChanged();
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
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    hidepDialog();
                }
            });
            PretStreet.getInstance().addToRequestQueue(jsonObjReq);
        }

        private void addToFollowers(String id, final int pos) {
            //http://doctronics.co.in/fashionapp/fashion_api.php?route=save_follow&store_id=31&user_id=3
            String urlJsonObj = Constant.FASHION_API + "route=save_follow&store_id=" + id + "&user_id=" + PreferenceServices.getInstance().geUsertId();
            showpDialog();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String strsuccess, message;
                    try {
                        strsuccess = response.getString("success");
                        if (strsuccess.equalsIgnoreCase("true")) {
                            message = response.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            mItems.get(pos).put("wishlist", "in");
                            mItems.get(pos).put("follow_count", String.valueOf(++followCount));
                            notifyDataSetChanged();
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
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    hidepDialog();
                }
            });
            PretStreet.getInstance().addToRequestQueue(jsonObjReq);
        }

        public void showPopupPhoneNumber() {
            final Dialog popupDialog = new Dialog(context);
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = li.inflate(R.layout.popup_phone_number, null);
            TextView txt_cat = (TextView) view.findViewById(R.id.txt_cat);
            ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
            img_close.setVisibility(View.VISIBLE);
            RelativeLayout rl_phone1 = (RelativeLayout) view.findViewById(R.id.rl_phone1);
            RelativeLayout rl_phone2 = (RelativeLayout) view.findViewById(R.id.rl_phone2);
            RelativeLayout rl_phone3 = (RelativeLayout) view.findViewById(R.id.rl_phone3);
            TextView txt_phone1 = (TextView) view.findViewById(R.id.txt_phone1);
            TextView txt_phone2 = (TextView) view.findViewById(R.id.txt_phone2);
            TextView txt_phone3 = (TextView) view.findViewById(R.id.txt_phone3);
            txt_cat.setTypeface(fontM);
            txt_phone1.setTypeface(fontM);
            txt_phone2.setTypeface(fontM);
            txt_phone3.setTypeface(fontM);
            txt_cat.setText(storename);
            txt_phone1.setText(phone1);
            txt_phone2.setText(phone2);
            txt_phone3.setText(phone3);
            if (phone1.equalsIgnoreCase(""))
                rl_phone1.setVisibility(View.GONE);
            if (phone2.equalsIgnoreCase(""))
                rl_phone2.setVisibility(View.GONE);
            if (phone3.equalsIgnoreCase(""))
                rl_phone3.setVisibility(View.GONE);
            popupDialog.setCanceledOnTouchOutside(true);
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

            rl_phone1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPhoneListener phoneListener = new MyPhoneListener();
                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                    try {
                        String uri = "tel:" + phone1;
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                        startActivity(dialIntent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Call Failed", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            rl_phone2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPhoneListener phoneListener = new MyPhoneListener();
                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                    try {
                        String uri = "tel:" + phone2;
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                        startActivity(dialIntent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Call Failed", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            rl_phone3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPhoneListener phoneListener = new MyPhoneListener();
                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                    try {
                        String uri = "tel:" + phone3;
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                        startActivity(dialIntent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Call Failed", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        }

        public void showAddress(String address) {
            final Dialog popupDialog = new Dialog(context);
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = li.inflate(R.layout.popup_phone_number, null);
            TextView txt_cat = (TextView) view.findViewById(R.id.txt_cat);
            TextView txt_close = (TextView) view.findViewById(R.id.txt_close);
            TextView txt_address = (TextView) view.findViewById(R.id.txt_address);
            RelativeLayout rl_phone1 = (RelativeLayout) view.findViewById(R.id.rl_phone1);
            RelativeLayout rl_phone2 = (RelativeLayout) view.findViewById(R.id.rl_phone2);
            RelativeLayout rl_phone3 = (RelativeLayout) view.findViewById(R.id.rl_phone3);
            rl_phone1.setVisibility(View.GONE);
            rl_phone2.setVisibility(View.GONE);
            rl_phone3.setVisibility(View.GONE);
            txt_close.setVisibility(View.VISIBLE);
            txt_address.setVisibility(View.VISIBLE);
            txt_cat.setTypeface(fontM);
            txt_close.setTypeface(fontM);
            txt_address.setTypeface(fontM);
            txt_cat.setText(storename);
            txt_address.setText(address);
            popupDialog.setCanceledOnTouchOutside(true);
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

            txt_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDialog.dismiss();
                }
            });
        }
    }

    private class MyPhoneListener extends PhoneStateListener {

        private boolean onCall = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(getActivity(), incomingNumber + " " + "Call you", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold
                    Toast.makeText(getActivity(), "onCall", Toast.LENGTH_LONG).show();
                    //because user answers the incoming call
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call
                    // detect flag from CALL_STATE_OFFHOOK
                    if (onCall == true) {
                        Toast.makeText(getActivity(), "Restart app after call", Toast.LENGTH_LONG).show();
                        // restart our application
                        Intent restart = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(restart);
                        onCall = false;
                    }
                    break;
                default:
                    break;
            }
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {

            case R.id.img_icon_menu:

                break;

            default:
                break;
        }
    }
}

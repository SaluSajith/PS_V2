package com.hit.pretstreet.pretstreet.fragment;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.hit.pretstreet.pretstreet.Constant;
import com.hit.pretstreet.pretstreet.Items.DatabaseHelper;
import com.hit.pretstreet.pretstreet.PreferenceServices;
import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;

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
    private ListView list_search;
    private SearchListAdapter searchListAdapter;

    String[] searchResult, searchID, searchAddress, result;

    private ArrayAdapter<String> searchAdapter;
    private SearchView searchview;
    SearchView.SearchAutoComplete img_searchAuto;
    ArrayList<HashMap<String, String>> list;
    int pageCount, totalPages;
    boolean requestCalled;
    String submitQuery, baseImage;
    private Typeface font;
    private ProgressDialog pDialog;
    JsonObjectRequest jsonObjReqSearch;
    private DatabaseHelper helper;

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
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");

        searchview = (SearchView) rootView.findViewById(R.id.searchview);
        img_searchAuto = (SearchView.SearchAutoComplete) searchview.findViewById(R.id.search_src_text);
        img_searchAuto.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        img_searchAuto.setHint(" Search PretStreet App");
        img_searchAuto.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
        searchview.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        txt_search.setTypeface(font);
        txt_recentsearches.setTypeface(font);

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
                if (!searchview.isIconified())
                    searchview.setIconified(true);

                submitQuery = query;
                pageCount = 0;
                showSearchResult(query, true);
                Constant.hide_keyboard(getActivity());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showDropDownSearchResult(newText);
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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    list_search.setVisibility(View.VISIBLE);
                    searchListAdapter = new SearchListAdapter(getActivity(), R.layout.row_search, list);
                    list_search.setAdapter(searchListAdapter);
                    break;
                }
            }
        }
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
                Log.e("Volley", response.toString());
                boolean responseSuccess = false;
                String strsuccess, category;
                try {
                    strsuccess = response.getString("success");
                    if (strsuccess.equals("true")) {
                        responseSuccess = true;
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
                        String[] stste = {"no result found"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, stste);
                        img_searchAuto.setAdapter(adapter);
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
                        searchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, result);
                        img_searchAuto.setAdapter(searchAdapter);
                        img_searchAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    private void showSearchResult(final String query, final boolean first) {
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
        try {
            urlJson = Constant.FASHION_API + "route=search&keywords=" + URLEncoder.encode(query, "UTF-8") + "&lat=" + lat + "&long=" + lng + "&start=" + ++pageCount;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            urlJson = Constant.FASHION_API;
        }
        if (first) {
            list = new ArrayList<>();
            showpDialog();
        }
        Log.e("URL: ", urlJson);
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
                    if (first) {
                        searchLogTracking(query);
                        Constant.hide_keyboard(getActivity());
                        searchListAdapter = new SearchListAdapter(getActivity(), R.layout.row_search, list);
                        list_search.setAdapter(searchListAdapter);
                    } else
                        searchListAdapter.notifyDataSetChanged();
                    requestCalled = false;
                } else {
                    if (first)
                        //getFragmentManager().popBackStack();
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
        jsonObjReqSearch.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

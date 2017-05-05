package com.hit.pretstreet.pretstreet.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
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
import com.hit.pretstreet.pretstreet.ui.OnLoadMoreListener;
import com.hit.pretstreet.pretstreet.ui.SelectLocation;
import com.hit.pretstreet.pretstreet.ui.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.ui.StoreLocationMapScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jesal on 05-Sep-16.
 */
public class CategoryWiseStoreListFragment extends Fragment implements View.OnClickListener {
    private ImageView img_icon_menu, img_notification, img_search, img_filter, img;
    private TextView txt_cat_name, txt_location;
    private LinearLayout ll_category, ll_header;
    RelativeLayout rl_background;
    private HorizontalScrollView hsv_category;
    private RecyclerView list_store;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;
    NestedScrollView nsv_header;

    private String mainCatId, subCatId, subCatName = "", isFrom;
    private String catValue[], catId[];
    private Typeface font, fontM;
    private ProgressDialog pDialog;
    ArrayList<CategoryItem> listCategory = new ArrayList<>();
    TextView[] txtname, txtnameAll;
    DisplayMetrics dm;

    String LLSelectedID = "";

    private String lat = "", lng = "";
    boolean maleClick, femaleClick;
    int pageCount, totalPages;
    public static int selectedPosition;
    boolean requestCalled;
    ArrayList<HashMap<String, String>> list;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_wise_store_list_screen, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        Bundle bundle = this.getArguments();
        mainCatId = bundle.getString("main_cat_id");
        if (LLSelectedID.equalsIgnoreCase("")) {
            subCatId = bundle.getString("sub_cat_id");
        } else {
            subCatId = LLSelectedID;
        }
        isFrom = bundle.getString("isFrom");
        if (subCatName.equalsIgnoreCase("")) {
            subCatName = bundle.getString("main_cat_name");
        }

        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        listCategory = (ArrayList<CategoryItem>) bundle.getSerializable("cat_list");
        list_store = (RecyclerView) rootView.findViewById(R.id.list_store);
        ll_header = (LinearLayout) rootView.findViewById(R.id.ll);
        nsv_header = (NestedScrollView) rootView.findViewById(R.id.nsv_header);
        font = Typeface.createFromAsset(getActivity().getAssets(), "RedVelvet-Regular.otf");
        fontM = Typeface.createFromAsset(getActivity().getAssets(), "Merriweather Light.ttf");

        if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("0.0")
                || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("0.0")) {
            lat = "";
            lng = "";
        } else {
            lat = PreferenceServices.getInstance().getLatitute();
            lng = PreferenceServices.getInstance().getLongitute();
        }

        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(getActivity(), R.layout.row_list_store1, list, list_store);
        final LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        list_store.setLayoutManager(mManager);
        list_store.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        list_store.setNestedScrollingEnabled(false);

        nsv_header.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    /*if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {

                    }*/
                    if (scrollY > oldScrollY) {
                        visibleItemCount = mManager.getChildCount();
                        totalItemCount = mManager.getItemCount();
                        pastVisiblesItems = mManager.findFirstVisibleItemPosition();

                        int visibleItemCount = mManager.getChildCount();
                        final int lastItem = mManager.findFirstVisibleItemPosition() + visibleItemCount;
                        System.out.println("log lastItem "+lastItem+" "+(mManager.getItemCount() ));
                        if (lastItem == mManager.getItemCount() ) {
                            System.out.println("log pageCount "+pageCount+" totalPages "+totalPages);
                            if (pageCount < totalPages) {
                                if (!requestCalled) {
                                    requestCalled = true;
                                    if (maleClick) {
                                        //SortStoreListByMenWomen("male", false);
                                    } else if (femaleClick) {
                                        //SortStoreListByMenWomen("female", false);
                                    } else {
                                            getStoreList(LLSelectedID, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        /**inflate header view of the list starts.**/
        LayoutInflater inflaterHeader = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflaterHeader.inflate(R.layout.header_withlabel, null);
        img_icon_menu = (ImageView) v.findViewById(R.id.img_icon_menu);
        img_notification = (ImageView) v.findViewById(R.id.img_notification);
        img_search = (ImageView) v.findViewById(R.id.img_search);
        img_filter = (ImageView) v.findViewById(R.id.img_filter);
        img = (ImageView) v.findViewById(R.id.img);
        txt_cat_name = (TextView) v.findViewById(R.id.txt_cat_name);
        txt_location = (TextView) v.findViewById(R.id.txt_location);
        hsv_category = (HorizontalScrollView) v.findViewById(R.id.hsv_category);
        ll_category = (LinearLayout) v.findViewById(R.id.ll_category);
        rl_background = (RelativeLayout) v.findViewById(R.id.rl_background);
        rl_background.getBackground().setFilterBitmap(true);

        txt_cat_name.setTypeface(font);
        hsv_category.bringToFront();
        txt_location.setTypeface(font);
        txt_cat_name.setText(subCatName);

        txt_location.setOnClickListener(this);
        img_icon_menu.setOnClickListener(this);
        img_notification.setOnClickListener(this);
        img_search.setOnClickListener(this);
        img_filter.setOnClickListener(this);

        ll_header.addView(v);
        ll_header.bringToFront();
        //txt_cat_name.getBackground().setFilterBitmap(true);
        //list_store.addHeaderView(v);
        /**inflate header view of the list ends.**/

        if (listCategory != null) {
            catValue = new String[listCategory.size()];
            catId = new String[listCategory.size()];
            txtname = new TextView[listCategory.size()];
            txtnameAll = new TextView[listCategory.size()];
            for (int i = 0; i < listCategory.size(); i++) {
                catValue[i] = listCategory.get(i).getCat_name();
                catId[i] = listCategory.get(i).getId();
                LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = infl.inflate(R.layout.row_catwise_cat, null);
                txtname[i] = (TextView) view.findViewById(R.id.name);
                txtnameAll[i] = (TextView) view.findViewById(R.id.nameAll);
                txtname[i].setTypeface(font);
                txtnameAll[i].setTypeface(font);
                if (i == 0) {
                    txtnameAll[i].setText("All");
                    list_store.setAdapter(null);
                } else {
                    txtnameAll[i].setVisibility(View.GONE);
                }
                if (mainCatId.equalsIgnoreCase(subCatId)) {
                    txtnameAll[i].setTextColor(getResources().getColor(R.color.black));
                    LLSelectedID = subCatId;
                    pageCount = 0;
                    txtnameAll[i].hasFocusable();
                    hsv_category.fullScroll(View.FOCUS_DOWN);
                    getStoreList(subCatId, true);
                }
                if (subCatId.equalsIgnoreCase(catId[i])) {
                    txtname[i].setTextColor(getResources().getColor(R.color.black));
                    LLSelectedID = catId[i];
                    pageCount = 0;
                    txtname[i].hasFocusable();
                    hsv_category.fullScroll(View.FOCUS_DOWN);
                    getStoreList(catId[i], true);
                }
                txtname[i].setText(catValue[i]);
                ll_category.addView(view);
                final int finalI = i;
                txtname[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list_store.setAdapter(null);
                        LLSelectedID = catId[finalI];
                        pageCount = 0;
                        if (isFrom.equalsIgnoreCase("SubCategory")) {
                            txt_cat_name.setText(catValue[finalI]);
                            subCatName = catValue[finalI];
                        }
                        getStoreList(catId[finalI], true);
                        for (int k = 0; k < listCategory.size(); k++) {
                            if (finalI == k) {
                                txtname[finalI].setTextColor(getResources().getColor(R.color.black));
                            } else {
                                txtname[k].setTextColor(getResources().getColor(R.color.dark_gray));
                                txtnameAll[0].setTextColor(getResources().getColor(R.color.dark_gray));
                            }
                        }
                    }
                });
                txtnameAll[finalI].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list_store.setAdapter(null);
                        for (int i = 0; i < listCategory.size(); i++) {
                            txtname[i].setTextColor(getResources().getColor(R.color.dark_gray));
                        }
                        txtnameAll[finalI].setTextColor(getResources().getColor(R.color.black));
                        LLSelectedID = mainCatId;
                        pageCount = 0;
                        if (isFrom.equalsIgnoreCase("SubCategory")) {
                            txt_cat_name.setText("All");
                            subCatName = "All";
                        }
                        getStoreList(mainCatId, true);
                    }
                });
            }
        } else {
            list_store.setAdapter(null);
            LLSelectedID = subCatId;
            LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = infl.inflate(R.layout.row_catwise_cat, null);
            txtname = new TextView[1];
            txtname[0] = (TextView) view.findViewById(R.id.name);
            txtname[0].setTextColor(getResources().getColor(R.color.black));
            txtname[0].setTypeface(font);
            txtname[0].setText("ALL");
            ll_category.addView(view);
            ll_category.setVisibility(View.VISIBLE);
            hsv_category.setVisibility(View.VISIBLE);
            getStoreList(LLSelectedID, true);
        }
        return rootView;
    }

   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("selectedif", LLSelectedID);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        String greeting = (savedInstanceState != null) ? savedInstanceState.getString("selectedif") : "null";

    }*/

    @Override
    public void onResume() {
        super.onResume();
        txt_location.setText(PreferenceServices.getInstance().getCurrentLocation());
    }

    private void getStoreList(String sub_catId, final boolean first) {
        //http://doctronics.co.in/fashionapp/fashion_api.php?route=cat_store_sort&main_cat_id=4&cat_id=104&user_id=68&start=3
        String urlJson = Constant.FASHION_API + "route=cat_store_sort&main_cat_id=" + mainCatId + "&cat_id=" + sub_catId
                + "&user_id=" + PreferenceServices.getInstance().geUsertId() + "&start=" + ++pageCount + "&lat=" + lat + "&long=" + lng;
        //String urlJson = Constant.FASHION_API + "route=get_stores_sort&gender=79&category_id=4&sortby=popularity&start=1&lat=19.1998211&long=72.842594&user_id=61";
        //http://52.77.174.143/fashion_api.php?route=get_stores_sort&gender=79&category_id=4&sortby=popularity&start=1&lat=19.1998211&long=72.842594&user_id=61
        if (first) {
            list = new ArrayList<>();
            showpDialog();
        }
        Log.e("url", urlJson);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                boolean responseSuccess = false;
                String strsuccess, msg = null;
                Log.e("response", response.toString());
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
                        JSONArray jsonArray = new JSONArray(response.getString("stores"));
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
                    if (first) {
                        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(getActivity(), R.layout.row_list_store1, list, list_store);
                        list_store.setAdapter(storeList_recyclerAdapter);

                    } else
                        storeList_recyclerAdapter.notifyDataSetChanged();
                            requestCalled = false;
                } else {
                    if (first) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
                if (first) {
                    hidepDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PretStreet.getInstance().addToRequestQueue(jsonObjReq, Constant.tag_json_obj);
    }

    private class MyPhoneListener extends PhoneStateListener {

        private boolean onCall = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(getActivity(), incomingNumber + " " + "Call you", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(getActivity(), "onCall", Toast.LENGTH_LONG).show();
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (onCall == true) {
                        Toast.makeText(getActivity(), "Restart app after call", Toast.LENGTH_LONG).show();
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
                bundle.putString("mainCatId", mainCatId);
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

    public class StoreList_RecyclerAdapter extends RecyclerView.Adapter<StoreList_RecyclerAdapter.ShopsHolder >
    {
        private Context context;
        int layoutResourceId;
        private ArrayList<HashMap<String, String>> mItems;
        String phone1, phone2, phone3, storename;
        int followCount;

        public StoreList_RecyclerAdapter(Context context, int layoutResourceId,
                                         ArrayList<HashMap<String, String>> data, RecyclerView recyclerView) {
            this.context = context;
            this.mItems = data;
            this.layoutResourceId = layoutResourceId;

        }

        @Override
        public ShopsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mRootView;
            int listViewItemType = getItemViewType(viewType);
            if (listViewItemType % 2 == 0) {
                mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store1, parent, false);
            } else {
                mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store2, parent, false);
            }
            return new ShopsHolder(mRootView);
        }

        @Override
        public void onBindViewHolder(final ShopsHolder holder, final int position) {

            holder.txt_storename.setTypeface(font);
            holder.txt_address.setTypeface(fontM);
            holder.txt_folleowercount.setTypeface(font);
            holder.img_follow_unfollow.setTypeface(font);
            holder.txt_storename.setText(mItems.get(position).get("name"));
            holder.txt_address.setText(mItems.get(position).get("area"));

            String strFollowCount = list.get(position).get("follow_count");
            if (strFollowCount.length() >= 4) {
                String convertedCountK = strFollowCount.substring(0, strFollowCount.length() - 3);
                if (convertedCountK.length() >= 4) {
                    String convertedCount = convertedCountK.substring(0, convertedCountK.length() - 3);
                    holder.txt_folleowercount.setText(Html.fromHtml(convertedCount + "<sup>M</sup>"));
                } else {
                    holder.txt_folleowercount.setText(Html.fromHtml(convertedCountK + "<sup>K</sup>"));
                }
            } else {
                holder.txt_folleowercount.setText(Html.fromHtml(strFollowCount));
            }
            if (position == 0) {
                LinearLayout.LayoutParams relativeParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins(0, 15, 10, 0);
                holder.tv_margintop.setLayoutParams(relativeParams);
                holder.tv_margintop.requestLayout();
            }
            Glide.with(CategoryWiseStoreListFragment.this)
                    .load(list.get(position).get("thumb"))
                    .asBitmap().fitCenter()
                    .into(new BitmapImageViewTarget(holder.img_store_photo) {
                        @Override
                        protected void setResource(Bitmap resource) {

                            Bitmap croppedBmp = Bitmap.createBitmap(resource);
                            final Matrix matrix = holder.img_store_photo.getImageMatrix();
                            matrix.postScale(2, 2);
                            holder.img_store_photo.setImageMatrix(matrix);
                            holder.img_store_photo.setImageBitmap(croppedBmp);

                        }
                    });

            if (mItems.get(position).get("wishlist").equalsIgnoreCase("notin")) {
                holder.img_follow_unfollow.setText("Follow");
            } else {
                holder.img_follow_unfollow.setText("Unfollow");
            }

            if (mItems.get(position).get("sale").equalsIgnoreCase("Yes")) {
                holder.img_sale.setVisibility(View.VISIBLE);
            } else {
                holder.img_sale.setVisibility(View.GONE);
            }

            if (mItems.get(position).get("arrival").equalsIgnoreCase("No")) {
                holder.img_new_arrival.setVisibility(View.GONE);
            } else {
                holder.img_new_arrival.setVisibility(View.VISIBLE);
            }

            holder.img_call.setOnClickListener(new View.OnClickListener() {
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

            holder.img_map.setOnClickListener(new View.OnClickListener() {
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

            holder.img_follow_unfollow.setOnClickListener(new View.OnClickListener() {
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

            holder.img_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickLogTracking("address", mItems.get(position).get("id"));
                    storename = mItems.get(position).get("name");
                    showAddress(mItems.get(position).get("address"));
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.mItems != null ? mItems.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ShopsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public ImageView img_store_photo, img_call, img_map, img_address, img_sale, img_new_arrival;
            TextView txt_storename, txt_address, txt_folleowercount, img_follow_unfollow, tv_margintop;

            public ShopsHolder(View itemView) {
                super(itemView);

                img_store_photo = (ImageView) itemView.findViewById(R.id.img_store_photo);
                img_follow_unfollow = (TextView) itemView.findViewById(R.id.img_follow_unfollow);
                img_call = (ImageView) itemView.findViewById(R.id.img_call);
                img_map = (ImageView) itemView.findViewById(R.id.img_map);
                img_address = (ImageView) itemView.findViewById(R.id.img_address);
                img_sale = (ImageView) itemView.findViewById(R.id.img_sale);
                img_new_arrival = (ImageView) itemView.findViewById(R.id.img_new_arrival);
                txt_storename = (TextView) itemView.findViewById(R.id.txt_storename);
                txt_address = (TextView) itemView.findViewById(R.id.txt_address);
                tv_margintop = (TextView) itemView.findViewById(R.id.tv_margintop);
                txt_folleowercount = (TextView) itemView.findViewById(R.id.txt_folleowercount);

                itemView.setOnClickListener(this);

            }


            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                clickLogTracking("storeview", mItems.get(position).get("id"));
                Fragment f1 = new StoreDetailFragment();
                Bundle b1 = new Bundle();
                b1.putString("cat_id", mItems.get(position).get("id"));
                b1.putString("cat_name", subCatName);
                b1.putInt("position", position);
                selectedPosition = position;
                f1.setArguments(b1);
                FragmentTransaction t1 = getFragmentManager().beginTransaction();
                t1.hide(getFragmentManager().findFragmentById(R.id.frame_container));
                t1.add(R.id.frame_container, f1);
//                    t1.replace(R.id.frame_container, f1);
                t1.addToBackStack(null);
                t1.commit();
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
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
}

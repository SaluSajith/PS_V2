package com.hit.pretstreet.pretstreet.subcategory_n_storelist;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.FILTERPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTINGLINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTING_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;
import static com.hit.pretstreet.pretstreet.core.utils.PreferenceServices.dropdownloc;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

public class StoreListingActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallbackStoreList, View.OnClickListener {

    @BindView(R.id.ll_empty) View ll_empty;
    @BindView(R.id.tv_msg) TextViewPret tv_msg;
    @BindView(R.id.btn_delhi) ButtonPret btn_delhi;
    @BindView(R.id.btn_retry) ButtonPret btn_retry;
    @BindView(R.id.btn_mumbai) ButtonPret btn_mumbai;
    @BindView(R.id.ll_scroll) LinearLayout ll_scroll;
    @BindView(R.id.nsv_header) AppBarLayout nsv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.rv_storelist) RecyclerView rv_storelist;
    @BindView(R.id.btn_try_another) ButtonPret btn_try_another;

    TextViewPret[] txtname;
    AppCompatImageView iv_filter;
    JsonRequestController jsonRequestController;
    SubCategoryController subCategoryController;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;

    private static final int STORE_DETAILS = 14;
    private static final int FILTER_PAGE = 15;

    int pageCount = 0;
    boolean requestCalled = false;
    boolean loadmore = true, first = true;
    private static String catTag = "";

    private static ArrayList<StoreListModel> storeListModels;
    private ArrayList<TwoLevelDataModel> dataModel;
    private JSONArray arrayFilter;

    private String mTitle, mpagetypeid, mClicktypeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_listing);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        arrayFilter = new JSONArray();
        dataModel = new ArrayList<>();
        storeListModels = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        AppCompatImageView iv_menu = toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AppCompatImageView iv_search = toolbar.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage();
            }
        });
        iv_filter = toolbar.findViewById(R.id.iv_filter);
        iv_filter.setVisibility(View.VISIBLE);
        iv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterPage();
            }
        });
        ll_scroll.setVisibility(View.VISIBLE);
        ll_empty.setVisibility(View.INVISIBLE);
        nsv_header.bringToFront();

        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("mSubTitle")) {
                mTitle = getIntent().getStringExtra("mSubTitle");
                mpagetypeid = getIntent().getStringExtra(Constant.PRE_PAGE_KEY);
                mClicktypeid = getIntent().getStringExtra(Constant.CLICKTYPE_KEY);
            }
        }
        tv_cat_name.setText(mTitle);
        setupCategoryScrollviewParams();
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());

        setListUtility();

        /*Get shoplist details of selected category*/
        createScrollingHeader();
        refreshListviewOnScrolling();
    }

    private void setupCategoryScrollviewParams(){
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) ll_scroll.getLayoutParams();
        if (mTitle.equalsIgnoreCase("DESIGNERS")) //TODO dynamic header margin
            layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.padding_xxsmall), 0, 0);
        else if (mTitle.equalsIgnoreCase("RETAIL")) //TODO dynamic header margin
            layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.padding_xsmall), 0, 0);
        else
            layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.padding_small), 0, 0);
        ll_scroll.setLayoutParams(layoutParams);
    }

    private void setListUtility(){
        Utility.setListLayoutManager_(rv_storelist, StoreListingActivity.this);
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(Glide.with(this),
                rv_storelist, StoreListingActivity.this, storeListModels);
        storeList_recyclerAdapter.setHasStableIds(true);
        rv_storelist.setAdapter(storeList_recyclerAdapter);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        RecyclerView.ItemAnimator animator = rv_storelist.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void refreshListviewOnScrolling() {
        storeList_recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!requestCalled) {
                    requestCalled = true;
                    first = false;
                    if (loadmore) {
                        getShoplist(catTag, false);
                    }
                }
            }

            @Override
            public void reachedLastItem() {
                if (!loadmore)
                    displaySnackBar("No more stores available!");
            }
        });
    }

    private void getShoplist(String mCatid, boolean first) {
        loadmore = true;
        btn_try_another.setVisibility(View.GONE);
        JSONObject resultJson = SubCategoryController.getShoplistJson(mCatid,
                ++pageCount + "", mpagetypeid, mClicktypeid, arrayFilter);
        if (first)
            this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, STORELISTING_URL);
    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        selectNewLocation();
    }

    @OnClick(R.id.iv_location)
    public void onIv_LocationPressed() {
        selectNewLocation();
    }

    @OnClick(R.id.btn_try_another)
    public void onBtn_TryAnottherPressed() {
        selectNewLocation();
    }

    @OnClick(R.id.btn_mumbai)
    public void onBtn_MumbaiPressed() {
        PreferenceServices.instance().saveCurrentLocation("Mumbai");
        PreferenceServices.instance().saveLatitute( "19.0760");
        PreferenceServices.instance().saveLongitute( "72.8777");
        PreferenceServices.instance().saveLocationType(dropdownloc);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        getNewList();
        getShoplist(catTag, first);
    }

    @OnClick(R.id.btn_delhi)
    public void onBtn_DelhiPressed() {
        PreferenceServices.instance().saveCurrentLocation("New Delhi");
        PreferenceServices.instance().saveLatitute( "28.6139");
        PreferenceServices.instance().saveLongitute( "77.2090");
        PreferenceServices.instance().saveLocationType(dropdownloc);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());
        getNewList();
        getShoplist((String) catTag, first);
    }

    private void openSearchPage() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
        intent.putExtra(ID_KEY, getIntent().getStringExtra(ID_KEY));
        startActivity(intent);
    }

    private void openFilterPage() {
        Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
        bundle.putString(ID_KEY, getIntent().getStringExtra(ID_KEY));
        bundle.putSerializable(PARCEL_KEY, this.dataModel);
        intent.putExtras(bundle);
        startActivityForResult(intent, parseInt(FILTERPAGE));
    }

    @SuppressLint("InflateParams")
    private void createScrollingHeader() {

        final ArrayList<HomeCatItems> homeSubCategories = (ArrayList<HomeCatItems>) getIntent().getSerializableExtra("contentData");
        int index = 0;

        txtname = new TextViewPret[homeSubCategories.size() + 1];
        LayoutInflater infl = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = infl.inflate(R.layout.row_storelistscroll, null);
        txtname[0] = view.findViewById(R.id.tv_catname);
        txtname[0].setText("All");
        ll_scroll.addView(view);
        txtname[0].setOnClickListener(this);

        if (homeSubCategories.size() > 0) {
            HomeCatContentData contentData = homeSubCategories.get(0).getHomeContentData();
            txtname[0].setTag(getIntent().getStringExtra(ID_KEY));
        } else {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) txtname[0].getLayoutParams();
            layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.padding_xxhuge), 0, 0);
            txtname[0].requestLayout();
            txtname[0].setLayoutParams(layoutParams);

            txtname[0].setTag(getIntent().getStringExtra("mCatId"));
            txtname[0].performClick();
        }

        for (HomeCatItems object : homeSubCategories) {
            index++;
            HomeCatItems homeCatItems = object;
            HomeCatContentData contentData = homeCatItems.getHomeContentData();

            infl = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infl.inflate(R.layout.row_storelistscroll, null);
            txtname[index] = view.findViewById(R.id.tv_catname);
            txtname[index].setText(contentData.getCategoryName());
            txtname[index].setTag(contentData.getCategoryId());
            ll_scroll.addView(view);
            txtname[index].setOnClickListener(this);
        }
        index = 0;
        for (HomeCatItems object : homeSubCategories) {
            index++;
            if (txtname[index].getText().toString().trim().equalsIgnoreCase(getIntent().getStringExtra("mTitle").trim())) {
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.black));
                txtname[index].performClick();
            } else {
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
            }
        }
    }

    private void handleResponse(final JSONObject response) {
        try {
            String url = response.getString("URL");
            switch (url) {
                case STORELISTING_URL:
                    requestCalled = false;
                    new JSONParsing().execute(response.toString());
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                    this.hideDialog();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class JSONParsing extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(params[0]);
                ArrayList<StoreListModel> storeListMode = SubCategoryController.getList(jsonObject);
                storeListModels.addAll(storeListMode);
                if (storeListMode.size() == 0)
                    loadmore = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setAdapter();
        }
    }

    private void selectNewLocation(){
        Intent intent = new Intent(StoreListingActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }

    private void setAdapter() {
        if (first)
            rv_storelist.setAdapter(storeList_recyclerAdapter);
        else
            storeList_recyclerAdapter.notifyDataSetChanged();
        storeList_recyclerAdapter.setLoaded();

        if (storeListModels.size() == 0) {
            handleEmptyView();
        } else ll_empty.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDialog();
            }
        }, 1000);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        subCategoryController = new SubCategoryController(this);
    }

    @Override
    public void onResponse(JSONObject response) {
        ll_empty.setVisibility(View.GONE);
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        requestCalled = false;
        displaySnackBar(error);
        if (storeListModels.size() == 0) {
            ll_empty.setVisibility(View.VISIBLE);
            TextViewPret tv_msg = ll_empty.findViewById(R.id.tv_msg);
            tv_msg.setText(error);
        }
    }

    private void handleEmptyView(){
        btn_mumbai.setVisibility(View.VISIBLE);
        btn_delhi.setVisibility(View.VISIBLE);
        tv_msg.setText(getResources().getString(R.string.no_stores));
        btn_try_another.setVisibility(View.VISIBLE);
        ll_empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < txtname.length; i++) {
            txtname[i].setBackgroundColor(ContextCompat.getColor(StoreListingActivity.this, R.color.transparent));
            txtname[i].setTextColor(ContextCompat.getColor(StoreListingActivity.this, R.color.yellow_storelist_scroll));
        }
        TextViewPret textViewPret = (TextViewPret) v;
        textViewPret.setBackgroundColor(ContextCompat.getColor(StoreListingActivity.this, R.color.yellow_storelist_scroll));
        textViewPret.setTextColor(ContextCompat.getColor(StoreListingActivity.this, R.color.black));

        catTag = textViewPret.getTag().toString() + "";
        getNewList();
        getShoplist((String) textViewPret.getTag(), first);
    }

    private void getNewList(){
        pageCount = 0;
        first = true;
        rv_storelist.setAdapter(null);
        storeListModels.clear();
    }

    @Override
    public void buttonClick(StoreListModel storeListModel) {
        openNext(storeListModel, STORELISTINGPAGE, STORELISTINGLINK);
    }

    @Override
    public void updateFollowStatus(String id) {
        JSONObject resultJson = SubCategoryController.updateFollowCount(id, Constant.STORELISTINGPAGE, Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case FILTER_PAGE:
                        Bundle bundle = data.getExtras();
                        dataModel = (ArrayList<TwoLevelDataModel>) bundle.getSerializable(PARCEL_KEY);
                        arrayFilter = SubCategoryController.createFilterModel(dataModel);
                        storeListModels.clear();
                        pageCount = 0;
                        getShoplist(catTag, true);
                        break;
                    case STORE_DETAILS:
                        int status = Integer.parseInt(data.getStringExtra(PARCEL_KEY));
                        String storeId = data.getStringExtra(ID_KEY);
                        storeList_recyclerAdapter.updateFollowStatus_fromDetails(status, storeId, data.getStringExtra("followcount"));
                        storeList_recyclerAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}

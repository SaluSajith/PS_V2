package com.hit.pretstreet.pretstreet.navigationitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.FOLLOWINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.FOLLOWING_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STOREDETAILSPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;

public class FollowingActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, View.OnClickListener, ButtonClickCallbackStoreList, LoginCallbackInterface {

    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.ll_scroll) LinearLayout ll_scroll;
    @BindView(R.id.rv_storelist)RecyclerView rv_storelist;
    @BindView(R.id.ll_header) LinearLayout ll_header;
    @BindView(R.id.ll_location) LinearLayout ll_location;
    @BindView(R.id.nsv_header)AppBarLayout nsv_header;
    @BindView(R.id.ll_empty) View ll_empty;

    JsonRequestController jsonRequestController;
    NavItemsController navItemsController;
    SubCategoryController subCategoryController;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;
    TextViewPret[] txtname;

    Context context;
    int pageCount=1;
    boolean requestCalled = false;
    boolean loadmore = true, first = true;
    String catTag = "";
    ArrayList<StoreListModel> storeListModels;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            pageCount = 1;
            first = true;
            rv_storelist.setAdapter(null);
            storeListModels.clear();
           /* txtname[0].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
            txtname[0].setTextColor(ContextCompat.getColor(this, R.color.black));*/
            getFollowingData("0", true); //For ALL catid = 0
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_listing);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        context = getApplicationContext();
        storeListModels = new ArrayList<>();
        Utility.setListLayoutManager_(rv_storelist, FollowingActivity.this);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(context));
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(Glide.with(this), rv_storelist, FollowingActivity.this, storeListModels);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppCompatImageView iv_menu = (AppCompatImageView) toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AppCompatImageView iv_search = (AppCompatImageView) toolbar.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPage();
            }
        });
        ll_scroll.setVisibility(View.VISIBLE);
        nsv_header.bringToFront();

        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) ll_scroll.getLayoutParams();
        layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.padding_xsmall), 0, 0);
        ll_scroll.setLayoutParams(layoutParams);

        String title = getIntent().getStringExtra("mSubTitle");
        tv_cat_name.setText(title);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());

        refreshListviewOnScrolling();
    }

    private void refreshListviewOnScrolling(){
        storeList_recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!requestCalled) {
                    requestCalled = true;
                    first = false;
                    if (loadmore) {
                        pageCount++;
                        getFollowingData(catTag, false);
                    }
                }
            }

            @Override
            public void reachedLastItem() {
                //if (!loadmore)
                    //displaySnackBar("No more stores available!");
            }
        });
    }

    private void openSearchPage(){
        finish();
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
        intent.putExtra(ID_KEY, getIntent().getStringExtra(ID_KEY));
        startActivity(intent);
    }
    private void getFollowingData(String catId, boolean first){
        JSONObject resultJson = navItemsController.getFollowinglistJson(catId,
                pageCount+"", getIntent().getStringExtra(Constant.PRE_PAGE_KEY));
        if(first)
            showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, FOLLOWING_URL);
    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        Intent intent = new Intent(FollowingActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }

    /*@OnClick(R.id.ll_location)
    public void onIvLocationPressed() {
        Intent intent = new Intent(FollowingActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }*/

    private void createScrollingHeader(ArrayList<SearchModel> homeSubCategories){
        if(txtname==null) {
            int index = 0;
            txtname = new TextViewPret[homeSubCategories.size()];

            for (SearchModel object : homeSubCategories) {
                SearchModel searchModel = object;
                LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View view = infl.inflate(R.layout.row_storelistscroll, null);
                txtname[index] = (TextViewPret) view.findViewById(R.id.tv_catname);
                txtname[index].setText(searchModel.getCategory());
                txtname[index].setTag(searchModel.getId());
                ll_scroll.addView(view);
                txtname[index].setOnClickListener(this);
                index++;
            }
            index = 0;
            for (SearchModel object : homeSubCategories) {
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
                index++;
            }
            txtname[0].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
            txtname[0].setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case FOLLOWING_URL:
                    requestCalled = false;
                    ArrayList<SearchModel> homeSubCategories = navItemsController.getCategoryListHeader(response);
                    createScrollingHeader(homeSubCategories);
                    storeListModels.addAll(navItemsController.getCategoryList(response));
                    loadmore = false;
                    setAdapter();
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
                    this.hideDialog();
                    break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(){
        if(first) {
            rv_storelist.setAdapter(storeList_recyclerAdapter);
        } else
            storeList_recyclerAdapter.notifyDataSetChanged();
        if(storeListModels.size()==0)
            ll_empty.setVisibility(View.VISIBLE);
        else ll_empty.setVisibility(View.INVISIBLE);
        storeList_recyclerAdapter.setLoaded();
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
        navItemsController = new NavItemsController(this);
        subCategoryController = new SubCategoryController(this);
    }

    @Override
    public void onResponse(JSONObject response) {
        handleResponse(response);
        //this.hideDialog();man
    }

    @Override
    public void onError(String error) {
        hideDialog();
        loadmore = false;
        requestCalled = false;
        displaySnackBar(error);
    }

    @Override
    public void onClick(View v) {
        for (int i=0;i<txtname.length;i++) {
            txtname[i].setBackgroundColor(ContextCompat.getColor(FollowingActivity.this, R.color.transparent));
            txtname[i].setTextColor(ContextCompat.getColor(FollowingActivity.this, R.color.yellow_storelist_scroll));
        }
        TextViewPret textViewPret = (TextViewPret) v;
        textViewPret.setBackgroundColor(ContextCompat.getColor(FollowingActivity.this, R.color.yellow_storelist_scroll));
        textViewPret.setTextColor(ContextCompat.getColor(FollowingActivity.this, R.color.black));

        catTag = textViewPret.getTag().toString()+"";
        pageCount = 1;
        first = true;
        rv_storelist.setAdapter(null);
        storeListModels.clear();
        getFollowingData((String) textViewPret.getTag(), first);
    }

    @Override
    public void buttonClick(StoreListModel storeListModel) {
        if(storeListModel.getPageTypeId().equals(STOREDETAILSPAGE)){
            for (int index = 0;index<txtname.length;index++) {
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
            }
            txtname[0].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_storelist_scroll));
            txtname[0].setTextColor(ContextCompat.getColor(this, R.color.black));
            Intent intent = new Intent(FollowingActivity.this, StoreDetailsActivity.class);
            intent.putExtra(Constant.PARCEL_KEY, storeListModel);
            intent.putExtra(CLICKTYPE_KEY, "");
            intent.putExtra(Constant.PRE_PAGE_KEY, FOLLOWINGPAGE);
            startActivity(intent);
        } else {
            openNext(storeListModel, FOLLOWINGPAGE, "");
        }
    }
    @Override
    public void updateFollowStatus(String id) {
    }

    @Override
    public void validateCallback(EdittextPret editText, String message, int type) {
    }

    @Override
    public void validationSuccess(String phonenumber) {
    }

    @Override
    public void validationSuccess(LoginSession loginSession) {
    }

    @Override
    public void validationSuccess(JSONObject jsonObject, int type) {
    }
}
package com.hit.pretstreet.pretstreet.subcategory;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.fragments.HomeFragment;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.splashnlogin.controllers.LoginController;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.subcategory.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListingActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallback, View.OnClickListener {

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.ll_header) LinearLayout ll_header;
    @BindView(R.id.ll_scroll) LinearLayout ll_scroll;
    @BindView(R.id.rv_storelist)RecyclerView rv_storelist;

    JsonRequestController jsonRequestController;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;
    TextViewPret[] txtname;

    int pageCount=0, totalPages;
    public static int selectedPosition;
    boolean requestCalled = false;
    boolean loadmore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_listing);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ll_header.bringToFront();
        ll_scroll.setVisibility(View.VISIBLE);

        String title = getIntent().getStringExtra("mTitle");
        tv_cat_name.setText(title);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());

        final LinearLayoutManager mManager = new LinearLayoutManager(getApplicationContext());
        rv_storelist.setLayoutManager(mManager);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        rv_storelist.setNestedScrollingEnabled(false);

        createHeader();
    }

    private void getShoplist(String mCatid){
            JSONObject resultJson = SubCategoryController.getShoplistJson(mCatid, Constant.LIMIT, pageCount+"", "0");
            this.showProgressDialog(getResources().getString(R.string.loading));
            jsonRequestController.sendRequest(this, resultJson, Constant.STORELISTING_URL);
    }

    private void createHeader(){

        HomeCatContentData catContentData = (HomeCatContentData) getIntent()
                .getExtras().getSerializable("contentData");
        final ArrayList<HomeCatItems> homeSubCategories = catContentData.getHomeSubCategoryArrayList();
        int index = 0;

        txtname = new TextViewPret[homeSubCategories.size()+1];
        LayoutInflater infl = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = infl.inflate(R.layout.row_storelistscroll, null);
        txtname[0] = (TextViewPret) view.findViewById(R.id.tv_catname);
        txtname[0].setText("All");
        ll_scroll.addView(view);
        txtname[0].setOnClickListener(this);

        for (HomeCatItems object: homeSubCategories) {
            index++;
            HomeCatItems homeCatItems = object;
            HomeCatContentData contentData = homeCatItems.getHomeContentData();

            infl = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infl.inflate(R.layout.row_storelistscroll, null);
            txtname[index] = (TextViewPret) view.findViewById(R.id.tv_catname);
            txtname[index].setText(contentData.getCategoryName());
            ll_scroll.addView(view);
            txtname[index].setOnClickListener(this);

            if(txtname[index].getText().toString().trim().equalsIgnoreCase(getIntent().getStringExtra("mTitle").trim())){
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_indicator));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.black));
            }else{
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.yellow_indicator));
            }
        }
    }

    private void handleResponse(JSONObject response){
        String strsuccess = null;
        try {
            String url = response.getString("URL");
            strsuccess = response.getString("Status");
            if (strsuccess.equals("1")) {
                //displaySnackBar(response.getString("CustomerMessage"));
                switch (url){
                    case Constant.STOREDETAILSPAGE:
                        ArrayList<StoreListModel> storeListModels = SubCategoryController.getList(response);
                        setAdapter(storeListModels);
                        break;
                    default: break;
                }
            } else {
                displaySnackBar(response.getString("Message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(ArrayList<StoreListModel> storeListModels){
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(getApplicationContext(), R.layout.row_list_store1, storeListModels);
        rv_storelist.setAdapter(storeList_recyclerAdapter);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
    }

    @Override
    public void buttonClick(int id) {

    }

    @Override
    public void onResponse(JSONObject response) {
        this.hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar( error);
    }

    @Override
    public void onClick(View v) {
        rv_storelist.setAdapter(null);
        for (int i=0;i<txtname.length;i++) {
            txtname[i].setBackgroundColor(ContextCompat.getColor(StoreListingActivity.this, R.color.black));
            txtname[i].setTextColor(ContextCompat.getColor(StoreListingActivity.this, R.color.yellow_indicator));
        }
        TextViewPret textViewPret = (TextViewPret) v;
        textViewPret.setBackgroundColor(ContextCompat.getColor(StoreListingActivity.this, R.color.yellow_indicator));
        textViewPret.setTextColor(ContextCompat.getColor(StoreListingActivity.this, R.color.black));

        HomeCatContentData catContentData = (HomeCatContentData) getIntent()
                .getExtras().getSerializable("contentData");
        final ArrayList<HomeCatItems> homeSubCategories = catContentData.getHomeSubCategoryArrayList();
        HomeCatContentData contentData = homeSubCategories.get(0).getHomeContentData();
        getShoplist(contentData.getCategoryId());
    }
}

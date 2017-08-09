package com.hit.pretstreet.pretstreet.subcategory_n_storelist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
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
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter$ShopsHolder_ViewBinding;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTING_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;

public class StoreListingActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ButtonClickCallbackStoreList, View.OnClickListener {

    @BindView(R.id.content) FrameLayout fl_content;
    @BindView(R.id.nsv_header)NestedScrollView nsv_header;
    @BindView(R.id.tv_cat_name) TextViewPret tv_cat_name;
    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.ll_header) LinearLayout ll_header;
    @BindView(R.id.ll_scroll) LinearLayout ll_scroll;
    @BindView(R.id.rv_storelist)RecyclerView rv_storelist;

    JsonRequestController jsonRequestController;
    SubCategoryController subCategoryController;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;
    TextViewPret[] txtname;

    int pageCount=0, totalPages, total;
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
        ll_scroll.setVisibility(View.VISIBLE);
        ll_header.bringToFront();

        String title = getIntent().getStringExtra("mTitle");
        tv_cat_name.setText(title);
        tv_location.setText(PreferenceServices.getInstance().getCurrentLocation());

        Utility.setListLayoutManager(rv_storelist, StoreListingActivity.this);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        /*Get shoplist details of selected category*/
        createScrollingHeader();
    }

    private void getShoplist(String mCatid){
        String pageid = getIntent().getStringExtra(Constant.PRE_PAGE_KEY);
        JSONObject resultJson = SubCategoryController.getShoplistJson(mCatid, pageCount+"", "0", pageid);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, STORELISTING_URL);
    }

    private void createScrollingHeader(){

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

        if(homeSubCategories.size()>0){
            HomeCatContentData contentData = homeSubCategories.get(0).getHomeContentData();
            txtname[0].setTag(contentData.getMainCatId());
        } else {
            txtname[0].performClick();
            txtname[0].setTag("");
            ll_scroll.setVisibility(View.GONE);
        }


        for (HomeCatItems object: homeSubCategories) {
            index++;
            HomeCatItems homeCatItems = object;
            HomeCatContentData contentData = homeCatItems.getHomeContentData();

            infl = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infl.inflate(R.layout.row_storelistscroll, null);
            txtname[index] = (TextViewPret) view.findViewById(R.id.tv_catname);
            txtname[index].setText(contentData.getCategoryName());
            txtname[index].setTag(contentData.getCategoryId());
            ll_scroll.addView(view);
            txtname[index].setOnClickListener(this);
        }
        index = 0;
        for (HomeCatItems object: homeSubCategories) {
            index++;
            if(txtname[index].getText().toString().trim().equalsIgnoreCase(getIntent().getStringExtra("mTitle").trim())){
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_indicator));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.black));
                txtname[index].performClick();
            }else{
                txtname[index].setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                txtname[index].setTextColor(ContextCompat.getColor(this, R.color.yellow_indicator));
            }
        }
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case STORELISTING_URL:
                    ArrayList<StoreListModel> storeListModels = SubCategoryController.getList(response);
                    setAdapter(storeListModels);
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
                    break;
                default: break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(ArrayList<StoreListModel> storeListModels){
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(StoreListingActivity.this, storeListModels);
        rv_storelist.setAdapter(storeList_recyclerAdapter);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        subCategoryController = new SubCategoryController(this);
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
        System.out.println("mnbnm");
        for (int i=0;i<txtname.length;i++) {
            txtname[i].setBackgroundColor(ContextCompat.getColor(StoreListingActivity.this, R.color.black));
            txtname[i].setTextColor(ContextCompat.getColor(StoreListingActivity.this, R.color.yellow_indicator));
        }
        TextViewPret textViewPret = (TextViewPret) v;
        textViewPret.setBackgroundColor(ContextCompat.getColor(StoreListingActivity.this, R.color.yellow_indicator));
        textViewPret.setTextColor(ContextCompat.getColor(StoreListingActivity.this, R.color.black));

        getShoplist((String) textViewPret.getTag());
    }

    @Override
    public void buttonClick(StoreListModel storeListModel) {
        switch (storeListModel.getPageTypeId()){
            case Constant.STOREDETAILSPAGE:
                Intent intent = new Intent(StoreListingActivity.this, StoreDetailsActivity.class);
                intent.putExtra(Constant.PARCEL_KEY, storeListModel);
                intent.putExtra(Constant.PRE_PAGE_KEY, Constant.STORELISTINGPAGE);
                startActivity(intent);
                break;
            default: break;
        }
    }
    @Override
    public void updateFollowStatus(String id) {
        JSONObject resultJson = SubCategoryController.updateFollowCount(id, Constant.STORELISTINGPAGE,  Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }
}

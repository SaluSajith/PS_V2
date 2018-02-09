package com.hit.pretstreet.pretstreet.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.search.controllers.SearchController;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.interfaces.ImageClickCallback;
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

import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.LIMIT;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTILINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTOREPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTORE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STORELISTINGPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.UPDATEFOLLOWSTATUS_URL;
/**
 * Collapsing Header activity of Multistore listing
 **/
public class MultistoreActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, ImageClickCallback, ButtonClickCallbackStoreList {

    private static final int STORE_DETAILS = 14;

    JsonRequestController jsonRequestController;
    SubCategoryController subCategoryController;
    SearchController searchController;

    @BindView(R.id.tv_name)TextViewPret tv_name;
    @BindView(R.id.tv_storecount)TextViewPret tv_storecount;
    @BindView(R.id.rv_storelist)RecyclerView rv_storelist;

    String shareText;
    Context context;
    ArrayList<StoreListModel> storeListModels;
    StoreList_RecyclerAdapter storeList_recyclerAdapter;

    int pageCount = 1;
    boolean loadmore = true;
    boolean requestCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multistore);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();
    }

    private void initUi(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        context = getApplicationContext();
        storeListModels = new ArrayList<>();

        Utility.setListLayoutManager_(rv_storelist, MultistoreActivity.this);
        rv_storelist.addItemDecoration(new SimpleDividerItemDecoration(context));
        setAdapter();
        refreshListviewOnScrolling();
        getShoplist();
    }

    private void getShoplist(){
        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras()
                    .containsKey(ID_KEY)) {
                String pageid = getIntent().getStringExtra(PRE_PAGE_KEY);
                String clicktypeid = getIntent().getStringExtra(CLICKTYPE_KEY);
                String mCatid = getIntent().getStringExtra(Constant.ID_KEY);
                JSONObject resultJson = searchController.getMultiStoreListJson(mCatid, pageid, clicktypeid, pageCount);//caat, prepage, clicktype, id
                if(pageCount ==1)
                    this.showProgressDialog(getResources().getString(R.string.loading));
                jsonRequestController.sendRequest(this, resultJson, MULTISTORE_URL);
            }
        }
    }

    private void setAdapter(){
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(Glide.with(this), rv_storelist, MultistoreActivity.this, storeListModels);
        rv_storelist.setAdapter(storeList_recyclerAdapter);
    }

    private void notifyListData(ArrayList<StoreListModel> storeListModels){
        storeList_recyclerAdapter.notifyDataSetChanged();
        //adapter.setHasStableIds(true);
        if(storeListModels.size()<Integer.parseInt(LIMIT))
            loadmore = false;
        else
            loadmore = true;
        storeList_recyclerAdapter.setLoaded();
    }

    private void setupCollapsingHeader(String title, String imageUrl){
        CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(context, R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(context, R.color.white));
        loadBackdrop(imageUrl);
    }

    private void refreshListviewOnScrolling(){
        storeList_recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(loadmore) {
                    pageCount++;
                    requestCalled = true;
                    getShoplist();
                }
                else; //displaySnackBar("No more data available!");
            }

            @Override
            public void reachedLastItem() {

            }
        });
    }

    private void loadBackdrop(String imageUrl) {
        final AppCompatImageView imageView = findViewById(R.id.backdrop);
        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                .placeholder(R.drawable.default_banner)
                .into(imageView);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        searchController = new SearchController(this);
    }

    /**Handling response corresponding to the URL
     * @param response response corresponding to each URL - here I am appending the URL itself
     *                 to the response so that I will be able to handle each response seperately*/
    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            //displaySnackBar(response.getString("CustomerMessage"));
            switch (url){
                case Constant.MULTISTORE_URL:
                    JSONObject jsonObject = response.getJSONObject("Data");
                    if(pageCount==1) {
                        tv_name.setText(jsonObject.getString("MultistoreTitle"));
                        try {
                            int storeCount = Integer.parseInt(jsonObject.getString("MultistoreCount"));
                            tv_storecount.setText( storeCount==1  ? storeCount+ " Store" : storeCount+ " Stores");
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setupCollapsingHeader(jsonObject.getString("MultistoreTitle"), jsonObject.getString("MultistoreImageSource"));
                        shareText = jsonObject.getString("Share");
                    }
                    ArrayList<StoreListModel> refreshedStoreListModels = searchController.getList(response);
                    if(refreshedStoreListModels.size()>0) {
                        storeListModels.addAll(refreshedStoreListModels);
                        notifyListData(storeListModels);
                    }
                    break;
                case UPDATEFOLLOWSTATUS_URL:
                    JSONObject object = response.getJSONObject("Data");
                    storeList_recyclerAdapter.updateFollowStatus(object.getInt("FollowingStatus"),
                            object.getString("StoreId"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                break;
                default: break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_share:
                shareUrl(shareText);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResponse(JSONObject response) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDialog();
            }
        }, 1000);
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar( error);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    @Override
    public void onClicked(int position, ArrayList<String> imageModels) {
        ArrayList<String> imageModels1 = imageModels;
        Intent intent = new Intent(MultistoreActivity.this, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(PRE_PAGE_KEY, Integer.parseInt(STORELISTINGPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);

    }

    @Override
    public void buttonClick(StoreListModel storeListModel) {
        openNext(storeListModel, MULTISTOREPAGE, MULTILINK);
    }

    @Override
    public void updateFollowStatus(String id) {
        JSONObject resultJson = subCategoryController.updateFollowCount(id, MULTISTOREPAGE,  Constant.FOLLOWLINK);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, UPDATEFOLLOWSTATUS_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode  == RESULT_OK) {
                if (requestCode == STORE_DETAILS){
                    int status = Integer.parseInt(data.getStringExtra(PARCEL_KEY));
                    String storeId = data.getStringExtra(ID_KEY);
                    storeList_recyclerAdapter.updateFollowStatus_fromDetails(status, storeId, data.getStringExtra("followcount"));
                    storeList_recyclerAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) { }
    }
}

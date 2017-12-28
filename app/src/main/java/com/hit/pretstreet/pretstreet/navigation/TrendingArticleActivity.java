package com.hit.pretstreet.pretstreet.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingArticleAdapter;
import com.hit.pretstreet.pretstreet.navigation.controllers.DetailsPageController;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ARTICLEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.CLICKTYPE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAYARTICLE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAYPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.MULTISTOREPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.STOREDETAILSPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDARTICLELINK;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGARTICLE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGLIKE_URL;

public class TrendingArticleActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, TrendingCallback, ZoomedViewListener {

    @BindView(R.id.rv_trendingarticle)RecyclerView rv_trendingarticle;
    @BindView(R.id.txt_description)TextViewPret txt_description;
    @BindView(R.id.ib_like)ImageView ib_like;
    @BindView(R.id.txt_name)TextViewPret txt_name;

    JsonRequestController jsonRequestController;
    DetailsPageController detailsPageController;

    String mId = "";
    Context context;
    private TrendingItems trendingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_article);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        context = TrendingArticleActivity.this;
        Utility.setListLayoutManager_(rv_trendingarticle, context);

        trendingItems = (TrendingItems)getIntent()
                .getSerializableExtra(PARCEL_KEY);
        String pagekey = trendingItems.getPagetypeid();
        String clicktype = trendingItems.getClicktype();
        mId = trendingItems.getId();
        getTrendingArticle(pagekey, clicktype, mId);
        if(trendingItems.getPagetype().equals(GIVEAWAYPAGE))
            ib_like.setVisibility(View.GONE);
    }

    @OnClick(R.id.ib_like)
    public void onLikePressed() {
        JSONObject resultJson = detailsPageController.getTrendinglikeJson(mId ,
                getIntent().getStringExtra(PRE_PAGE_KEY));
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDINGLIKE_URL);
    }

    @OnClick(R.id.iv_back)
    public void onBackPress() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putString(ID_KEY, trendingItems.getId());
        int likeStatus = trendingItems.getLike() == true ? 1 : 0;
        b.putString(PARCEL_KEY, likeStatus+"");
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void getTrendingArticle(String prepage, String clicktype, String trid){
        JSONObject resultJson = detailsPageController.getTrendingArticle(prepage, clicktype, trid);
        this.showProgressDialog(getResources().getString(R.string.loading));
        if(trendingItems.getPagetype().equals(GIVEAWAYPAGE))
            jsonRequestController.sendRequest(this, resultJson, GIVEAWAYARTICLE_URL);
        else
            jsonRequestController.sendRequest(this, resultJson, TRENDINGARTICLE_URL);
    }

    private void setupArticle(ArrayList<TrendingItems> trendingArticle){
        TrendingArticleAdapter adapter = new TrendingArticleAdapter(context, trendingArticle, this);
        rv_trendingarticle.setAdapter(adapter);
    }

    private void handleResponse(JSONObject response){
        try {
            String url = response.getString("URL");
            switch (url){
                case Constant.TRENDINGARTICLE_URL:
                    txt_name.setText(detailsPageController.getTitle(response));
                    ib_like.setTag(detailsPageController.getLikeStatus(response) == false ? R.drawable.grey_heart : R.drawable.red_heart);
                    ib_like.setImageResource(detailsPageController.getLikeStatus(response) == false ? R.drawable.grey_heart : R.drawable.red_heart);
                    ArrayList<TrendingItems> trendingArticle = detailsPageController.getTrendingArticle(response);
                    trendingItems.setLike(detailsPageController.getLikeStatus(response));
                    setupArticle(trendingArticle);
                    this.hideDialog();
                    break;
                case Constant.GIVEAWAYARTICLE_URL:
                    txt_name.setText(detailsPageController.getTitle(response));
                    ib_like.setTag(detailsPageController.getLikeStatus(response) == false ? R.drawable.grey_heart : R.drawable.red_heart);
                    ib_like.setImageResource(detailsPageController.getLikeStatus(response) == false ? R.drawable.grey_heart : R.drawable.red_heart);
                    trendingArticle = detailsPageController.getTrendingArticle(response);
                    trendingItems.setLike(detailsPageController.getLikeStatus(response));
                    setupArticle(trendingArticle);
                    this.hideDialog();
                    break;
                case TRENDINGLIKE_URL:
                    this.hideDialog();
                    JSONObject object = response.getJSONObject("Data");
                    ib_like.setTag(object.getInt("LikeStatus") == 1 ? R.drawable.red_heart : R.drawable.grey_heart);
                    ib_like.setImageResource(object.getInt("LikeStatus") == 1 ? R.drawable.red_heart : R.drawable.grey_heart);
                    trendingItems.setLike(object.getInt("LikeStatus") == 0 ? false : true);
                    break;
                default: break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        detailsPageController = new DetailsPageController(this);
    }

    @Override
    public void onResponse(JSONObject response) {
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar( error);
    }

    @Override
    public void bindData(ArrayList<TrendingItems> trendingItems) {
        Intent intent;
        String pagetypeid = trendingItems.get(0).getPagetypeid();
        String id = trendingItems.get(0).getId();
        switch (pagetypeid){
            case MULTISTOREPAGE:
                intent = new Intent(TrendingArticleActivity.this, MultistoreActivity.class);
                intent.putExtra(ID_KEY, id);
                intent.putExtra(PRE_PAGE_KEY, ARTICLEPAGE);
                startActivity(intent);
                break;
            case STOREDETAILSPAGE:
                StoreListModel storeListModel =  new StoreListModel();
                storeListModel.setId(id);
                intent = new Intent(TrendingArticleActivity.this, StoreDetailsActivity.class);
                intent.putExtra(PARCEL_KEY, storeListModel);
                intent.putExtra(CLICKTYPE_KEY, TRENDARTICLELINK);
                intent.putExtra(PRE_PAGE_KEY, ARTICLEPAGE);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClicked(int position, ArrayList<String> mImagearray) {
        ArrayList<String> imageModels1 = mImagearray;
        Intent intent = new Intent(context, FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(PRE_PAGE_KEY, Integer.parseInt(Constant.HOMEPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);
    }
}

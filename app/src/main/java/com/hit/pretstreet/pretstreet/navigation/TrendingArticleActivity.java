package com.hit.pretstreet.pretstreet.navigation;

import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingAdapter;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingArticleAdapter;
import com.hit.pretstreet.pretstreet.navigation.controllers.DetailsPageController;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONLIKE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGARTICLE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGLIKE_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDING_URL;

public class TrendingArticleActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface {

    @BindView(R.id.rv_trendingarticle)RecyclerView rv_trendingarticle;
    @BindView(R.id.txt_description)TextViewPret txt_description;
    @BindView(R.id.ib_like)ImageView ib_like;
    @BindView(R.id.txt_name)TextViewPret txt_name;

    JsonRequestController jsonRequestController;
    DetailsPageController detailsPageController;

    String mId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_article);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        Utility.setListLayoutManager(rv_trendingarticle, getApplicationContext());

        TrendingItems trendingItems = (TrendingItems)getIntent()
                .getSerializableExtra(Constant.PARCEL_KEY);
        String pagekey = trendingItems.getPagetypeid();
        String clicktype = trendingItems.getClicktype();
        String mId = trendingItems.getId();
        getTrendingArticle(pagekey, clicktype, mId);
    }


    @OnClick(R.id.ib_like)
    public void onLikePressed() {
        JSONObject resultJson = detailsPageController.getTrendinglikeJson(mId ,
                getIntent().getStringExtra(Constant.PRE_PAGE_KEY));
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDINGLIKE_URL);
        /*Integer resource = (Integer) ib_like.getTag();
        if(resource == R.drawable.grey_heart) {
            ib_like.setImageResource(R.drawable.red_heart);
            ib_like.setTag(R.drawable.red_heart);
        } else {
            ib_like.setImageResource(R.drawable.grey_heart);
            ib_like.setTag(R.drawable.grey_heart);
        }*/
    }

    private void getTrendingArticle(String prepage, String clicktype, String trid){
        JSONObject resultJson = detailsPageController.getTrendingArticle(prepage, clicktype, trid);
        this.showProgressDialog(getResources().getString(R.string.loading));
        jsonRequestController.sendRequest(this, resultJson, TRENDINGARTICLE_URL);
    }

    private void setupArticle(ArrayList<TrendingItems> trendingArticle){
        TrendingArticleAdapter adapter = new TrendingArticleAdapter(getApplicationContext(), trendingArticle);
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
                    setupArticle(trendingArticle);
                    this.hideDialog();
                    break;
                case TRENDINGLIKE_URL:
                    this.hideDialog();
                    JSONObject object = response.getJSONObject("Data");
                    ib_like.setTag(object.getInt("LikeStatus") == 1 ? R.drawable.red_heart : R.drawable.grey_heart);
                    ib_like.setImageResource(object.getInt("LikeStatus") == 1 ? R.drawable.red_heart : R.drawable.grey_heart);
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
}

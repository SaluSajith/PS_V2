package com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.navigationitems.FollowingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/27/2017.
 */

public class StoreList_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {

    static int mPosition;
    private Context context;
    private ArrayList<StoreListModel> mItems;
    private ButtonClickCallbackStoreList buttonClickCallback;
    private OnLoadMoreListener mOnLoadMoreListener;

    private final int VIEW_TYPE_LOADING = 3;
    private final int VIEW_TYPE_ITEM_0 = 0;
    private final int VIEW_TYPE_ITEM_1 = 1;
    private final int VIEW_TYPE_ITEM_2 = 2;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private final RequestManager glide;

    public StoreList_RecyclerAdapter(final RequestManager glide, RecyclerView mRecyclerView, Context context,
                                     ArrayList<StoreListModel> storeListModels) {
        this.context = context;
        this.mItems = storeListModels;
        this.glide = glide;
        buttonClickCallback = (ButtonClickCallbackStoreList) context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                try {
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                /*if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    glide.resumeRequests();
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    glide.pauseRequests();
                }*/
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRootView = null;
        switch (viewType) {
            case VIEW_TYPE_ITEM_0:
                mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store, parent, false);
                return new ShopsHolder(mRootView);
            case VIEW_TYPE_ITEM_1:
                mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store1, parent, false);
                return new ShopsHolder(mRootView);
            case VIEW_TYPE_ITEM_2:
                mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store2, parent, false);
                return new ShopsHolder(mRootView);
            default:
                mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store1, parent, false);
                return new ShopsHolder(mRootView);
        }
    }

    @Override
    public int getItemCount() {
        return this.mItems != null ? mItems.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (position % 2 == 0) {
            if (context.getClass().getSimpleName().equals(StoreListingActivity.class.getSimpleName()) ||
                    context.getClass().getSimpleName().equals(FollowingActivity.class.getSimpleName())) {
                if (position == 0)
                    viewType = VIEW_TYPE_ITEM_0;
                else
                    viewType = VIEW_TYPE_ITEM_1;
            } else
                viewType = VIEW_TYPE_ITEM_1;
        } else
            viewType = VIEW_TYPE_ITEM_2;
        //return position == mItems.size() ? VIEW_TYPE_LOADING : viewType;
        return viewType;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ShopsHolder shopsHolder = (ShopsHolder) holder;
        if (shopsHolder.matrix == null) {
            shopsHolder.matrix = shopsHolder.img_store_photo.getImageMatrix();
            shopsHolder.matrix.postScale(2, 2);
        } else ;
        StoreListModel storeListModel = mItems.get(position);
        shopsHolder.txt_storename.setText(storeListModel.getTitle());
        shopsHolder.txt_address.setText(storeListModel.getLocation());

        String strFollowCount = storeListModel.getFollowingCount();
        if (strFollowCount.length() >= 4) {
            String convertedCountK = strFollowCount.substring(0, strFollowCount.length() - 3);
            if (convertedCountK.length() >= 4) {
                String convertedCount = convertedCountK.substring(0, convertedCountK.length() - 3);
                shopsHolder.txt_folleowercount.setText(Html.fromHtml(convertedCount + "<sup>M</sup>"));
            } else shopsHolder.txt_folleowercount.setText(Html.fromHtml(convertedCountK + "<sup>K</sup>"));
        } else shopsHolder.txt_folleowercount.setText(Html.fromHtml(strFollowCount));

        loadImage(glide, storeListModel.getImageSource(), shopsHolder.img_store_photo);

        shopsHolder.img_follow_unfollow.setText(storeListModel.getFollowingStatus() == true ? "Unfollow" : "Follow");
        shopsHolder.tv_closeStatus.setText(storeListModel.getOpenStatus() == false ? "Closed" : "Open");
        setVisibility(shopsHolder.img_sale, storeListModel.getSaleflag());
        setVisibility(shopsHolder.img_offer, storeListModel.getOfferflag());
        setVisibility(shopsHolder.img_newarrival, storeListModel.getNewflag());
        setVisibility(shopsHolder.iv_banner, storeListModel.getBannerFlag());

        if(storeListModel.getBannerFlag()) {
            shopsHolder.iv_banner.setVisibility(View.VISIBLE);
            loadImage(glide, storeListModel.getImageSource(), shopsHolder.iv_banner);
        } else shopsHolder.iv_banner.setVisibility(View.GONE);

    }

    private void setVisibility(View view, boolean b){
        if(b) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateFollowStatus(int status, String storeid) {
        if (mItems.get(mPosition).getId().equals(storeid)) {
            mItems.get(mPosition).setFollowingStatus(status == 1 ? true : false);
            int count = Integer.parseInt(mItems.get(mPosition).getFollowingCount());
            mItems.get(mPosition).setFollowingCount(status == 1 ? (count+1) +"" : (count-1) +"");
        } else ;
    }

    class ShopsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ll_progress) LinearLayout ll_progress;
        @BindView(R.id.img_follow_unfollow) TextViewPret img_follow_unfollow;
        @BindView(R.id.txt_storename) TextViewPret txt_storename;
        @BindView(R.id.txt_address) TextViewPret txt_address;
        @BindView(R.id.tv_margintop) TextViewPret tv_margintop;
        @BindView(R.id.txt_folleowercount) TextViewPret txt_folleowercount;
        @BindView(R.id.tv_closeStatus) TextViewPret tv_closeStatus;
        @BindView(R.id.img_store_photo) ImageView img_store_photo;
        @BindView(R.id.iv_banner) ImageView iv_banner;
        @BindView(R.id.img_sale) ImageButton img_sale;
        @BindView(R.id.img_offer) ImageButton img_offer;
        @BindView(R.id.img_newarrival) ImageButton img_newarrival;

        Matrix matrix;

        ShopsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            img_follow_unfollow.setOnClickListener(this);
            img_store_photo.setOnClickListener(this);
            iv_banner.setOnClickListener(this);
            txt_storename.setOnClickListener(this);
            iv_banner.setOnClickListener(this);
            if (context.getClass().getSimpleName().equals(FollowingActivity.class.getSimpleName()))
                img_follow_unfollow.setVisibility(View.GONE);
            else ;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = getAdapterPosition();
                    if(mPosition<mItems.size()) {
                        StoreListModel storeListModel = mItems.get(mPosition);
                        buttonClickCallback.buttonClick(storeListModel);
                    }else ;
                }
            });
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if(position<=mItems.size()) {
                StoreListModel storeListModel = mItems.get(position);
                mPosition = position;
                switch (id) {
                    case R.id.img_follow_unfollow:
                        buttonClickCallback.updateFollowStatus(storeListModel.getId());
                        break;
                    case R.id.img_store_photo:
                        buttonClickCallback.buttonClick(storeListModel);
                        break;
                    case R.id.iv_banner:
                        buttonClickCallback.buttonClick(storeListModel);
                        break;
                    case R.id.txt_storename:
                        buttonClickCallback.buttonClick(storeListModel);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    /*@Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        StoreList_RecyclerAdapter.ShopsHolder holder1 = (StoreList_RecyclerAdapter.ShopsHolder) holder;
        if(holder != null) {
            Glide.clear(holder1.iv_banner);
            Glide.clear(holder1.img_store_photo);
        }
        super.onViewRecycled(holder);
    }*/

    static void loadImage(RequestManager glide, String url, ImageView view) {
        glide.load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(view);
    }
}
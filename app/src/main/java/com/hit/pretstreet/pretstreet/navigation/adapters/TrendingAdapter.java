package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.CircularImageView;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.navigation.fragments.ExhibitionFragment;
import com.hit.pretstreet.pretstreet.navigation.fragments.TrendingFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.ProductImageItem;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 03/08/2017.
 */

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder>{

    Context context;
    int dotsCount = 0;
    static int mPosition;
    private static ImageView imageViewPret;
    private static ArrayList<TrendingItems> list;
    ArticlePagerAdapter mAdapter;
    private int mLastPosition;
    TrendingHolderInvoke trendingHolderInvoke;
    private ZoomedViewListener zoomedViewListener;

    private static final int TRENDING_FRAGMENT = 10;
    private static final int EXHIBITION_FRAGMENT = 11;

    public TrendingAdapter(Activity activity, TrendingFragment context, ArrayList<TrendingItems> list) {
        this.context = activity;
        this.list = list;
        this.zoomedViewListener = (ZoomedViewListener) context;
        this.trendingHolderInvoke = (TrendingHolderInvoke)activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.row_trending, parent, false);
        return new ViewHolder(mRootView);
    }

    @Override
    public int getItemCount() {
        return this.list != null ? list.size() : 0;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TrendingItems trendingItems = list.get(position);
        holder.txt_date.setText(trendingItems.getArticledate());
        holder.txt_title.setText(trendingItems.getTitle());
        holder.txt_description.setText(trendingItems.getArticle());

        if(trendingItems.getLoadmoreFlag())
            holder.ll_progress.setVisibility(View.VISIBLE);
        else
            holder.ll_progress.setVisibility(View.GONE);

        if(trendingItems.getBanner()){
            holder.iv_banner.setVisibility(View.VISIBLE);
            holder.article_images.setVisibility(View.GONE);
            Glide.with(context)
                    .load(trendingItems.getImagearray().get(0))
                    .fitCenter()
                    .into(holder.iv_banner);
        }
        else {
            switch (trendingItems.getImagearray().size()) {
                case 0:
                    holder.iv_banner.setVisibility(View.VISIBLE);
                    holder.iv_banner.setImageResource(R.mipmap.ic_launcher);
                    break;
                default:
                    holder.iv_banner.setVisibility(View.GONE);
                    mAdapter = new ArticlePagerAdapter(context, trendingItems.getImagearray());
                    holder.article_images.setAdapter(mAdapter);
                    holder.article_images.setCurrentItem(0);
                    if (trendingItems.getImagearray().size() > 1) {
                        setUiPageViewController(holder, position);
                    }
                    break;
            }
        }

        String udata = trendingItems.getStoreName()+"";
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        holder.txt_shopname.setText(content);

        Glide.with(context)
                .load(trendingItems.getLogoImage())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.iv_profile);

        holder.iv_like.setImageResource(trendingItems.getLike() == true ?
                R.drawable.red_heart : R.drawable.grey_heart);
        holder.ll_desc.setVisibility(trendingItems.getBanner() == true ? View.GONE : View.VISIBLE);
    }


    private void setUiPageViewController(ViewHolder holder, int position) {

        dotsCount = list.get(position).getImagearray().size();
        holder.dots = new ImageView[dotsCount];
        holder.pager_indicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            //for (Iterator it = list.iterator(); it.hasNext(); ) {
            holder.dots[i] = new ImageView(context);
            holder.dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            holder.pager_indicator.addView(holder.dots[i], params);
        }
        if(holder.dots.length>0)
            holder.dots[0].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_selected));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.row_trending;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, ViewPager.OnPageChangeListener {

        @BindView(R.id.iv_like)ImageView iv_like;
        @BindView(R.id.iv_share)ImageView iv_share;
        @BindView(R.id.iv_banner)ImageView iv_banner;
        @BindView(R.id.iv_profile)CircularImageView iv_profile;

        @BindView(R.id.txt_date)TextViewPret txt_date;
        @BindView(R.id.txt_title)TextViewPret txt_title;
        @BindView(R.id.txt_shopname)TextViewPret txt_shopname;
        @BindView(R.id.txt_description)TextViewPret txt_description;

        @BindView(R.id.pager_article)ViewPager article_images;
        @BindView(R.id.viewPagerCountDots)LinearLayout pager_indicator;
        @BindView(R.id.ll_desc)LinearLayout ll_desc;
        @BindView(R.id.ll_progress) LinearLayout ll_progress;

        ImageView[] dots;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageViewPret =  new ImageView(context);

            iv_like.setOnClickListener(this);
            iv_share.setOnClickListener(this);
            txt_shopname.setOnClickListener(this);
            iv_banner.setOnClickListener(this);
            txt_description.setOnClickListener(this);
            txt_title.setOnClickListener(this);
            article_images.setOnPageChangeListener(this);
/*
            try {
                float initialTranslation = (mLastPosition <= getAdapterPosition() ? 500f : -500f);
                itemView.setTranslationY(initialTranslation);
                itemView.animate()
                        .setInterpolator(new DecelerateInterpolator(1.0f))
                        .translationY(0f)
                        .setDuration(300l)
                        .setListener(null);
                mLastPosition = getAdapterPosition();
            }catch (Exception e){ }*/
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            TrendingItems trendingItems = list.get(getAdapterPosition());
            switch (viewId) {
                case R.id.iv_like:
                    mPosition = getAdapterPosition();
                    imageViewPret = (ImageView) view;
                    trendingHolderInvoke.likeInvoke(Integer.parseInt(trendingItems.getId()), TRENDING_FRAGMENT);
                    break;
                case R.id.iv_share:
                    trendingHolderInvoke.shareUrl("null");
                    break;
                case R.id.txt_shopname:
                    StoreListModel storeListModel = new StoreListModel();
                    storeListModel.setId(trendingItems.getId());
                    storeListModel.setTitle(trendingItems.getTitle());
                    trendingHolderInvoke.loadStoreDetails(getAdapterPosition(),
                            storeListModel);
                    break;
                case R.id.txt_description:
                    trendingHolderInvoke.openTrendingArticle(trendingItems, Constant.TRENDINGPAGE);
                    break;
                case R.id.txt_title:
                    trendingHolderInvoke.openTrendingArticle(trendingItems, Constant.TRENDINGPAGE);
                    break;
                case R.id.iv_banner:
                    if(trendingItems.getBanner()){
                        trendingHolderInvoke.openTrendingArticle(trendingItems, Constant.TRENDINGPAGE);
                    } else {
                        ProductImageItem productImageItem = new ProductImageItem();
                        ArrayList<String> mImagearray = new ArrayList<>();
                        //productImageItem.setImage(list.get(getAdapterPosition()).getImagearray().get(0));
                        mImagearray.add(trendingItems.getImagearray().get(0));
                        zoomedViewListener.onClicked(0, mImagearray);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            try {
                for (int i = 0; i < list.get(getAdapterPosition()).getImagearray().size(); i++) {
                    dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_unselected));
                }
                dots[position].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_selected));
            }catch (Exception e){}
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public static void updateLikeStatus(int status, String storeid) {
        imageViewPret.setImageResource(status == 1 ?
                R.drawable.red_heart : R.drawable.grey_heart);
        if(list.get(mPosition).getId().equals(storeid))
            list.get(mPosition).setLike(status == 0 ? false : true);
    }

    public void loadMoreView(boolean visibility){
        if(list.size()>1)
            list.get(list.size()-1).setLoadmoreFlag(visibility);
    }
}
package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
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
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.fragments.ExhibitionFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.ProductImageItem;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.ButtonClickCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 04/08/2017.
 */

public class ExhibitionAdapter extends RecyclerView.Adapter<ExhibitionAdapter.ViewHolder>{

    Context context;
    static int mPosition;
    private static ImageView imageViewPret;
    private static ArrayList<TrendingItems> list;
    private TrendingHolderInvoke trendingHolderInvoke;
    private ZoomedViewListener zoomedViewListener;

    private static final int TRENDING_FRAGMENT = 10;
    private static final int EXHIBITION_FRAGMENT = 11;

    public ExhibitionAdapter(Activity activity, ExhibitionFragment context, ArrayList<TrendingItems> list) {
        this.context = activity;
        this.list = list;
        this.zoomedViewListener = (ZoomedViewListener) context;
        this.trendingHolderInvoke = (TrendingHolderInvoke)activity;
    }

    @Override
    public ExhibitionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitionAdapter.ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ExhibitionAdapter.ViewHolder holder, int position) {

        TrendingItems trendingItems = list.get(position);
        holder.txt_date.setText(trendingItems.getArticledate());
        holder.txt_shopname.setText(trendingItems.getTitle());
        holder.txt_description.setText(trendingItems.getArticle());

        try {
            Glide.with(context)
                    .load(trendingItems.getImagearray().get(0))
                    .fitCenter()
                    //.placeholder(R.mipmap.ic_launcher)
                    .into(holder.iv_banner);
        }catch (Exception e){}

        holder.iv_like.setImageResource(trendingItems.getLike() == true ? R.drawable.red_heart : R.drawable.grey_heart);
        holder.ll_desc.setVisibility(trendingItems.getBanner() == true ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return this.list != null ? list.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.row_exhibitiondata;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.iv_like)ImageView iv_like;
        @BindView(R.id.iv_share)ImageView iv_share;
        @BindView(R.id.iv_banner)ImageView iv_banner;

        @BindView(R.id.txt_date)TextViewPret txt_date;
        @BindView(R.id.txt_shopname)TextViewPret txt_shopname;
        @BindView(R.id.txt_description)TextViewPret txt_description;

        @BindView(R.id.ll_desc)LinearLayout ll_desc;

        int viewType;
        private int mLastPosition;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            imageViewPret =  new ImageView(context);
            ButterKnife.bind(this, itemView);

            iv_like.setOnClickListener(this);
            iv_share.setOnClickListener(this);
            txt_shopname.setOnClickListener(this);
            iv_banner.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    TrendingItems trendingItems = list.get(position);
                    ((HomeInnerActivity)(context)).openExhibitionDetails(trendingItems);
                }
            });

            try {
                float initialTranslation = (mLastPosition <= getAdapterPosition() ? 500f : -500f);
                itemView.setTranslationY(initialTranslation);
                itemView.animate()
                        .setInterpolator(new DecelerateInterpolator(1.0f))
                        .translationY(0f)
                        .setDuration(300l)
                        .setListener(null);
                mLastPosition = getAdapterPosition();
            }catch (Exception e){ }
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            TrendingItems trendingItems = list.get(getAdapterPosition());
            switch (viewId) {
                case R.id.iv_like:
                    mPosition = getAdapterPosition();
                    imageViewPret = (ImageView) view;
                    trendingHolderInvoke.likeInvoke(Integer.parseInt(trendingItems.getId()), EXHIBITION_FRAGMENT);
                    break;
                case R.id.iv_share:
                    trendingHolderInvoke.shareUrl("null");
                    //shareTextUrl();
                    break;
                case R.id.txt_shopname:
                    ((HomeInnerActivity)(context)).openExhibitionDetails(trendingItems);
                    break;
                case R.id.iv_banner:
                    if(trendingItems.getBanner()){
                        trendingHolderInvoke.openTrendingArticle(trendingItems, Constant.EXHIBITIONPAGE);
                    }else {
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
    }

    public static void updateLikeStatus(int status, String storeid) {
        imageViewPret.setImageResource(status == 1 ?
                R.drawable.red_heart : R.drawable.grey_heart);
        if(list.get(mPosition).getId().equals(storeid))
            list.get(mPosition).setLike(status == 0 ? false : true);
    }
}
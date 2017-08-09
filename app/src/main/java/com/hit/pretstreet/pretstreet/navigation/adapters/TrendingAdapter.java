package com.hit.pretstreet.pretstreet.navigation.adapters;

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
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
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
    int button01pos = 0;
    ArrayList<TrendingItems> list;
    ArticlePagerAdapter mAdapter;
    private int mLastPosition;
    TrendingHolderInvoke trendingHolderInvoke;

    public TrendingAdapter(Context context, ArrayList<TrendingItems> list) {
        this.context = context;
        this.list = list;
        this.trendingHolderInvoke = (TrendingHolderInvoke)context;
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
        System.out.println("hksbgvkd"+trendingItems.getLike());
        holder.txt_date.setText(trendingItems.getArticledate());
        holder.txt_title.setText(trendingItems.getTitle());
        holder.txt_description.setText(trendingItems.getArticle());

        if(trendingItems.getImagearray().size()==0){
            holder.iv_banner.setVisibility(View.VISIBLE);
            holder.iv_banner.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            holder.iv_banner.setVisibility(View.INVISIBLE);
            mAdapter = new ArticlePagerAdapter(context, trendingItems.getImagearray());
            holder.article_images.setAdapter(mAdapter);
            holder.article_images.setCurrentItem(0);
            if(trendingItems.getImagearray().size()>1){
                setUiPageViewController(holder, position);
            }
        }

        String udata = trendingItems.getStoreName()+"";
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        holder.txt_shopname.setText(content);
        System.out.println("hks  bgvkd"+trendingItems.getLogoImage());

        //if(!trendingItems.getStoreLink().equals("0")){
            Glide.with(context)
                    .load(trendingItems.getLogoImage())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.iv_profile);
        /*//}
        //else{
            //holder.iv_profile.setImageResource(R.drawable.logo1);
            Bitmap bitmap = ((BitmapDrawable)holder.iv_profile.getDrawable()).getBitmap();
            //holder.iv_profile.setImageBitmap(getCircleBitmap(bitmap));
        //}*/

        holder.iv_like.setImageResource(trendingItems.getLike() == true ?
                R.drawable.grey_heart : R.drawable.red_heart);
        holder.ll_desc.setVisibility(trendingItems.getBanner() == true ? View.VISIBLE : View.GONE);
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

        int viewType;
        ImageView[] dots;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            iv_like.setOnClickListener(this);
            iv_share.setOnClickListener(this);
            txt_shopname.setOnClickListener(this);
            iv_banner.setOnClickListener(this);
            txt_description.setOnClickListener(this);
            txt_title.setOnClickListener(this);
            article_images.setOnPageChangeListener(this);

            try {
                float initialTranslation = (mLastPosition <= getAdapterPosition() ? 500f : -500f);
                itemView.setTranslationY(initialTranslation);
                itemView.animate()
                        .setInterpolator(new DecelerateInterpolator(1.0f))
                        .translationY(0f)
                        .setDuration(300l)
                        .setListener(null);
                mLastPosition = getAdapterPosition();
            }catch (Exception e){
                Log.e("Exception", e+"");
            }

        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            TrendingItems trendingItems = list.get(getAdapterPosition());
            switch (viewId) {
                case R.id.iv_like:
                    /*if (button01pos == 0) {
                        img_like.setImageResource(R.drawable.red_heart);
                        button01pos = 1;
                    } else if (button01pos == 1) {
                        img_like.setImageResource(R.drawable.grey_heart);
                        button01pos = 0;
                    }*/
                    //sendButtonStatus(list.get(getAdapterPosition()).getId());

                    trendingHolderInvoke.likeInvoke(Integer.parseInt(trendingItems.getId()));
                    break;
                case R.id.iv_share:
                    trendingHolderInvoke.shareUrl("null");
                    //shareTextUrl();
                    break;
                case R.id.txt_shopname:
                    //if(!list.get(getAdapterPosition()).getStoreLink().equals("0")) {
                        StoreListModel storeListModel = new StoreListModel();
                        storeListModel.setId(trendingItems.getId());
                        storeListModel.setTitle(trendingItems.getTitle());
                        trendingHolderInvoke.loadStoreDetails(getAdapterPosition(),
                                storeListModel);
                    //}
                        //openStoreDetails();
                    break;
                case R.id.txt_description:
                    trendingHolderInvoke.openTrendingArticle(trendingItems);
                    break;
                case R.id.txt_title:
                    trendingHolderInvoke.openTrendingArticle(trendingItems);
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
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_unselected));
            }
            dots[position].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_selected));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }
}
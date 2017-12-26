package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.TrendingArticleActivity;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 07/08/2017.
 */

public class TrendingArticleAdapter extends RecyclerView.Adapter<TrendingArticleAdapter.ArticleViewHolder>{

    private Context context;
    ArrayList<TrendingItems> artItems;
    TrendingCallback trendingCallback;
    private ZoomedViewListener zoomedViewListener;

    public TrendingArticleAdapter(Context context, ArrayList<TrendingItems> artItems) {
        this.context = context;
        this.artItems = artItems;
        try {
            if(context.getClass().getSimpleName().equals(TrendingArticleActivity.class.getSimpleName()))
                this.zoomedViewListener = ((TrendingArticleActivity) context);
            else if(context.getClass().getSimpleName().equals(NavigationItemsActivity.class.getSimpleName()))
                this.zoomedViewListener = ((NavigationItemsActivity) context);
            else this.zoomedViewListener = (ZoomedViewListener) context.getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.artItems != null ? artItems.size() : 0;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_trending_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {

        TrendingItems trendingItems = artItems.get(position);
        holder.txt_title.setVisibility(trendingItems.getPagetypeid().trim().length()>0 ? View.VISIBLE : View.GONE);
        setViewText(holder.txt_shopname, trendingItems.getTitle());
        setViewText(holder.txt_description, trendingItems.getArticle());

        Glide.with(context)
                .load(trendingItems.getLogoImage())
                .fitCenter()
                //.placeholder(R.mipmap.ic_launcher)
                .into(holder.iv_banner);
    }

    private void setViewText(TextView textView, String text) {
        if(text.length()==0)
            textView.setVisibility(View.GONE);
        else
            textView.setText(text);
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_banner)ImageView iv_banner;
        @BindView(R.id.txt_shopname)TextViewPret txt_shopname;
        @BindView(R.id.txt_title)TextViewPret txt_title;
        @BindView(R.id.txt_description)TextViewPret txt_description;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            iv_banner.setOnClickListener(this);
            txt_title.setOnClickListener(this);
            txt_shopname.setOnClickListener(this);
            txt_description.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrendingItems notifItems = artItems.get(getAdapterPosition());
                    if(notifItems.isNotifPage()){
                        trendingCallback = (NavigationItemsActivity)context;
                        ArrayList<TrendingItems> trendingItemses = new ArrayList<>();
                        trendingItemses.add(notifItems);
                        trendingCallback.bindData(trendingItemses);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            TrendingItems notifItems = artItems.get(getAdapterPosition());
            switch (viewId){
                case R.id.txt_title:
                    if(!notifItems.isNotifPage()) {
                        trendingCallback = (TrendingArticleActivity) context;
                        ArrayList<TrendingItems> trendingItemses = new ArrayList<>();
                        trendingItemses.add(notifItems);
                        trendingCallback.bindData(trendingItemses);
                    }
                    else{
                        trendingCallback = (NavigationItemsActivity)context;
                        ArrayList<TrendingItems> trendingItemses = new ArrayList<>();
                        trendingItemses.add(notifItems);
                        trendingCallback.bindData(trendingItemses);
                    }
                    break;
                case R.id.iv_banner:
                    if(!notifItems.isNotifPage()) {
                        ArrayList<String> mImagearray = new ArrayList<>();
                        mImagearray.add(artItems.get(getAdapterPosition()).getLogoImage());
                        zoomedViewListener.onClicked(0, mImagearray);
                    }
                    else{
                        trendingCallback = (NavigationItemsActivity)context;
                        ArrayList<TrendingItems> trendingItemses = new ArrayList<>();
                        trendingItemses.add(notifItems);
                        trendingCallback.bindData(trendingItemses);
                    }
                    break;
                case R.id.txt_description:
                    if(notifItems.isNotifPage()) {
                        trendingCallback = (NavigationItemsActivity)context;
                        ArrayList<TrendingItems> trendingItemses = new ArrayList<>();
                        trendingItemses.add(notifItems);
                        trendingCallback.bindData(trendingItemses);
                    }
                    break;
                case R.id.txt_shopname:
                    if(notifItems.isNotifPage()) {
                        trendingCallback = (NavigationItemsActivity)context;
                        ArrayList<TrendingItems> trendingItemses = new ArrayList<>();
                        trendingItemses.add(notifItems);
                        trendingCallback.bindData(trendingItemses);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

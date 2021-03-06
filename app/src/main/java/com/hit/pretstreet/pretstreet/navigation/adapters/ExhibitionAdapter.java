package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITION_FRAGMENT;

/**
 * Created by User on 04/08/2017.
 * Adapter class for exhibition listing page
 */

public class ExhibitionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static int mPosition;
    private static Context context;
    private TextViewPret textViewPret;
    private ArrayList<TrendingItems> list;
    private TrendingHolderInvoke trendingHolderInvoke;

    private static final int LIKE = 22;
    private static int selected_id = 22;
    private static final int ReGISTER = 21;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private final RequestManager glide;
    private OnLoadMoreListener mOnLoadMoreListener;

    /** Public constructor for exhibition listing
     * @param glide common glide object
     * @param activity activity contecxt
     * @param list exhibition list model array
     * @param mRecyclerView recyclerview param for loadmore*/
    public ExhibitionAdapter(final RequestManager glide, RecyclerView mRecyclerView,
                             Activity activity, ArrayList<TrendingItems> list) {
        context = activity;
        this.list = list;
        this.glide = glide;
        this.trendingHolderInvoke = (TrendingHolderInvoke)activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        /**Load more items listener*/
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) { //reached 5th last item
                    if (mOnLoadMoreListener != null) {
                        isLoading = true;
                        mOnLoadMoreListener.onLoadMore(); //call loadmore api with the next offset
                    }
                }
            }
            /*@Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    glide.resumeRequests();
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    glide.pauseRequests();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }*/
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public ExhibitionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitionAdapter.ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        ViewHolder holder = (ViewHolder) holder1;
        TrendingItems trendingItems = list.get(position);
        setViewText(holder.txt_date, trendingItems.getArticledate());
        setViewText(holder.txt_shopname, trendingItems.getTitle());
        setViewText(holder.txt_description, ""); // we are not displaying description right now
        //setViewText(holder.txt_description, trendingItems.getArticle());
        setViewText(holder.txt_location, trendingItems.getArea());

        try {
            loadImage(glide, trendingItems.getImagearray().get(0), holder.iv_banner);
        }catch (Exception e){}

        holder.iv_like.setImageResource(trendingItems.getLike() == true ? R.drawable.red_heart : R.drawable.grey_heart);
        holder.ll_desc.setVisibility(trendingItems.getBanner() == true ? View.GONE : View.VISIBLE);

        /**Register button STATUS flag
         * @value 0 - not registered yet
         * @value 1 - Registered for the particular exhibition
         * @value 2 - past exhibitions*/
        switch (trendingItems.getRegisterFlag()){
            case "0":
                holder.bt_register.setEnabled(true);
                holder.bt_register.setText("Register");
                holder.bt_register.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                holder.bt_register.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                holder.bt_register.setVisibility(View.VISIBLE);
                break;
            case "1":
                holder.bt_register.setEnabled(false);
                holder.bt_register.setText("Registered");
                holder.bt_register.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.bt_register.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
                holder.bt_register.setVisibility(View.VISIBLE);
                break;
            case "2":
                holder.bt_register.setEnabled(false);
                holder.bt_register.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        /**Reached last item in the list*/
        if(position == list.size()-1){
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.reachedLastItem();
            }
        }
    }

    private void setViewText(TextView textView, String text) {
        textView.setText(text);
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

        int viewType;
        @BindView(R.id.iv_like)AppCompatImageView iv_like;
        @BindView(R.id.iv_share)AppCompatImageView iv_share;
        @BindView(R.id.iv_banner)AppCompatImageView iv_banner;

        @BindView(R.id.txt_date)TextViewPret txt_date;
        @BindView(R.id.txt_shopname)TextViewPret txt_shopname;
        @BindView(R.id.txt_location)TextViewPret txt_location;
        @BindView(R.id.txt_description)TextViewPret txt_description;
        @BindView(R.id.bt_register)TextViewPret bt_register;

        @BindView(R.id.ll_desc)LinearLayout ll_desc;
        @BindView(R.id.ll_progress) LinearLayout ll_progress;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            ButterKnife.bind(this, itemView);

            iv_like.setOnClickListener(this);
            iv_share.setOnClickListener(this);
            bt_register.setOnClickListener(this);
            txt_shopname.setOnClickListener(this);
            txt_description.setOnClickListener(this);
            iv_banner.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mPosition = getAdapterPosition();
                    TrendingItems trendingItems = list.get(position);
                    ((HomeInnerActivity)(context)).openExhibitionDetails(trendingItems);
                }
            });
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            TrendingItems trendingItems = list.get(getAdapterPosition());
            switch (viewId) {
                case R.id.iv_like:
                    mPosition = getAdapterPosition();
                    selected_id = LIKE;
                    /**Calling like api*/
                    trendingHolderInvoke.likeInvoke(Integer.parseInt(trendingItems.getId()), EXHIBITION_FRAGMENT);
                    break;
                case R.id.iv_share:
                    /**Share page with friends*/
                    trendingHolderInvoke.shareurl(trendingItems.getShareUrl());
                    break;
                case R.id.bt_register:
                    /**Register for exhibition*/
                    mPosition = getAdapterPosition();
                    selected_id = ReGISTER;
                    textViewPret = (TextViewPret) view;
                    trendingHolderInvoke.registerInvoke(Integer.parseInt(trendingItems.getId()));
                    break;
                case R.id.txt_shopname:
                    /**Open exhibition details*/
                    mPosition = getAdapterPosition();
                    ((HomeInnerActivity)(context)).openExhibitionDetails(trendingItems);
                    break;
                case R.id.txt_description:
                    /**Open exhibition details*/
                    mPosition = getAdapterPosition();
                    ((HomeInnerActivity)(context)).openExhibitionDetails(trendingItems);
                    break;
                case R.id.iv_banner:
                    if(trendingItems.getBanner()){
                        /**If it is a clickable banner only
                         * Row will be replaced by a banner image*/
                        trendingHolderInvoke.openTrendingArticle(trendingItems, Constant.EXHIBITIONPAGE);
                    }else {
                        mPosition = getAdapterPosition();
                        ((HomeInnerActivity)(context)).openExhibitionDetails(trendingItems);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**Update Button status in list as per the response status*/
    public void updateLikeStatus(int status, String storeid) {
        switch (selected_id){
            case ReGISTER:
                /**Update Register Button Text*/
                if(status==1){
                    textViewPret.setEnabled(false);
                    textViewPret.setText("Registered");
                    textViewPret.setTextColor(ContextCompat.getColor(context, R.color.white));
                    textViewPret.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
                }
                if(list.get(mPosition).getId().equals(storeid))
                    list.get(mPosition).setRegister(status+"");
                break;
            case LIKE:
                /**Update LIKE or HEART button text*/
                if(list.get(mPosition).getId().equals(storeid))
                    list.get(mPosition).setLike(status == 0 ? false : true);
                break;
            default:
                break;
        }
    }

    /*@Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ExhibitionAdapter.ViewHolder holder1 = (ExhibitionAdapter.ViewHolder) holder;
        if(holder != null) {
            Glide.clear(holder1.iv_banner);
        }
        super.onViewRecycled(holder);
    }*/

    public void setLoaded() {
        isLoading = false;
    }

    /**Download image from URL
     * Removed caching strategy as it is affecting if the image is replaced with another*/
    static void loadImage(RequestManager glide, String url, AppCompatImageView view) {
        //glide.load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);
        glide.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(view);
    }
}
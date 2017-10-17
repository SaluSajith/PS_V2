package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.fragments.TrendingFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingHolderInvoke;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGPAGE;

/**
 * Created by User on 03/08/2017.
 */

public class TrendingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context, fragContext;
    private int dotsCount = 0;
    static int mPosition;
    private ArrayList<TrendingItems> list;
    ArticlePagerAdapter mAdapter;
    TrendingHolderInvoke trendingHolderInvoke;
    private ZoomedViewListener zoomedViewListener;

    private static final int TRENDING_FRAGMENT = 10;
    private static final int EXHIBITION_FRAGMENT = 11;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private final RequestManager glide;

    public TrendingAdapter(final RequestManager glide, RecyclerView mRecyclerView,
                           Activity activity, TrendingFragment context, ArrayList<TrendingItems> list) {
        this.context = activity;
        this.list = list;
        this.glide = glide;
        this.zoomedViewListener = ((HomeInnerActivity) activity);
        this.trendingHolderInvoke = (TrendingHolderInvoke)activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.row_trending, parent, false);
        return new ViewHolder(mRootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        ViewHolder holder = (ViewHolder) holder1;

        TrendingItems trendingItems = list.get(position);
        setViewText(holder.txt_date, trendingItems.getArticledate());
        //setViewText(holder.txt_title, trendingItems.getTitle());
        holder.txt_title.setVisibility(trendingItems.getTitleid().trim().length()>0 ? View.VISIBLE : View.GONE);
        setViewText(holder.txt_description, trendingItems.getArticle());

        if(trendingItems.getBanner()){
            holder.iv_banner.setVisibility(View.VISIBLE);
            holder.article_images.setVisibility(View.GONE);
            loadImage(glide, trendingItems.getImagearray().get(0), holder.iv_banner);
        }
        else {
            switch (trendingItems.getImagearray().size()) {
                case 0:
                    holder.pager_indicator.setVisibility(View.INVISIBLE);
                    holder.iv_banner.setVisibility(View.VISIBLE);
                    holder.article_images.setVisibility(View.GONE);
                    holder.iv_banner.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 1:
                    loadImage(glide, trendingItems.getImagearray().get(0), holder.iv_banner);
                    holder.pager_indicator.setVisibility(View.INVISIBLE);
                    holder.iv_banner.setVisibility(View.VISIBLE);
                    holder.article_images.setVisibility(View.GONE);
                    break;
                default:
                    holder.pager_indicator.setVisibility(View.VISIBLE);
                    holder.iv_banner.setVisibility(View.GONE);
                    holder.article_images.setVisibility(View.VISIBLE);
                    mAdapter = new ArticlePagerAdapter(glide, context, trendingItems.getImagearray());
                    holder.article_images.setAdapter(mAdapter);
                    holder.article_images.setCurrentItem(0);
                    if (trendingItems.getImagearray().size() > 1) {
                        setUiPageViewController(holder, position);
                    }
                    break;
            }
        }
        String udata = trendingItems.getTitle()+"";
        holder.txt_shopname.setText(udata);
        loadImage(glide, trendingItems.getLogoImage(), holder.iv_profile);
        holder.iv_like.setImageResource(trendingItems.getLike() == true ?
                R.drawable.red_heart : R.drawable.grey_heart);
        holder.ll_desc.setVisibility(trendingItems.getBanner() == true ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return this.list != null ? list.size() : 0;
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

    private void setViewText(TextView textView, String text) {
        textView.setText(text);
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
        @BindView(R.id.iv_profile)ImageView iv_profile;

        @BindView(R.id.txt_date)TextViewPret txt_date;
        @BindView(R.id.txt_title)TextViewPret txt_title;
        @BindView(R.id.txt_shopname)TextViewPret txt_shopname;
        @BindView(R.id.txt_description)TextViewPret txt_description;

        @BindView(R.id.ll_desc)LinearLayout ll_desc;
        @BindView(R.id.ll_progress) LinearLayout ll_progress;
        @BindView(R.id.pager_article)ViewPager article_images;
        @BindView(R.id.viewPagerCountDots)LinearLayout pager_indicator;

        private ImageView[] dots;

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
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            TrendingItems trendingItems = list.get(getAdapterPosition());
            switch (viewId) {
                case R.id.iv_like:
                    mPosition = getAdapterPosition();
                    trendingHolderInvoke.likeInvoke(Integer.parseInt(trendingItems.getId()), TRENDING_FRAGMENT);
                    break;
                case R.id.iv_share:
                    trendingHolderInvoke.shareurl(trendingItems.getShareUrl());
                    break;
                case R.id.txt_shopname:
                    StoreListModel storeListModel = new StoreListModel();
                    storeListModel.setId(trendingItems.getTitleid());
                    storeListModel.setTitle(trendingItems.getTitle());
                    storeListModel.setPageTypeId(trendingItems.getTitlepagetype());
                    trendingHolderInvoke.loadStoreDetails(getAdapterPosition(),
                            storeListModel);
                    break;
                case R.id.txt_description:
                    mPosition = getAdapterPosition();
                    trendingHolderInvoke.openTrendingArticle(trendingItems, TRENDINGPAGE);
                    break;
                case R.id.txt_title:
                    storeListModel = new StoreListModel();
                    storeListModel.setId(trendingItems.getTitleid());
                    storeListModel.setTitle(trendingItems.getTitle());
                    storeListModel.setPageTypeId(trendingItems.getTitlepagetype());
                    trendingHolderInvoke.loadStoreDetails(getAdapterPosition(),
                            storeListModel);
                    break;
                case R.id.iv_banner:
                    if(trendingItems.getBanner()){
                        mPosition = getAdapterPosition();
                        trendingHolderInvoke.openTrendingArticle(trendingItems, TRENDINGPAGE);
                    } else {
                        ArrayList<String> mImagearray = new ArrayList<>();
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
            //article_images.reMeasureCurrentPage(article_images.getCurrentItem());
            if(list.get(getAdapterPosition()).getImagearray().size()>1)
                try {
                    for (int i = 0; i < list.get(getAdapterPosition()).getImagearray().size(); i++) {
                        dots[i].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_unselected));
                    }
                    dots[position].setImageDrawable(context.getResources().getDrawable(R.drawable.image_indicator_selected));
                }
                catch (IllegalStateException e){}
                catch (Exception e){}
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public void updateLikeStatus(int status, String storeid) {
        if(list.get(mPosition).getId().equals(storeid))
            list.get(mPosition).setLike(status == 0 ? false : true);
    }

    public void setLoaded() {
        isLoading = false;
    }

    static void loadImage(RequestManager glide, String url, ImageView view) {
        glide.load(url).into(view);
    }
}
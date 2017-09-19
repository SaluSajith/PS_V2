package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.PageState;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 03/08/2017.
 */

public class TrendingFragment extends AbstractBaseFragment<WelcomeActivity>
        implements TrendingCallback {

    @BindView(R.id.rv_trending) RecyclerView rv_trending;
    ArrayList<TrendingItems> trendingItems;
    TrendingAdapter adapter;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_trending, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        Utility.setListLayoutManager(rv_trending, getActivity());
        trendingItems = new ArrayList<>();
        adapter = new TrendingAdapter(getActivity(), TrendingFragment.this, this.trendingItems);
        rv_trending.setAdapter(adapter);
        ((HomeInnerActivity)getActivity()).getTrendinglist(1);//todo
    }

    @Override
    public void bindData(ArrayList<TrendingItems> trendingItems) {
        this.trendingItems.addAll(trendingItems);
        adapter.notifyDataSetChanged();
        //adapter.setHasStableIds(true);
    }

    public void updateLikeStatus(int status, String storeid) {
        adapter.updateLikeStatus(status, storeid);
    }

    public void updateRegisterStatus(int status, String storeid) {
        adapter.updateLikeStatus(status, storeid);
    }

    public void update_loadmore_adapter(boolean b){
        adapter.loadMoreView(b);
    }
}
package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.ExhibitionAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 04/08/2017.
 */

public class ExhibitionFragment extends  AbstractBaseFragment<HomeInnerActivity>
        implements TrendingCallback, ZoomedViewListener {

    @BindView(R.id.rv_trending) RecyclerView rv_trending;
    @BindView(R.id.ll_empty) View ll_empty;
    @BindView(R.id.tv_msg) TextViewPret tv_msg;
    ExhibitionAdapter adapter;
    ArrayList<TrendingItems> exHItems;

    int pageCount = 1;
    boolean loadmore = true;
    boolean requestCalled = false;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        exHItems = new ArrayList<>();
        Utility.setListLayoutManager_(rv_trending, getActivity());
        adapter = new ExhibitionAdapter(Glide.with(this), rv_trending, getActivity(), exHItems);
        rv_trending.setAdapter(adapter);
        ((HomeInnerActivity)getActivity()).getExhibitionlist(pageCount);
        refreshListviewOnScrolling();
    }

    private void refreshListviewOnScrolling(){
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(loadmore) {
                    pageCount++;
                    requestCalled = true;
                    ((HomeInnerActivity)getActivity()).getExhibitionlist(pageCount);
                }
                else
                    adapter.setLoaded();
            }

            @Override
            public void reachedLastItem() {
                if(!loadmore) {
                    ((HomeInnerActivity)getActivity()).displaySnackBar("No more data available!");
                }
            }
        });
    }

    @Override
    public void bindData(ArrayList<TrendingItems> exHItems) {
        this.exHItems.addAll(exHItems);
        adapter.notifyDataSetChanged();
        if(exHItems.size()==0)
            loadmore = false;
        else
            loadmore = true;
        adapter.setLoaded();
        if(this.exHItems.size()==0) {
            tv_msg.setText("Please check your internet connection and try again!");
            ll_empty.setVisibility(View.VISIBLE);
        }
        else ll_empty.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClicked(int position, ArrayList<String> mImagearray) {

        ArrayList<String> imageModels1 = mImagearray;
        Intent intent = new Intent(getActivity(), FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.HOMEPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);

    }

    public void updateLikeStatus(int status, String storeid) {
        adapter.updateLikeStatus(status, storeid);
        adapter.notifyDataSetChanged();
    }
}
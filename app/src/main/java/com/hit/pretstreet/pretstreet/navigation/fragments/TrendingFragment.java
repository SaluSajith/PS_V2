package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.DividerDecoration;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 03/08/2017.
 */

public class TrendingFragment extends AbstractBaseFragment<WelcomeActivity> implements TrendingCallback {

    @BindView(R.id.rv_trending) RecyclerView rv_trending;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        Utility.setListLayoutManager(rv_trending, getActivity());
        ((HomeInnerActivity)getActivity()).getTrendinglist("");

    }

    @Override
    public void bindData(ArrayList<TrendingItems> trendingItems) {
        TrendingAdapter adapter = new TrendingAdapter(getActivity(), trendingItems);
        rv_trending.setAdapter(adapter);
    }
}
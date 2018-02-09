package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.GiveawayAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 28/11/2017.
 */

public class GiveawayFragment extends AbstractBaseFragment<WelcomeActivity>
        implements TrendingCallback {

    @BindView(R.id.rv_trending) RecyclerView rv_giveaway;
    @BindView(R.id.ll_empty) View ll_empty;
    @BindView(R.id.tv_msg) TextViewPret tv_msg;
    ArrayList<TrendingItems> giveawayItems;
    GiveawayAdapter adapter;

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
        Utility.setListLayoutManager_(rv_giveaway, getActivity());
        giveawayItems = new ArrayList<>();
        adapter = new GiveawayAdapter(Glide.with(this), rv_giveaway, getActivity(), this.giveawayItems);
        rv_giveaway.setAdapter(adapter);
        ((HomeInnerActivity)getActivity()).getGiveawaylist(pageCount);
        refreshListviewOnScrolling();
    }

    private void refreshListviewOnScrolling(){
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(loadmore) {
                    pageCount++;
                    requestCalled = true;
                    ((HomeInnerActivity)getActivity()).getGiveawaylist(pageCount);
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
    public void bindData(ArrayList<TrendingItems> trendingItems) {
        this.giveawayItems.addAll(trendingItems);
        adapter.notifyDataSetChanged();
        //adapter.setHasStableIds(true);
        if(trendingItems.size()==0)
            loadmore = false;
        else
            loadmore = true;
        adapter.setLoaded();
        if(this.giveawayItems.size()==0) {
            tv_msg.setText("Please check your internet connection and try again!");
            ll_empty.setVisibility(View.VISIBLE);
        }
        else ll_empty.setVisibility(View.INVISIBLE);
    }

    public void updateLikeStatus(int status, String storeid) {
        adapter.updateLikeStatus(status, storeid);
        adapter.notifyDataSetChanged();
    }
}
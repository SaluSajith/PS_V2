package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingArticleAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 16/08/2017.
 */

public class NotificationFragment extends AbstractBaseFragment<NavigationItemsActivity> implements TrendingCallback {
    @BindView(R.id.rv_trending)
    RecyclerView rv_trending;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        Utility.setListLayoutManager(rv_trending, getActivity());
        /*rv_trending.addItemDecoration(new DividerDecoration(getActivity(),
                ContextCompat.getColor(getActivity(), R.color.trending_grey), 5.0f));*/
        ((NavigationItemsActivity)getActivity()).getNotificationlist("", "", "");//TODO
    }

    @Override
    public void bindData(ArrayList<TrendingItems> trendingItems) {
        TrendingArticleAdapter adapter = new TrendingArticleAdapter(getActivity(), trendingItems);
        rv_trending.setAdapter(adapter);
    }
}
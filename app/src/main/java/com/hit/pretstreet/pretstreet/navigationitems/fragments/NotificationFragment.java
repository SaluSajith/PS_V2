package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingArticleAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 16/08/2017.
 */

public class NotificationFragment extends AbstractBaseFragment<NavigationItemsActivity> implements TrendingCallback {
    @BindView(R.id.rv_trending)
    RecyclerView rv_trending;
    @BindView(R.id.ll_empty) View ll_empty;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notif, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        Log.d("FCM notif fragment", ""+PreferenceServices.getInstance().getNotifCOunt());
        PreferenceServices.getInstance().updateNotif(0);
        Utility.setListLayoutManager(rv_trending, getActivity());
        rv_trending.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        ((NavigationItemsActivity)getActivity()).getNotificationlist();
    }

    @Override
    public void bindData(ArrayList<TrendingItems> trendingItems, String msg) {
            TrendingArticleAdapter adapter = new TrendingArticleAdapter(getActivity(), trendingItems, getHostActivity());
            rv_trending.setAdapter(adapter);
            if (trendingItems.size() == 0)
                ll_empty.setVisibility(View.VISIBLE);
            else ll_empty.setVisibility(View.INVISIBLE);
    }
}
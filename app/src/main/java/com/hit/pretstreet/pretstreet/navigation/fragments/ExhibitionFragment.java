package com.hit.pretstreet.pretstreet.navigation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeInnerActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.ExhibitionAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.TrendingCallback;
import com.hit.pretstreet.pretstreet.navigation.interfaces.ZoomedViewListener;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.storedetails.FullscreenGalleryActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 04/08/2017.
 */

public class ExhibitionFragment extends  AbstractBaseFragment<HomeInnerActivity>
        implements TrendingCallback, ZoomedViewListener {

    @BindView(R.id.rv_trending) RecyclerView rv_trending;
    ExhibitionAdapter adapter;

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
        ((HomeInnerActivity)getActivity()).getExhibitionlist("1");

    }

    @Override
    public void bindData(ArrayList<TrendingItems> exHItems) {
        adapter = new ExhibitionAdapter(getActivity(),ExhibitionFragment.this, exHItems);
        rv_trending.setAdapter(adapter);
        /*TrendingArticleAdapter mAdapter = new TrendingArticleAdapter(getActivity(), exHItems);
        rv_trending.setAdapter(mAdapter);*/
    }

    @Override
    public void onClicked(int position, ArrayList<String> mImagearray) {
        /*Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constant.PARCEL_KEY, mImagearray);
        bundle.putInt(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.HOMEPAGE));
        bundle.putInt(Constant.POSITION_KEY, position);

        Fragment fragment = new SlideshowDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();*/

        ArrayList<String> imageModels1 = mImagearray;
        Intent intent = new Intent(getActivity(), FullscreenGalleryActivity.class);
        intent.putExtra(Constant.PARCEL_KEY, imageModels1);
        intent.putExtra(Constant.PRE_PAGE_KEY, Integer.parseInt(Constant.HOMEPAGE));
        intent.putExtra(Constant.POSITION_KEY, position);
        startActivity(intent);

    }

    public void updateLikeStatus(int status, String storeid) {
        adapter.updateLikeStatus(status, storeid);
    }
}
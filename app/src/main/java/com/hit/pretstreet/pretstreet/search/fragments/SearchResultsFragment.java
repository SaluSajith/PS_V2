package com.hit.pretstreet.pretstreet.search.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.search.interfaces.SearchDataCallback;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.FilterActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.OnLoadMoreListener;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.FILTERPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;

/**
 * Created by User on 16/08/2017.
 */

public class SearchResultsFragment extends AbstractBaseFragment<WelcomeActivity> implements SearchDataCallback {

    @BindView(R.id.rv_Search) RecyclerView rv_Search;
    @BindView(R.id.ll_empty) View ll_empty;

    private StoreList_RecyclerAdapter storeList_recyclerAdapter;
    private ArrayList<StoreListModel> storeListModels;

    private int pageCount=1;
    private boolean requestCalled = false;
    private static boolean loadmore = true, first = true;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        storeList_recyclerAdapter = null;
        storeListModels.clear();
        pageCount=1;
        requestCalled = false;
        loadmore = true;
        first = true;
    }

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchresults, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        storeListModels = new ArrayList<>();
        Utility.setListLayoutManager(rv_Search, getActivity());
        storeList_recyclerAdapter = new StoreList_RecyclerAdapter(Glide.with(this), rv_Search, getActivity(), storeListModels);
        rv_Search.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        refreshListviewOnScrolling();
        ((SearchActivity)getActivity()).getSearchResult(pageCount, first);
    }

    @OnClick(R.id.iv_filter)
    public void onFilterPress(){
        openFilterPage();
    }

    public void refreshSearchResult(){
        ((SearchActivity)getActivity()).getSearchResult(pageCount, first);
    }

    private void openFilterPage(){
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PRE_PAGE_KEY, Constant.SEARCHPAGE);
        bundle.putString(ID_KEY, getArguments().getString(ID_KEY));
        bundle.putSerializable(PARCEL_KEY, getArguments().getSerializable(PARCEL_KEY));
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, Integer.parseInt(FILTERPAGE));
    }

    @OnClick(R.id.imageButton)
    public void onBackPress(){
        getActivity().onBackPressed();
    }

    private void refreshListviewOnScrolling(){
        storeList_recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(!requestCalled){
                    requestCalled = true;
                    first = false;
                    if(loadmore) {
                        ((SearchActivity)getActivity()).getSearchResult(pageCount, first);
                    }}
                if(!loadmore)
                    ((SearchActivity) getActivity()).displaySnackBar("No more stores available!");
            }
        });
    }


    private void setAdapter(){
        if(storeListModels.size()==0)
            ll_empty.setVisibility(View.VISIBLE);
        else {
            ll_empty.setVisibility(View.GONE);
            if (first) {
                ((SearchActivity) getActivity()).setAdapter(storeList_recyclerAdapter);
                rv_Search.setAdapter(storeList_recyclerAdapter);
            } else storeList_recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setRecentsearchList(ArrayList<SearchModel> recentViewModels,
                                    ArrayList<SearchModel> recentSearchModels,
                                    ArrayList<SearchModel> catModels) {
    }
    @Override
    public void setAutosearchList(ArrayList<SearchModel> searchModels) {
    }

    @Override
    public void setSearchList(ArrayList<StoreListModel> searchModels, boolean loadmore) {
        this.loadmore = loadmore;
        requestCalled = false;
        pageCount++;
        //storeListModels.clear();
        storeListModels.addAll(searchModels);
        setAdapter();
        storeList_recyclerAdapter.setLoaded();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("", "");
    }
}
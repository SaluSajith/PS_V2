package com.hit.pretstreet.pretstreet.search.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.customview.SimpleDividerItemDecoration;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.search.MultistoreActivity;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.search.adapters.AutoSearchAdapter;
import com.hit.pretstreet.pretstreet.search.controllers.SearchController;
import com.hit.pretstreet.pretstreet.search.interfaces.SearchDataCallback;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.StoreList_RecyclerAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 16/08/2017.
 */

public class SearchResultsFragment extends AbstractBaseFragment<WelcomeActivity> implements SearchDataCallback {

    @BindView(R.id.rv_Search) RecyclerView rv_Search;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchresults, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        Utility.setListLayoutManager(rv_Search, getActivity());
        rv_Search.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    }

    private void setAdapter(ArrayList<StoreListModel> storeListModels){
        StoreList_RecyclerAdapter storeList_recyclerAdapter = new StoreList_RecyclerAdapter(getActivity(), storeListModels);
        ((SearchActivity)getActivity()).setAdapter(storeList_recyclerAdapter);
        rv_Search.setAdapter(storeList_recyclerAdapter);
    }
    @Override
    public void setAutosearchList(ArrayList<SearchModel> searchModels) {
    }

    @Override
    public void setSearchList(ArrayList<StoreListModel> searchModels) {
        setAdapter(searchModels);
    }
}
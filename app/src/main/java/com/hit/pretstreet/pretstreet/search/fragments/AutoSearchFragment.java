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
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.search.SearchActivity;
import com.hit.pretstreet.pretstreet.search.adapters.AutoSearchAdapter;
import com.hit.pretstreet.pretstreet.search.interfaces.SearchDataCallback;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 14/08/2017.
 */

public class AutoSearchFragment extends AbstractBaseFragment<WelcomeActivity> implements SearchDataCallback{

    @BindView(R.id.sp_Type) Spinner sp_Type;
    @BindView(R.id.sp_CatType) Spinner sp_CatType;
    @BindView(R.id.edt_search) EdittextPret edt_search;
    @BindView(R.id.tv_heading) TextViewPret tv_heading;
    @BindView(R.id.rv_Search) RecyclerView rv_Search;

    private String mStrSearch;
    ArrayList<SearchModel> mSearchModels;
    private AutoSearchAdapter searchAdapter;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_autosearch, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        setupTypeSpinner();
        setupCatTypeSpinner();
        mSearchModels = new ArrayList<>();
        Utility.setListLayoutManager(rv_Search, getActivity());

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //TODO change id
                    tv_heading.setVisibility(View.GONE);
                    ((SearchActivity)getActivity()).getSearchResult(mStrSearch, "5");
                    return true;
                }
                return false;
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStrSearch = s.toString();
                //TODO change id
                ((SearchActivity)getActivity()).getAutoSearch(mStrSearch, "5");
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setupCatTypeSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("All"); //TODO
        list.add("Designs");
        list.add("Jewellery");
        list.add("Brands");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.row_text, list);
        dataAdapter.setDropDownViewResource(R.layout.row_text);
        sp_CatType.setAdapter(dataAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onBackPress(){
        getActivity().onBackPressed();
    }

    private void setupTypeSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("Products");
        list.add("Stores");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.row_text, list);
        dataAdapter.setDropDownViewResource(R.layout.row_text);
        sp_Type.setAdapter(dataAdapter);
    }

    @Override
    public void setAutosearchList(ArrayList<SearchModel> searchModels) {
        if(mSearchModels.size()==0) {
            mSearchModels.clear();
            mSearchModels.addAll(searchModels);
            searchAdapter = new AutoSearchAdapter(getActivity(), searchModels);
            rv_Search.setAdapter(searchAdapter);
        }
        else searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSearchList(ArrayList<StoreListModel> searchModels) {

    }
}

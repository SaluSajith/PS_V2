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
import android.widget.AdapterView;
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
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;

/**
 * Created by User on 14/08/2017.
 */

public class AutoSearchFragment extends AbstractBaseFragment<WelcomeActivity>
        implements SearchDataCallback, Spinner.OnItemSelectedListener{

    @BindView(R.id.sp_Type) Spinner sp_Type;
    @BindView(R.id.sp_CatType) Spinner sp_CatType;
    @BindView(R.id.edt_search) EdittextPret edt_search;
    @BindView(R.id.tv_heading) TextViewPret tv_heading;
    @BindView(R.id.tv_heading_view) TextViewPret tv_heading_view;
    @BindView(R.id.rv_Search) RecyclerView rv_Search;
    @BindView(R.id.rv_View) RecyclerView rv_View;

    private String mStrSearch;
    private ArrayList<BasicModel> mSearchModels;
    private ArrayList<BasicModel> mViewModels;
    private ArrayList<BasicModel> mCatModels;
    private AutoSearchAdapter searchAdapter, viewAdapter;

    @Override
    public void onResume() {
        super.onResume();
        ((SearchActivity)getActivity()).getRecentPage(this, "0");//TODO All
    }

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_autosearch, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        setupTypeSpinner();
        mViewModels = new ArrayList<>();
        mSearchModels = new ArrayList<>();
        mCatModels = new ArrayList<>();
        viewAdapter = new AutoSearchAdapter(getActivity(), mViewModels);
        searchAdapter = new AutoSearchAdapter(getActivity(), mSearchModels);
        tv_heading.setVisibility(View.GONE);
        tv_heading_view.setVisibility(View.GONE);
        sp_Type.setOnItemSelectedListener(this);
        sp_CatType.setOnItemSelectedListener(this);
        Utility.setListLayoutManager(rv_Search, getActivity());
        Utility.setListLayoutManager(rv_View, getActivity());

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSearchData();
                    return true;
                }
                return false;
            }
        });
        edt_search.setDrawableClickListener(new EdittextPret.DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        getSearchData();
                        break;
                    default:
                        break;
                }
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAutoSearchData(s);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });
    }
    private void getAutoSearchData(CharSequence s){
        try {
            mStrSearch = s.toString();
            if(mStrSearch.length()>=1)
                ((SearchActivity) getActivity()).getAutoSearch(mStrSearch,
                        mCatModels.get(sp_CatType.getSelectedItemPosition()).getId(),
                        sp_Type.getSelectedItemPosition() + "");
        }catch (Exception e){}
    }

    private void getSearchData(){
        try {
            if(mStrSearch.length()>=1)
                ((SearchActivity)getActivity()).openSearchResult(mStrSearch,
                        mCatModels.get(sp_CatType.getSelectedItemPosition()).getId(),
                        sp_Type.getSelectedItemPosition()+"");
        }catch (Exception e){}
    }

    private void setupCatTypeSpinner(ArrayList<BasicModel> catModels){
        List<String> list = new ArrayList<String>();
        for(BasicModel model : catModels){
            list.add(model.getCategory());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.row_text, list);
        dataAdapter.setDropDownViewResource(R.layout.row_text);
        sp_CatType.setAdapter(dataAdapter);
        String id = "";
        if (getActivity().getIntent() != null) {
            if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().getExtras()
                    .containsKey(ID_KEY)) {
                id = getActivity().getIntent().getStringExtra(ID_KEY);
            }
        }
        for(int i=0;i<catModels.size();i++){
            if(catModels.get(i).getId().equals(id))
                sp_CatType.setSelection(i);
        }
    }

    @OnClick(R.id.iv_back)
    public void onBackPress(){
        getActivity().onBackPressed();
    }

    private void setupTypeSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("Stores");
        list.add("Products");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.row_text, list);
        dataAdapter.setDropDownViewResource(R.layout.row_text);
        sp_Type.setAdapter(dataAdapter);
    }

    @Override
    public void setRecentsearchList(ArrayList<BasicModel> recentViewModels,
                                    ArrayList<BasicModel> recentSearchModels,
                                    ArrayList<BasicModel> catModels) {
        if(recentSearchModels.size()>0)
            tv_heading.setVisibility(View.VISIBLE);
        if(recentViewModels.size()>0)
            tv_heading_view.setVisibility(View.VISIBLE);
        setupCatTypeSpinner(catModels);
        mSearchModels.clear();
        mSearchModels.addAll(recentSearchModels);
        mViewModels.clear();
        mViewModels.addAll(recentViewModels);
        mCatModels.addAll(catModels);
        rv_Search.setAdapter(searchAdapter);
        rv_View.setAdapter(viewAdapter);
        searchAdapter.notifyDataSetChanged();
        viewAdapter.notifyDataSetChanged();
    }

    @Override
    public void setAutosearchList(ArrayList<BasicModel> searchModels) {
        mSearchModels.clear();
        mSearchModels.addAll(searchModels);

        tv_heading.setVisibility(View.GONE);
        tv_heading_view.setVisibility(View.GONE);
        rv_View.setVisibility(View.GONE);

        rv_Search.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSearchList(ArrayList<StoreListModel> searchModels, boolean b) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        edt_search.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}

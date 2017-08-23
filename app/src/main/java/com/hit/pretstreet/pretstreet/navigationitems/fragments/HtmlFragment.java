package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/17/2017.
 */

public class HtmlFragment  extends AbstractBaseFragment<NavigationItemsActivity> {
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_html, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        ((NavigationItemsActivity)getActivity()).getHtmlData();
    }
/*
    @OnClick(R.id.btn_submit)
    public void onSubmitPressed() {
        getActivity().finish();
    }*/

}
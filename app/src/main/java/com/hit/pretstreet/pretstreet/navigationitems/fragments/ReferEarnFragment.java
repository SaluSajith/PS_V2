package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.controllers.NavItemsController;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 10/11/2017.
 */

public class ReferEarnFragment extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.txt_frndscount) TextViewPret txt_frndscount;
    @BindView(R.id.txt_invitecode) TextViewPret txt_invitecode;
    @BindView(R.id.txt_cat_name) TextViewPret txt_cat_name;
    //@BindView(R.id.rv_designers) RecyclerView rv_designers;
    NavItemsController navItemsController;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refer_earn, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        navItemsController = new NavItemsController(getActivity());
        //txt_cat_name.bringToFront();
    }

}
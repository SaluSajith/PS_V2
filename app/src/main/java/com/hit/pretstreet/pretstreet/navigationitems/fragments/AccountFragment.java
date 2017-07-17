package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/14/2017.
 */

public class AccountFragment extends AbstractBaseFragment<WelcomeActivity> {
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitPressed() {
        getActivity().finish();
    }

}
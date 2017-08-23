package com.hit.pretstreet.pretstreet.core.customview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 17/08/2017.
 */

public class EmptyFragment extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.tv_msg) TextViewPret tv_msg;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_view, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        tv_msg.setText(getArguments().getString("error"));
    }
}
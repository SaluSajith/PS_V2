package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/13/2017.
 */

public class SplashFragment extends AbstractBaseFragment<WelcomeActivity> {

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        /*ButterKnife.bind(this, view);
        PreferenceServices.init(getActivity());*/
    }
}
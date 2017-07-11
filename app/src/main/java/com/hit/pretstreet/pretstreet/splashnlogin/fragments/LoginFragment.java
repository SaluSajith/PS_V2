package com.hit.pretstreet.pretstreet.splashnlogin.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeScreen;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/7/2017.
 */

public class LoginFragment extends AbstractBaseFragment<WelcomeScreen>{

    @BindView(R.id.tv_heading)TextViewPret tv_heading;
    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        tv_heading.setPaintFlags(tv_heading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick(R.id.btn_login)
    public void onLoginPressed() {
        getActivity().finish();
       startActivity(new Intent(getActivity(), HomeActivity.class));
    }

}

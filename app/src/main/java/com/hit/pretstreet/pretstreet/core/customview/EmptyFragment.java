package com.hit.pretstreet.pretstreet.core.customview;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.SubCatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.HOMEPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SUBCATPAGE;

/**
 * Created by User on 17/08/2017.
 */

public class EmptyFragment extends AbstractBaseFragment<WelcomeActivity> {

    @BindView(R.id.tv_msg) TextViewPret tv_msg;
    @BindView(R.id.network_error)AppCompatImageView network_error;
    @BindView(R.id.btn_retry) ButtonPret btn_retry;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_view, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        tv_msg.setText(getArguments().getString("error"));
        String retry = getArguments().getString("retry");
        if(retry.equalsIgnoreCase("1")){
            network_error.setVisibility(View.GONE);
            btn_retry.setVisibility(View.VISIBLE);
        } else {
            network_error.setVisibility(View.VISIBLE);
            btn_retry.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_retry)
    public void onRetryPressed() {
        String pageid = getArguments().getString("pageid");
        switch (pageid) {
            case HOMEPAGE:
                ((HomeActivity) getActivity()).refreshPage();
                break;
            case SUBCATPAGE:
                ((SubCatActivity)getActivity()).refreshPage();
                break;
            default: break;
        }
    }
}
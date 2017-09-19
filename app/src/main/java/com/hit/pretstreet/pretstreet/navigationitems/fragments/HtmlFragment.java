package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.helpers.MyTagHandler;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 7/17/2017.
 */

public class HtmlFragment  extends AbstractBaseFragment<NavigationItemsActivity> {

    String URL;
    @BindView(R.id.ll_empty) View ll_empty;
    @BindView(R.id.txt_content) TextViewPret txt_content;

    @Override
    protected View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_html, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        String html = this.getArguments().getString(Constant.PARCEL_KEY);
        if(html.length()!=0){
            loadHtml(html);
        }else {
            URL = this.getArguments().getString(Constant.URL_KEY);
            ((NavigationItemsActivity) getActivity()).getHtmlData(URL);
        }
    }

    public void loadHtml(String html){
        if(html.trim().length()!=0) {
            txt_content.setVisibility(View.VISIBLE);
            txt_content.setText(Html.fromHtml(html, null, new MyTagHandler()));
            ll_empty.setVisibility(View.GONE);
        }
        else ll_empty.setVisibility(View.VISIBLE);
    }
}
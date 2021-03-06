package com.hit.pretstreet.pretstreet.navigationitems.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.NestedWebView;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseFragment;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/17/2017.
 */

public class HtmlFragment  extends AbstractBaseFragment<NavigationItemsActivity> {

    String URL;
    @BindView(R.id.ll_empty) View ll_empty;
    @BindView(R.id.webview)
    NestedWebView webview;
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
            try {
                webview.setWebViewClient(new MyBrowser());
                webview.getSettings().setLoadsImagesAutomatically(true);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webview.loadData(html,"text/html" , "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*txt_content.setVisibility(SView.VISIBLE);
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                    txt_content.setText(Html.fromHtml(html));
                else
                    txt_content.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //txt_content.setText(Html.fromHtml(html, null, new MyTagHandler()));
            ll_empty.setVisibility(SView.GONE);*/
        }
        else ll_empty.setVisibility(View.VISIBLE);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
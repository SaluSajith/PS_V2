package com.hit.pretstreet.pretstreet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hit.pretstreet.pretstreet.R;

import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    private ArrayList<String> searchHistory;
    String mUrl = "http://pretstreet.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().hide();
        webView = (WebView) findViewById(R.id.activity_web_view);
        searchHistory = new ArrayList<String>();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(mUrl);
        searchHistory.add(mUrl);
    }
/*
    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && searchHistory.size() > 0) {
            Log.e("Log", "Handling back keyevent");
            //remove eldest entry
            searchHistory.remove(mUrl);
            //load the new url
            webView.loadUrl(mUrl);
        }
        else{
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.hit.pretstreet.pretstreet.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.hit.pretstreet.pretstreet.R;

import java.util.ArrayList;

/**
 * Created by User on 5/10/2017.
 */

public class WebViewDialogFragment extends DialogFragment {


    public WebViewDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SlideshowDialogFragment.
     */

    private String TAG = WebViewDialogFragment.class.getSimpleName();
    WebView wv_article;

    public static WebViewDialogFragment newInstance() {
        WebViewDialogFragment fragment = new WebViewDialogFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.article_detailed_view, container, false);
        wv_article = (WebView) v.findViewById(R.id.wv_article);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = "<br /><br />Read the handouts please for tomorrow.<br /><br /><!--homework help homework" +
                "help help with homework homework assignments elementary school high school middle school" +
                "// --><font color='#60c000' size='4'><strong>Please!</strong></font>" +
                "<img src='http://www.homeworknow.com/hwnow/upload/images/tn_star300.gif'  />";

        wv_article.loadDataWithBaseURL("", html, mimeType, encoding, "");
        return v;
    }
}

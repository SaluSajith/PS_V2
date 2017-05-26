package com.hit.pretstreet.pretstreet.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;

public class ArticleActivity extends AppCompatActivity {

    WebView wv_article;
    TextView txt_storename;
    Button btn_back;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detailed_view);
        getSupportActionBar().hide();

        wv_article = (WebView) findViewById(R.id.wv_article);
        txt_storename = (TextView) findViewById(R.id.txt_storename);
        btn_back = (Button) findViewById(R.id.btn_back);

        Intent intent = getIntent();
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = intent.getStringExtra("articleData");

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "RedVelvet-Regular.otf");
        txt_storename.setTypeface(font);
        /*String html = "<br /><br />Read the handouts please for tomorrow.<br /><br /><!--homework help homework" +
                "help help with homework homework assignments elementary school high school middle school" +
                "// --><font color='#60c000' size='4'><strong>Please!</strong></font>" +
                "<img src='http://www.homeworknow.com/hwnow/upload/images/tn_star300.gif'  />";*/

        txt_storename.setText(intent.getStringExtra("name"));
        wv_article.loadDataWithBaseURL("", html, mimeType, encoding, "");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

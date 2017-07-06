package com.hit.pretstreet.pretstreet.storedetails;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.helpers.testimonials.ShadowTransformer;
import com.hit.pretstreet.pretstreet.core.helpers.testimonials.CardFragmentPagerAdapter;

public class StoreDetailsActivity extends AppCompatActivity{

    TextViewPret tv_info, tv_imgsrc, tv_produuct, tv_testimonials_heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storedetails);
        /* Intent intent = getIntent();
        final String cheeseName = intent.getStringExtra(EXTRA_NAME);*/

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUi();
    }

    private void initUi(){
        tv_info = (TextViewPret) findViewById(R.id.tv_info);
        tv_imgsrc = (TextViewPret) findViewById(R.id.tv_imgsrc);
        tv_produuct = (TextViewPret) findViewById(R.id.tv_products);
        tv_testimonials_heading = (TextViewPret) findViewById(R.id.tv_testimonials_heading);

        SpannableString content = new SpannableString("Testimonials");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_testimonials_heading.setText(content);

        String sourceString = "<b>" + "Info: " + "</b> " + "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. ";
        tv_info.setText(Html.fromHtml(sourceString));
        sourceString = "<b>" + "Image Source: " + "</b> " + "Instagram";
        tv_imgsrc.setText(Html.fromHtml(sourceString));
        sourceString = "<b>" + "Product: " + "</b> " + "Bangles, Bracelets, Chains, Earrings, Necklaces, Nose pins, Pendant, Rings";
        tv_produuct.setText(Html.fromHtml(sourceString));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(1, this));
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("TANISHQ Showroom Gurgav Area");
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        loadBackdrop();

    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.base).centerCrop().into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Change value in dp to pixels
     * @param dp
     * @param context
     * @return
     */
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

}
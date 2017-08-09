package com.hit.pretstreet.pretstreet;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.adapters.TrendingArticleAdapter;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AbstractBaseAppCompatActivity {
    @BindView(R.id.product_list)
    RecyclerView rv_trending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        rv_trending.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ArrayList<TrendingItems> exHItems = new ArrayList<>();
        TrendingArticleAdapter mAdapter = new TrendingArticleAdapter(getApplicationContext(), exHItems);
        rv_trending.setAdapter(mAdapter);
    }

    @Override
    protected void setUpController() {

    }
}

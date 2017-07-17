package com.hit.pretstreet.pretstreet;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;

public class TestActivity extends AbstractBaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void setUpController() {

    }
}

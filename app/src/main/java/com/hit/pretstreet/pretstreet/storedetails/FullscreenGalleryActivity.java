package com.hit.pretstreet.pretstreet.storedetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.storedetails.view.SlideshowDialogFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class FullscreenGalleryActivity extends AbstractBaseAppCompatActivity {

    @BindView(R.id.content) FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_gallery);

        ArrayList imageModels =  getIntent()
                .getStringArrayListExtra(Constant.PARCEL_KEY);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constant.PARCEL_KEY, imageModels);
        bundle.putInt(Constant.PRE_PAGE_KEY, getIntent().getIntExtra(Constant.PRE_PAGE_KEY, 0));
        bundle.putInt(Constant.POSITION_KEY, getIntent().getIntExtra(Constant.POSITION_KEY, 0));

        Fragment fragment = new SlideshowDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }

    @Override
    protected void setUpController() {
    }
}

package com.hit.pretstreet.pretstreet.splashnlogin;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.fragments.WelcomeFragment;

public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceServices.init(this);
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String valueOne = uri.getQueryParameter("share");
            String id = uri.getQueryParameter("id");

            PreferenceServices.getInstance().setIdQueryparam(id);
            PreferenceServices.getInstance().setShareQueryparam(valueOne);
        }

        if (PreferenceServices.getInstance().geUsertId().equalsIgnoreCase("")) {
            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
        } else {
            if (PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("")
                    || PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("")) {
                startActivity(new Intent(getApplicationContext(), DefaultLocationActivity.class));
            } else {
                intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
        }
    }
}

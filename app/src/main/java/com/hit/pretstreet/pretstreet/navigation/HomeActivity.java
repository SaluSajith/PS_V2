package com.hit.pretstreet.pretstreet.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.DividerDecoration;
import com.hit.pretstreet.pretstreet.core.customview.NotificationBadge;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.navigation.adapters.NavDrawerAdapter;
import com.hit.pretstreet.pretstreet.navigation.interfaces.NavigationClick;
import com.hit.pretstreet.pretstreet.navigation.models.NavDrawerItem;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.storedetails.StoreDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationClick {

    //TODO put same in NavigationItemsActivity as well
    private int selectedFragment = 0;
    private static final int ACCOUNT_FRAGMENT = 0;
    private static final int FOLLOWING_FRAGMENT = 1;
    private static final int ABOUT_FRAGMENT = 2;
    private static final int ADDSTORE_FRAGMENT = 3;
    private static final int CONTACTUS_FRAGMENT = 4;
    private static final int HOME_FRAGMENT = 5;

    @BindView(R.id.tv_location) TextViewPret tv_location;
    @BindView(R.id.iv_location) ImageView iv_location;

    NavDrawerAdapter navDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ButterKnife.bind(this, toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setPadding(0, Utility.getStatusBarHeight(HomeActivity.this), 0, 0);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NotificationBadge mBadge = (NotificationBadge) navigationView.findViewById(R.id.badge);
        mBadge.setNumber(10);
        mBadge.bringToFront();

        NavDrawerItem[] navArray = new NavDrawerItem[]{
                new NavDrawerItem("nav_home", "Home"),
                new NavDrawerItem("nav_account", "Account"),
                new NavDrawerItem("nav_following", "Following"),
                new NavDrawerItem("nav_addstore", "Add Store"),
                new NavDrawerItem("nav_about", "About Pretstreet"),
                new NavDrawerItem("nav_contact", "Contact Us/Support"),
                new NavDrawerItem("storedetails", "Store details")};

        navDrawerAdapter = new NavDrawerAdapter(HomeActivity.this, navArray, HomeActivity.this);
        RecyclerView rv_nav = (RecyclerView) findViewById(R.id.rv_nav);
        Utility.setListLayoutManager(rv_nav, HomeActivity.this);
        rv_nav.addItemDecoration(new DividerDecoration(getApplicationContext(),
                ContextCompat.getColor(getApplicationContext(), R.color.trending_grey), 0.5f));
        rv_nav.setNestedScrollingEnabled(false);
        rv_nav.getItemAnimator().setChangeDuration(0);
        rv_nav.setAdapter(navDrawerAdapter);

        ImageView iv_menu = (ImageView) toolbar.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isHovered())
                    drawer.openDrawer(Gravity.LEFT);
            }
        });
        LinearLayout ll_header = (LinearLayout) toolbar.findViewById(R.id.linearLayout);
        ll_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isHovered())
                    drawer.openDrawer(Gravity.LEFT);
            }
        });

        NotificationBadge badge_home = (NotificationBadge)toolbar.findViewById(R.id.badge);
        badge_home.setNumber(10);
        badge_home.bringToFront();

        TextViewPret tv_rateus = (TextViewPret) drawer.findViewById(R.id.tv_rateus);
        tv_rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getApplicationContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void menuOnClick(String id) {
        String itemId = id;
        Toast.makeText(HomeActivity.this, id, Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (itemId) {
            case "nav_home":

                break;
            case "nav_account":
                selectedFragment = ACCOUNT_FRAGMENT;
                Intent intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_following":
                selectedFragment = FOLLOWING_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_addstore":
                selectedFragment = ADDSTORE_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_about":
                selectedFragment = ABOUT_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "nav_contact":
                selectedFragment = CONTACTUS_FRAGMENT;
                intent = new Intent(HomeActivity.this, NavigationItemsActivity.class);
                intent.putExtra("fragment", selectedFragment);
                startActivity(intent);
                break;
            case "storedetails":
                intent = new Intent(HomeActivity.this, StoreDetailsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.tv_location)
    public void onTvLocationPressed() {
        Intent intent = new Intent(HomeActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.iv_location)
    public void onLocationPressed() {
        Intent intent = new Intent(HomeActivity.this, DefaultLocationActivity.class);
        startActivity(intent);
    }


    private void changeFragment(Fragment fragment, boolean addBackstack, int content) {

        FragmentManager fm = getSupportFragmentManager();       /*Removing stack*/
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        FrameLayout fl_content = (FrameLayout) findViewById(R.id.content);
        fl_content.removeAllViews();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); /* Fragment transition*/
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if(content==HOME_FRAGMENT)
            ft.add(R.id.content, fragment);
        else {
            fl_content.removeAllViews();
            ft.replace(R.id.content_main, fragment);
        }
        if (addBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
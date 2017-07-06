package com.hit.pretstreet.pretstreet.core.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.PageState;
import com.hit.pretstreet.pretstreet.core.managers.PreferenceManager;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;

/**
 * Created by User on 7/5/2017.
 */

public abstract class AbstractBaseAppCompatActivity extends AppCompatActivity {

    protected PageState pageState;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpPageState();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        PreferenceServices.init(this);
    }

    private void setUpPageState() {
        this.pageState = new PageState(this);
        this.pageState.setOnPageStateListner(new PageState.PageStateListener() {
            @Override
            public void onErrorIconClicked() {
                onErrorRefresh();
            }
        });
    }

    public void onErrorRefresh() {

    }

    public void displaySnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void displaySnackBar(@StringRes int message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void logMessage(String message) {
        Log.d("DEBUG", message);
    }

    public void logError(String error) {
        Log.e("ERROR", error);
    }

    public void displayDialog(String title, String message, boolean isCancellable,
                              @StringRes int positiveText, DialogInterface.OnClickListener positiveTextClickListener,
                              @StringRes int cancelText, DialogInterface.OnClickListener cancelTextClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title).setCancelable(isCancellable);

        if (positiveText != 0 || positiveTextClickListener != null) {
            builder.setPositiveButton(positiveText, positiveTextClickListener);
        }

        if (cancelText != 0 || cancelTextClickListener != null) {
            builder.setNegativeButton(cancelText, cancelTextClickListener);
        }

        builder.create().show();
    }

    public void startActivityAndFinish(Class<?> mActivity) {
        try {
            Intent intent = new Intent(this, mActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void permissionGrantSuccess(int permissionCode) {

    }

    public boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }

    public void requestPermission(String requestPermission, int permissionCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, requestPermission)) {
            displaySnackBar("Required permission access");
        }
        ActivityCompat.requestPermissions(this, new String[]{requestPermission}, permissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /*if (requestCode == HomeActivity.CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGrantSuccess(requestCode);
            } else {
                displaySnackBar("Required permission access");
            }
        }*/
    }

    public void showProgressDialog(String mContent) {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setContentView(R.layout.loading_view);
            pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void destroyDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDialog();
    }

    @Override
    public void onBackPressed() {
        if (pageState.isLoading()) return;
        super.onBackPressed();
    }
}


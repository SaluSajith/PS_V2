package com.hit.pretstreet.pretstreet.core.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by User on 6/27/2017.
 */

public class Utility {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /** Settingup layout manager for recyclerview inside Nested scrollview
     *  Scrollable header*/
    public static LinearLayoutManager setListLayoutManager(RecyclerView recyclerView, Context context){
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false); //to make header scrollable
        recyclerView.setHasFixedSize(true);
        //recyclerView.setItemViewCacheSize(20);
        //recyclerView.setDrawingCacheEnabled(true);
        //recyclerView.setDrawingCacheQuality(SView.DRAWING_CACHE_QUALITY_HIGH);
        return llm;
    }

    /** Settingup layout manager for recyclerview inside Nested scrollview
     *  Fixed header*/
    public static LinearLayoutManager setListLayoutManager_(RecyclerView recyclerView, Context context){
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setItemViewCacheSize(20);
        //recyclerView.setDrawingCacheEnabled(true);
        //recyclerView.setDrawingCacheQuality(SView.DRAWING_CACHE_QUALITY_HIGH);
        return llm;
    }

    /** Settingup layout manager for gridview inside Nested scrollview
     *  Scrollable header*/
    public static LinearLayoutManager setGridLayoutManager(RecyclerView recyclerView, Context context, int columnCount){
        GridLayoutManager llm = new GridLayoutManager(context, columnCount);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        return llm;
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean validCellPhone(String number){
        boolean status = false;
        if(android.util.Patterns.PHONE.matcher(number).matches()) {
            String regexStr = "^[789]\\d{9}$";
            if(number.length()<10 || number.length()>13 || number.matches(regexStr)==false  ) {
                status = false;
            } else status = true;
        }else
            status = false;
        return status;
    }

    public static void hide_keyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

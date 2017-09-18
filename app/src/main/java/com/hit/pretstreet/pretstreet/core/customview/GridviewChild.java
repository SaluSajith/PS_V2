package com.hit.pretstreet.pretstreet.core.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by User on 15/09/2017.
 */

public class GridviewChild extends RelativeLayout {

    LayoutInflater mInflater;
    public GridviewChild(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);

    }
    public GridviewChild(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
    }
    public GridviewChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}

package com.hit.pretstreet.pretstreet.core.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * Created by User on 15/09/2017.
 * Custom Gridview child in homepage listing
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

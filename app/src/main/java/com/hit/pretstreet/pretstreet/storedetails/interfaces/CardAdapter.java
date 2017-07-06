package com.hit.pretstreet.pretstreet.storedetails.interfaces;

import android.support.v7.widget.CardView;

/**
 * Created by User on 6/30/2017.
 */

public interface CardAdapter {
    public final int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
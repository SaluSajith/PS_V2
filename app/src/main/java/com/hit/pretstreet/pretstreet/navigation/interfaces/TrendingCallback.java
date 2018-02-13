package com.hit.pretstreet.pretstreet.navigation.interfaces;

import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;

import java.util.ArrayList;

/**
 * Created by User on 03/08/2017.
 */

public interface TrendingCallback {
    void bindData(ArrayList<TrendingItems> trendingItems, String message);
}

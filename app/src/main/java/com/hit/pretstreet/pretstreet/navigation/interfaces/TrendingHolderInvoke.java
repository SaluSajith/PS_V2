package com.hit.pretstreet.pretstreet.navigation.interfaces;

import com.hit.pretstreet.pretstreet.navigation.models.ProductImageItem;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

/**
 * Created by User on 04/08/2017.
 */

public interface TrendingHolderInvoke {
    void shareUrl(String text);
    void likeInvoke(int trendingId);
    void openTrendingArticle(TrendingItems trendingItems, String prePage);
    void loadStoreDetails(int position, StoreListModel storeListModel);
}

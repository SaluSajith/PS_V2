package com.hit.pretstreet.pretstreet.navigation.interfaces;

import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

/**
 * Created by User on 04/08/2017.
 */

public interface TrendingHolderInvoke {
    void shareurl(String text);
    void likeInvoke(int trendingId, int pageId);
    void registerInvoke(int trendingId);
    void interestInvoke(int trendingId, int pageId);
    void openTrendingArticle(TrendingItems trendingItems, String prePage);
    void loadStoreDetails(int position, StoreListModel storeListModel);
}

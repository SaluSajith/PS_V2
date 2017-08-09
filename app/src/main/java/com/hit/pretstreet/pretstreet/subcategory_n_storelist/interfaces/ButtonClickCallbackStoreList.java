package com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces;

import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

/**
 * Created by User on 31/07/2017.
 */

public interface ButtonClickCallbackStoreList {
    void buttonClick(StoreListModel storeListModel);
    void updateFollowStatus(String id);
}

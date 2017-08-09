package com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces;

import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;

/**
 * Created by User on 7/25/2017.
 */

public interface SubCatTrapeClick {
    void onTrapeClick(HomeCatItems homeSubCategory);
    void onSubTrapeClick(HomeCatContentData homeCatContentData, String title);
}

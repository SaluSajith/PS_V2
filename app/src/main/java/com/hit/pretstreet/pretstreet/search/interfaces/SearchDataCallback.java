package com.hit.pretstreet.pretstreet.search.interfaces;

import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

/**
 * Created by User on 14/08/2017.
 */

public interface SearchDataCallback {
    void setRecentsearchList(ArrayList<BasicModel> recentViewModels,
                             ArrayList<BasicModel> recentSearchModels,
                             ArrayList<BasicModel> catModels);
    void setAutosearchList(ArrayList<BasicModel> searchModels);
    void setSearchList(ArrayList<StoreListModel> searchModels, boolean loadmore);
}

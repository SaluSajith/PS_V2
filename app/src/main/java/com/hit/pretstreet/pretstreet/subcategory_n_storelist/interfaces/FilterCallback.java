package com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces;

import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.FilterDataModel;

import java.util.ArrayList;

/**
 * Created by User on 18/08/2017.
 */

public interface FilterCallback {
    void updateStatus(ArrayList<FilterDataModel> dataModels);
}

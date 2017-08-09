package com.hit.pretstreet.pretstreet.storedetails.interfaces;

import com.hit.pretstreet.pretstreet.storedetails.model.ImageModel;

import java.util.ArrayList;

/**
 * Created by User on 02/08/2017.
 */

public interface ImageClickCallback {
    void onClicked(int position, ArrayList<String> imageModels);
}

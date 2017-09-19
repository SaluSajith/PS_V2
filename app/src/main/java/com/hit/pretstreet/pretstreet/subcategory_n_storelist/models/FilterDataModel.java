package com.hit.pretstreet.pretstreet.subcategory_n_storelist.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hit.pretstreet.pretstreet.search.models.SearchModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pratap.kesaboyina on 30-11-2015.
 */
public class FilterDataModel implements Serializable{

    private String headerTitle = "";
    private ArrayList<SearchModel> allItemsInSection =  new ArrayList<>();

    public FilterDataModel() {
    }
    public FilterDataModel(String headerTitle, ArrayList<SearchModel> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }
    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<SearchModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<SearchModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}

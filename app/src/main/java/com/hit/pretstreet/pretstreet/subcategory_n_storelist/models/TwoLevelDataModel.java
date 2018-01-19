package com.hit.pretstreet.pretstreet.subcategory_n_storelist.models;

import com.hit.pretstreet.pretstreet.search.models.BasicModel;

import java.io.Serializable;
import java.util.ArrayList;

public class TwoLevelDataModel implements Serializable{

    private String headerTitle = "";
    private String id = "";
    private ArrayList<BasicModel> allItemsInSection =  new ArrayList<>();

    public TwoLevelDataModel() {
    }
    public TwoLevelDataModel(String headerid, String headerTitle, ArrayList<BasicModel> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.id = headerid;
        this.allItemsInSection = allItemsInSection;
    }
    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<BasicModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<BasicModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}

package com.hit.pretstreet.pretstreet.navigation.models;

import com.hit.pretstreet.pretstreet.search.models.BasicModel;

import java.util.ArrayList;

/**
 * Created by User on 6/27/2017.
 */

public class NavDrawerItem {
    public String id="";
    public String name="";
    public ArrayList<BasicModel> basicModels = new ArrayList<>();

    public NavDrawerItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public NavDrawerItem(String id, String name, ArrayList<BasicModel> basicModels) {
        this.id = id;
        this.name = name;
        this.basicModels = basicModels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<BasicModel> getBasicModels() {
        return basicModels;
    }

    public void setBasicModels(ArrayList<BasicModel> basicModels) {
        this.basicModels = basicModels;
    }
}

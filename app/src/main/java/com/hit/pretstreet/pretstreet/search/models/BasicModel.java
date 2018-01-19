package com.hit.pretstreet.pretstreet.search.models;

import java.io.Serializable;

/**
 * Created by User on 14/08/2017.
 */

public class BasicModel implements Serializable{
    String PageType="", PageTypeId="", Id="", Title="", Category="", Location="";
    boolean Status=false;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPageTypeId() {
        return PageTypeId;
    }

    public void setPageTypeId(String pageTypeId) {
        PageTypeId = pageTypeId;
    }
    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPageType() {
        return PageType;
    }

    public void setPageType(String pageType) {
        PageType = pageType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setStatus(boolean status) {
        Status = status;
    }
    public boolean getStatus(){
        return Status;
    }
}

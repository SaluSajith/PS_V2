package com.hit.pretstreet.pretstreet.search.models;

/**
 * Created by User on 14/08/2017.
 */

public class SearchModel {
    String PageType, PageTypeId, Id, Title, Category, Location;

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
}

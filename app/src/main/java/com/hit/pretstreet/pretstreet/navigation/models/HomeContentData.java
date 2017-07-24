package com.hit.pretstreet.pretstreet.navigation.models;

/**
 * Created by User on 7/24/2017.
 */

public class HomeContentData {
    String CategoryId, CategoryName, Title, ImageSource, PageType, PageTypeId;
    HomeSubCategory homeSubCategory;

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getImageSource() {
        return ImageSource;
    }

    public void setImageSource(String imageSource) {
        ImageSource = imageSource;
    }

    public String getPageType() {
        return PageType;
    }

    public void setPageType(String pageType) {
        PageType = pageType;
    }

    public String getPageTypeId() {
        return PageTypeId;
    }

    public void setPageTypeId(String pageTypeId) {
        PageTypeId = pageTypeId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public HomeSubCategory getHomeSubCategory() {
        return homeSubCategory;
    }

    public void setHomeSubCategory(HomeSubCategory homeSubCategory) {
        this.homeSubCategory = homeSubCategory;
    }
}

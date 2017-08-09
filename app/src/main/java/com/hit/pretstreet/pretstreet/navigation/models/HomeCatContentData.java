package com.hit.pretstreet.pretstreet.navigation.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class HomeCatContentData implements Serializable{
    String CategoryId, CategoryName, Title, ImageSource, PageType, PageTypeId, MainCatId;
    ArrayList<HomeCatItems> homeSubCategoryArrayList;

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

    public String getMainCatId() {
        return MainCatId;
    }

    public void setMainCatId(String mainCatId) {
        MainCatId = mainCatId;
    }

    public ArrayList<HomeCatItems> getHomeSubCategoryArrayList() {
        return homeSubCategoryArrayList;
    }

    public void setHomeSubCategoryArrayList(ArrayList<HomeCatItems> homeSubCategoryArrayList) {
        this.homeSubCategoryArrayList = homeSubCategoryArrayList;
    }
}

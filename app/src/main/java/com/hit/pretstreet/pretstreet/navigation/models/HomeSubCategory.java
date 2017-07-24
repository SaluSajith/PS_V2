package com.hit.pretstreet.pretstreet.navigation.models;

/**
 * Created by User on 7/24/2017.
 */

public class HomeSubCategory {
    String ContentTypeId, ContentType;
    HomeContentData ContentData;

    public String getContentTypeId() {
        return ContentTypeId;
    }

    public void setContentTypeId(String contentTypeId) {
        ContentTypeId = contentTypeId;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public HomeContentData getContentData() {
        return ContentData;
    }

    public void setContentData(HomeContentData contentData) {
        ContentData = contentData;
    }
}

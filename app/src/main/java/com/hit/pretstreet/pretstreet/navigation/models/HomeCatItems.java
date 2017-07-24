package com.hit.pretstreet.pretstreet.navigation.models;

/**
 * Created by User on 7/24/2017.
 */

public class HomeCatItems {
    String ContentTypeId, ContentType, Position;
    HomeContentData homeContentData;

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentTypeId(String contentTypeId) {
        ContentTypeId = contentTypeId;
    }

    public String getContentTypeId() {
        return ContentTypeId;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getPosition() {
        return Position;
    }

    public HomeContentData getHomeContentData() {
        return homeContentData;
    }

    public void setHomeContentData(HomeContentData homeContentData) {
        this.homeContentData = homeContentData;
    }
}

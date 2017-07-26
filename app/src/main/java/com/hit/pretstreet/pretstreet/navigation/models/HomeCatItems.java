package com.hit.pretstreet.pretstreet.navigation.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class HomeCatItems implements Serializable {
    String ContentTypeId, ContentType, Position;
    HomeCatContentData homeContentData;
    ArrayList<HomeCatContentData> contentDataArrayList;

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

    public HomeCatContentData getHomeContentData() {
        return homeContentData;
    }

    public void setHomeContentData(HomeCatContentData homeContentData) {
        this.homeContentData = homeContentData;
    }

    public ArrayList<HomeCatContentData> getContentDataArrayList() {
        return contentDataArrayList;
    }

    public void setContentDataArrayList(ArrayList<HomeCatContentData> contentDataArrayList) {
        this.contentDataArrayList = contentDataArrayList;
    }

}

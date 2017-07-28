package com.hit.pretstreet.pretstreet.subcategory.models;

import java.io.Serializable;

/**
 * Created by User on 7/27/2017.
 */

public class StoreListModel implements Serializable {

    String PageType, PageTypeId, Id, Title, FollowingStatus,
            FollowingCount, Location, ImageSource, OpenStatus, BannerFlag, Flags;

    public String getBannerFlag() {
        return BannerFlag;
    }

    public void setBannerFlag(String bannerFlag) {
        BannerFlag = bannerFlag;
    }

    public String getFlags() {
        return Flags;
    }

    public void setFlags(String flags) {
        Flags = flags;
    }

    public String getFollowingCount() {
        return FollowingCount;
    }

    public void setFollowingCount(String followingCount) {
        FollowingCount = followingCount;
    }

    public String getFollowingStatus() {
        return FollowingStatus;
    }

    public void setFollowingStatus(String followingStatus) {
        FollowingStatus = followingStatus;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImageSource() {
        return ImageSource;
    }

    public void setImageSource(String imageSource) {
        ImageSource = imageSource;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getOpenStatus() {
        return OpenStatus;
    }

    public void setOpenStatus(String openStatus) {
        OpenStatus = openStatus;
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
}

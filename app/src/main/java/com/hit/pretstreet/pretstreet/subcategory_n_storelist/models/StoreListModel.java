package com.hit.pretstreet.pretstreet.subcategory_n_storelist.models;

import java.io.Serializable;

/**
 * Created by User on 7/27/2017.
 */

public class StoreListModel implements Serializable {

    private String PageType="", PageTypeId="", Id="", Title="",
            FollowingCount="", Location="", ImageSource="", Share="", ClickType="";
    private boolean Saleflag=false, Offerflag=false, Newflag=false, FollowingStatus=false,
            OpenStatus=false, BannerFlag=false, loadmoreFlag=false;

    public Boolean getBannerFlag() {
        return BannerFlag;
    }

    public void setBannerFlag(Boolean bannerFlag) {
        BannerFlag = bannerFlag;
    }

    public void setNewflag(Boolean newflag) {
        Newflag = newflag;
    }

    public Boolean getNewflag() {
        return Newflag;
    }

    public void setOfferflag(Boolean offerflag) {
        Offerflag = offerflag;
    }

    public Boolean getOfferflag() {
        return Offerflag;
    }

    public void setSaleflag(Boolean saleflag) {
        Saleflag = saleflag;
    }

    public Boolean getSaleflag() {
        return Saleflag;
    }

    public String getFollowingCount() {
        return FollowingCount;
    }

    public void setFollowingCount(String followingCount) {
        FollowingCount = followingCount;
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

    public void setFollowingStatus(Boolean followingStatus) {
        FollowingStatus = followingStatus;
    }

    public Boolean getFollowingStatus() {
        return FollowingStatus;
    }

    public void setOpenStatus(Boolean openStatus) {
        OpenStatus = openStatus;
    }

    public Boolean getOpenStatus() {
        return OpenStatus;
    }

    public void setLoadmoreFlag(boolean loadmoreFlag) {
        this.loadmoreFlag = loadmoreFlag;
    }

    public Boolean getLoadmoreFlag() {
        return loadmoreFlag;
    }

    public String getShare() {
        return Share;
    }

    public void setShare(String share) {
        Share = share;
    }

    public void setClickType(String clickType) {
        ClickType = clickType;
    }

    public String getClickType() {
        return ClickType;
    }
}

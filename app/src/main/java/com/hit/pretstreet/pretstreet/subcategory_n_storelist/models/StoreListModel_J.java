package com.hit.pretstreet.pretstreet.subcategory_n_storelist.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by User on 14/09/2017.
 */

public class StoreListModel_J {

    @JsonProperty
    private String PageType, PageTypeId, Id, Title,
            FollowingCount, Location, ImageSource, Share;
    @JsonProperty
    private boolean Saleflag, Offerflag, Newflag, FollowingStatus, OpenStatus, BannerFlag, loadmoreFlag;

    @JsonAnyGetter
    public Boolean getBannerFlag() {
        return BannerFlag;
    }

    @JsonAnySetter
    public void setBannerFlag(Boolean bannerFlag) {
        BannerFlag = bannerFlag;
    }

    @JsonAnySetter
    public void setNewflag(Boolean newflag) {
        Newflag = newflag;
    }

    @JsonAnyGetter
    public Boolean getNewflag() {
        return Newflag;
    }

    @JsonAnySetter
    public void setOfferflag(Boolean offerflag) {
        Offerflag = offerflag;
    }

    @JsonAnyGetter
    public Boolean getOfferflag() {
        return Offerflag;
    }

    @JsonAnySetter
    public void setSaleflag(Boolean saleflag) {
        Saleflag = saleflag;
    }

    @JsonAnyGetter
    public Boolean getSaleflag() {
        return Saleflag;
    }

    @JsonAnyGetter
    public String getFollowingCount() {
        return FollowingCount;
    }

    @JsonAnySetter
    public void setFollowingCount(String followingCount) {
        FollowingCount = followingCount;
    }

    @JsonAnyGetter
    public String getId() {
        return Id;
    }

    @JsonAnySetter
    public void setId(String id) {
        Id = id;
    }

    @JsonAnyGetter
    public String getImageSource() {
        return ImageSource;
    }

    @JsonAnySetter
    public void setImageSource(String imageSource) {
        ImageSource = imageSource;
    }

    @JsonAnyGetter
    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    @JsonAnyGetter
    public String getPageType() {
        return PageType;
    }

    @JsonAnySetter
    public void setPageType(String pageType) {
        PageType = pageType;
    }

    @JsonAnyGetter
    public String getPageTypeId() {
        return PageTypeId;
    }

    @JsonAnySetter
    public void setPageTypeId(String pageTypeId) {
        PageTypeId = pageTypeId;
    }

    @JsonAnyGetter
    public String getTitle() {
        return Title;
    }

    @JsonAnySetter
    public void setTitle(String title) {
        Title = title;
    }

    @JsonAnySetter
    public void setFollowingStatus(Boolean followingStatus) {
        FollowingStatus = followingStatus;
    }

    @JsonAnyGetter
    public Boolean getFollowingStatus() {
        return FollowingStatus;
    }

    @JsonAnySetter
    public void setOpenStatus(Boolean openStatus) {
        OpenStatus = openStatus;
    }

    @JsonAnyGetter
    public Boolean getOpenStatus() {
        return OpenStatus;
    }

    @JsonAnySetter
    public void setLoadmoreFlag(boolean loadmoreFlag) {
        this.loadmoreFlag = loadmoreFlag;
    }

    @JsonAnyGetter
    public Boolean getLoadmoreFlag() {
        return loadmoreFlag;
    }

    @JsonAnyGetter
    public String getShare() {
        return Share;
    }

    @JsonAnySetter
    public void setShare(String share) {
        Share = share;
    }
}

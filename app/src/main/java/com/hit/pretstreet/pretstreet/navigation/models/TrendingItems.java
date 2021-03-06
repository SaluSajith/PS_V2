package com.hit.pretstreet.pretstreet.navigation.models;

import java.io.Serializable;
import java.util.ArrayList;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGPAGE;

/**
 * Created by User on 5/9/2017.
 */

public class TrendingItems implements Serializable {

    String id="", storeLink="", logoImage="", title="", article="", articledate="", pagetype = TRENDINGPAGE, pagetypeid="", storeid="", clicktype="";
    String storeName="", shareUrl="", area="", titlepagetype="", titleid="", register="";
    boolean like = false, banner = false, NotifPage = false;
    String latitude="", longitude="";
    ArrayList<String> imagearray = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String image) {
        this.logoImage = image;
    }

    public String getStoreLink() {
        return storeLink;
    }

    public void setStoreLink(String storeLink) {
        this.storeLink = storeLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getArticledate() {
        return articledate;
    }

    public void setArticledate(String articledate) {
        this.articledate = articledate;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean getLike() {
        return like;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<String> getImagearray() {
        return imagearray;
    }

    public void setImagearray(ArrayList<String> imagearray) {
        this.imagearray = imagearray;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }
    public boolean getBanner() {
       return banner;
    }

    public void setPagetype(String pagetype) {
        this.pagetype = pagetype;
    }

    public String getPagetype() {
        return pagetype;
    }

    public void setPagetypeid(String pagetypeid) {
        this.pagetypeid = pagetypeid;
    }

    public String getPagetypeid() {
        return pagetypeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setClicktype(String clicktype) {
        this.clicktype = clicktype;
    }

    public String getClicktype() {
        return clicktype;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getRegisterFlag() {
        return register;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setTitleid(String titleid) {
        this.titleid = titleid;
    }

    public String getTitleid() {
        return titleid;
    }

    public void setTitlepagetype(String titlepagetype) {
        this.titlepagetype = titlepagetype;
    }

    public String getTitlepagetype() {
        return titlepagetype;
    }

    public void setNotifPage(boolean notifPage) {
        NotifPage = notifPage;
    }

    public boolean isNotifPage() {
        return NotifPage;
    }
    /*
    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgWidth() {
        return imgWidth;
    }*/
}
package com.hit.pretstreet.pretstreet.Items;

import org.json.JSONArray;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by User on 5/9/2017.
 */

public class TrendingItems implements Serializable {

    String id, storeLink, logoImage, title, article, articledate;
    String like, storeName;
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

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
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

    public void setImagearray(ArrayList<String> cat_name) {
        this.imagearray = imagearray;
    }

}
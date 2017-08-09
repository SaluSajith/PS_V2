package com.hit.pretstreet.pretstreet.navigation.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 04/08/2017.
 */

public class ProductImageItem implements Parcelable {
    String image;

    public ProductImageItem() {
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    protected ProductImageItem(Parcel in) {
        image = in.readString();
    }

    public final Creator<ProductImageItem> CREATOR = new Creator<ProductImageItem>() {
        @Override
        public ProductImageItem createFromParcel(Parcel in) {
            return new ProductImageItem(in);
        }

        @Override
        public ProductImageItem[] newArray(int size) {
            return new ProductImageItem[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
    }


}
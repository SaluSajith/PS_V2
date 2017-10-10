package com.hit.pretstreet.pretstreet.storedetails.model;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by User on 01/08/2017.
 */

public class StoreDetailsModel {

    String Id = "", StoreName = "", AreaCity = "", FollowingCount = "", Flags = "", Address = "", Latitude = "", Longitude = "", About = "",
            Products = "", ImageSource = "", TimingToday = "", BaseImage = "", Share = "", Description = "", RegisterStatus = "";
    boolean OpenStatus = false, FollowingStatus = false, AppointmentFlag = false;
    ArrayList<String> Phone = new ArrayList<>();
    ArrayList<Testimonials> arrayListTesti = new ArrayList<>();
    ArrayList<String> arrayListTimings = new ArrayList<>();
    ArrayList<String> arrayListImages = new ArrayList<>();

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getBaseImage() {
        return BaseImage;
    }

    public void setBaseImage(String baseImage) {
        BaseImage = baseImage;
    }

    public boolean getOpenStatus(){
        return OpenStatus;
    }

    public void setOpenStatus(boolean openStatus) {
        OpenStatus = openStatus;
    }

    public String getTimingToday() {
        return TimingToday;
    }

    public void setTimingToday(String timingToday) {
        TimingToday = timingToday;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public ArrayList<String> getPhone() {
        return Phone;
    }

    public void setPhone(ArrayList<String> phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAreaCity() {
        return AreaCity;
    }

    public void setAreaCity(String areaCity) {
        AreaCity = areaCity;
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

    public boolean getFollowingStatus() {
        return FollowingStatus;
    }

    public void setFollowingStatus(boolean followingStatus) {
        FollowingStatus = followingStatus;
    }

    public String getImageSource() {
        return ImageSource;
    }

    public void setImageSource(String imageSource) {
        ImageSource = imageSource;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getProducts() {
        return Products;
    }

    public void setProducts(String products) {
        Products = products;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public ArrayList<Testimonials> getArrayListTesti() {
        return arrayListTesti;
    }

    public void setArrayListTesti(ArrayList<Testimonials> arrayListTesti) {
        this.arrayListTesti = arrayListTesti;
    }

    public ArrayList<String> getArrayListTimings() {
        return arrayListTimings;
    }

    public void setArrayListTimings(ArrayList<String> arrayListTimings) {
        this.arrayListTimings = arrayListTimings;
    }

    public ArrayList<String> getArrayListImages() {
        return arrayListImages;
    }

    public void setArrayListImages(ArrayList<String> arrayListImages) {
        this.arrayListImages = arrayListImages;
    }

    public void setShare(String share) {
        Share = share;
    }

    public String getShare() {
        return Share;
    }

    public void setRegisterStatus(String registerStatus) {
        RegisterStatus = registerStatus;
    }

    public String getRegisterStatus() {
        return RegisterStatus;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setAppointmentFlag(boolean appointmentFlag) {
        AppointmentFlag = appointmentFlag;
    }

    public boolean getAppointmentFlag() {
        return AppointmentFlag;
    }
}

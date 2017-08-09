package com.hit.pretstreet.pretstreet.navigation.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.storedetails.model.StoreDetailsModel;
import com.hit.pretstreet.pretstreet.storedetails.model.Testimonials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 07/08/2017.
 */

public class DetailsPageController {
    private static Context context;

    public DetailsPageController(Context context) {
        this.context = context;
    }

    public static JSONObject getExhibitionArticle(String prepage, String clicktype, String exid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("Id", exid);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getTrendingArticle(String prepage, String clicktype, String exid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("Id", exid);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static StoreDetailsModel getExhibitionData(JSONObject response){

        StoreDetailsModel storeDetailsModel = new StoreDetailsModel();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");

            storeDetailsModel.setId(jsonObject.getString("ExhibitionArticleId"));
            storeDetailsModel.setStoreName(jsonObject.getString("ExhibitionArticleName"));
            storeDetailsModel.setAreaCity(jsonObject.getString("AreaCity"));
            //storeDetailsModel.setFlags(jsonObject.getString("Flags"));
            storeDetailsModel.setAddress(jsonObject.getString("Address"));
            storeDetailsModel.setBaseImage(jsonObject.getString("BaseImage"));
            storeDetailsModel.setLatitude(jsonObject.getString("Latitude"));
            storeDetailsModel.setLongitude(jsonObject.getString("Longitude"));
            storeDetailsModel.setAbout(jsonObject.getString("About"));
            storeDetailsModel.setProducts(jsonObject.getString("Products"));
            storeDetailsModel.setImageSource(jsonObject.getString("ImageSource"));
            storeDetailsModel.setTimingToday(jsonObject.getString("TimingToday"));
            storeDetailsModel.setOpenStatus(jsonObject.getInt("OpenStatus") == 0 ? true : false);

            JSONArray jsonArray = jsonObject.getJSONArray("Timings");
            ArrayList listArray = new ArrayList();
            for(int i=0;i<jsonArray.length();i++) {
                listArray.add(jsonArray.get(i));
            }
            storeDetailsModel.setArrayListTimings(listArray);

            listArray = new ArrayList();
            jsonArray = jsonObject.getJSONArray("Phone");
            for(int i=0;i<jsonArray.length();i++) {
                listArray.add(jsonArray.get(i));
            }
            storeDetailsModel.setPhone(listArray);

            jsonArray = jsonObject.getJSONArray("Images");
            ArrayList<String> listArrayImages = new ArrayList();
            for(int i=0;i<jsonArray.length();i++) {
                /*imageModel = new ImageModel();
                imageModel.setImgSrc(jsonArray.get(i)+"");*/
                listArrayImages.add(jsonArray.get(i)+"");
            }
            storeDetailsModel.setArrayListImages(listArrayImages);

        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return storeDetailsModel;
    }

    public static ArrayList<TrendingItems> getTrendingArticle(JSONObject response){

        ArrayList<TrendingItems> itemsArrayList = new ArrayList<>();
        TrendingItems trendingItems;
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("TrendingArticleContent");
            for(int i = 0;i<jsonArray.length();i++){
                trendingItems = new TrendingItems();
                trendingItems.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                trendingItems.setLogoImage(jsonArray.getJSONObject(i).getString("ImageSource"));
                trendingItems.setArticle(jsonArray.getJSONObject(i).getString("Description"));
                itemsArrayList.add(trendingItems);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return itemsArrayList;
    }

    public static String getTitle(JSONObject response){

        String title = "";
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            title = jsonObject.getString("TrendingArticleName");

        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return title;
    }


    public static boolean getLikeStatus(JSONObject response){

        boolean like = false;
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            like = (jsonObject.getInt("CustomerLikeStatus") == 0 ? true : false);

        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return like;
    }

}
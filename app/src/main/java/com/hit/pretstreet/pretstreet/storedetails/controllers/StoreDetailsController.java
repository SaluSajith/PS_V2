package com.hit.pretstreet.pretstreet.storedetails.controllers;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.storedetails.model.ImageModel;
import com.hit.pretstreet.pretstreet.storedetails.model.StoreDetailsModel;
import com.hit.pretstreet.pretstreet.storedetails.model.Testimonials;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 01/08/2017.
 */

public class StoreDetailsController {
    private static Context context;

    public StoreDetailsController(Context context){
        this.context = context;
    }

    public static JSONObject getShopDetailsJson(String storeId, String prePage, String clickid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("PreviousPageTypeId", prePage);
            jsonBody.put("ClickTypeId", clickid);
            jsonBody.put("Id", storeId);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }


    public static JSONObject getBookAppoJson(String dateNtime, String StoreId, String remarks) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("AppointmentDate", dateNtime);
            jsonBody.put("StoreId", StoreId);
            jsonBody.put("Message", remarks);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getReportErrorJson(String StoreId, String remarks) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("StoreId", StoreId);
            jsonBody.put("Message", remarks);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getLogTrackJson(String clicktype, String catId, String prepage, String Storeid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("CategoryId", catId);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("StoreId", Storeid);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }
    public static StoreDetailsModel getStoreData(JSONObject response){

        StoreDetailsModel storeDetailsModel = new StoreDetailsModel();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            storeDetailsModel.setStoreName(jsonObject.getString("StoreName"));
            storeDetailsModel.setAreaCity(jsonObject.getString("AreaCity"));
            storeDetailsModel.setFollowingCount(jsonObject.getString("FollowingCount"));
            storeDetailsModel.setFlags(jsonObject.getString("Flags"));
            storeDetailsModel.setAddress(jsonObject.getString("Address"));
            storeDetailsModel.setLatitude(jsonObject.getString("Latitude"));
            storeDetailsModel.setLongitude(jsonObject.getString("Longitude"));
            storeDetailsModel.setAbout(jsonObject.getString("About"));
            storeDetailsModel.setProducts(jsonObject.getString("Products"));
            storeDetailsModel.setImageSource(jsonObject.getString("ImageSource"));
            storeDetailsModel.setTimingToday(jsonObject.getString("TimingToday"));
            storeDetailsModel.setBaseImage(jsonObject.getString("BaseImage"));
            storeDetailsModel.setShare(jsonObject.getString("Share"));
            storeDetailsModel.setDescription(jsonObject.getJSONObject("ShortDescritpion").getString("Html"));
            storeDetailsModel.setAppointmentFlag(jsonObject.getInt("AppointmentFlag") == 0 ? false : true);
            storeDetailsModel.setOpenStatus(jsonObject.getInt("OpenStatus") == 0 ? true : false);
            storeDetailsModel.setFollowingStatus(jsonObject.getInt("FollowingStatus") == 0 ? true : false);

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

            jsonArray = jsonObject.getJSONArray("Testimonials");
            ArrayList<Testimonials> listTestArray = new ArrayList();
            Testimonials testimonials;
            for(int i=0;i<jsonArray.length();i++) {
                testimonials = new Testimonials();
                testimonials.setTestimonial(jsonArray.getJSONObject(i).getString("testimonial"));
                testimonials.setName(jsonArray.getJSONObject(i).getString("name"));
                listTestArray.add(testimonials);
            }
            storeDetailsModel.setArrayListTesti(listTestArray);

        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return storeDetailsModel;
    }
}

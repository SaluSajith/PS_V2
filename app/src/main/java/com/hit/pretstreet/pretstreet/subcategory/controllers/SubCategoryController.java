package com.hit.pretstreet.pretstreet.subcategory.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.subcategory.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 7/27/2017.
 */

public class SubCategoryController {
    private static LoginCallbackInterface loginCallbackInterface;
    private static Context context;


    public SubCategoryController(LoginCallbackInterface loginCallbackInterface, Context context){
        this.loginCallbackInterface = loginCallbackInterface;
        this.context = context;
    }

    public static JSONObject getShoplistJson(String catId, String limit, String offset, String filter) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("CategoryId", catId);
            jsonBody.put("Limit", limit);
            jsonBody.put("Offset", offset);
            jsonBody.put("Filter", filter);
            jsonBody.put("Latitude", PreferenceServices.getInstance().getLatitute().equalsIgnoreCase("0.0"));
            jsonBody.put("Longitude", PreferenceServices.getInstance().getLongitute().equalsIgnoreCase("0.0"));
            jsonBody.put("ApiKey", Constant.API);
            jsonBody.put("DeviceType", "1");
            jsonBody.put("DeviceId", PretStreet.getDeviceId());

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static ArrayList <StoreListModel> getList(JSONObject response) {
        ArrayList<StoreListModel> storeListModels = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            StoreListModel storeListModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                storeListModel = new StoreListModel();
                storeListModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                storeListModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                storeListModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                storeListModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                storeListModel.setFollowingStatus(jsonArray.getJSONObject(i).getString("FollowingStatus"));
                storeListModel.setFollowingCount(jsonArray.getJSONObject(i).getString("FollowingCount"));
                storeListModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));
                storeListModel.setImageSource(jsonArray.getJSONObject(i).getString("ImageSource"));
                storeListModel.setOpenStatus(jsonArray.getJSONObject(i).getString("OpenStatus"));
                storeListModel.setFlags(jsonArray.getJSONObject(i).getString("Flags"));
                storeListModel.setBannerFlag(jsonArray.getJSONObject(i).getString("BannerFlag"));

                storeListModels.add(storeListModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  storeListModels;
    }
}

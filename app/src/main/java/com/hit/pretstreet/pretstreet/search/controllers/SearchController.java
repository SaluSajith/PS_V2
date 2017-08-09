package com.hit.pretstreet.pretstreet.search.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.storedetails.model.StoreDetailsModel;
import com.hit.pretstreet.pretstreet.storedetails.model.Testimonials;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 08/08/2017.
 */

public class SearchController {
    private static Context context;

    public SearchController(Context context) {
        this.context = context;
    }

    public static JSONObject getShopListJson(String catId, String prePage, String clicktype, String Id) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("CategoryId", catId);
            jsonBody.put("PreviousPageTypeId", prePage);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("Id", Id);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static ArrayList <StoreListModel> getList(JSONObject response) {
        ArrayList<StoreListModel> storeListModels = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("StoreListing");
            StoreListModel storeListModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                storeListModel = new StoreListModel();
                storeListModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                storeListModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                storeListModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                storeListModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                if(jsonArray.getJSONObject(i).getString("FollowingStatus").contains("0"))
                    storeListModel.setFollowingStatus(false);
                else
                    storeListModel.setFollowingStatus(true);
                if(jsonArray.getJSONObject(i).getString("OpenStatus").contains("0"))
                    storeListModel.setOpenStatus(false);
                else
                    storeListModel.setOpenStatus(true);
                storeListModel.setFollowingCount(jsonArray.getJSONObject(i).getString("FollowingCount"));
                storeListModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));
                storeListModel.setImageSource(jsonArray.getJSONObject(i).getString("ImageSource"));
                String flag = jsonArray.getJSONObject(i).getString("Flags");
                if(flag.contains("0")){
                    storeListModel.setSaleflag(true);
                }
                else{
                    storeListModel.setSaleflag(false);
                }
                if(flag.contains("1")){
                    storeListModel.setOfferflag(true);
                }
                else{
                    storeListModel.setOfferflag(false);
                }
                if(flag.contains("2")){
                    storeListModel.setNewflag(true);
                }
                else{
                    storeListModel.setNewflag(false);
                }
                storeListModel.setBannerFlag(jsonArray.getJSONObject(i).getString("BannerFlag"));

                storeListModels.add(storeListModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  storeListModels;
    }
}
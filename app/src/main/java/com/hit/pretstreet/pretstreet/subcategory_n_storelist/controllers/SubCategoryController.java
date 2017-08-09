package com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.PretStreet;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 7/27/2017.
 */

public class SubCategoryController {
    private static Context context;

    public SubCategoryController(Context context){
        this.context = context;
    }

    public static JSONObject getShoplistJson(String catId, String offset, String filter, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("CategoryId", catId);
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);
            jsonBody.put("Filter", filter);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject updateFollowCount(String storeId, String previouspageid, String clicktype) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("CategoryId", "");
            jsonBody.put("PreviousPageTypeId", previouspageid);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("StoreId", storeId);
            jsonBody = Constant.addConstants(jsonBody, context);

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

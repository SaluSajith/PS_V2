package com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;
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

    /**Create JSON object to POST for getting shoplist
     * @param catId Category id/Sub category id
     * @param offset for pagination purpose
     * @param clicktype for logging purpose
     * @param arrayFilter filter array that should be passed*/
    public static JSONObject getShoplistJson(String catId, String offset, String prepage, String clicktype, JSONArray arrayFilter) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", catId);
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("Filter", arrayFilter);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    /**Get default filter JSON object*/
    public static JSONObject getFilterJson(String id, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", id);
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
            jsonBody.put("PreviousPageTypeId", previouspageid);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("StoreId", storeId);
            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    /**To create json array from two dimensional array*/
    public static JSONArray createFilterModel(ArrayList<TwoLevelDataModel> filterDataModels){

        JSONArray jsonArray1 = new JSONArray();
        StringBuilder stringBuilder;
        try {
            for(int i=0;i<filterDataModels.size();i++){
                TwoLevelDataModel filterDataModel = filterDataModels.get(i);
                JSONObject jsonBody = new JSONObject();
                stringBuilder = new StringBuilder();
                JSONArray jsonArray = new JSONArray();

                for(int j=0;j<filterDataModel.getAllItemsInSection().size();j++){
                    if(filterDataModel.getAllItemsInSection().get(j).getStatus())
                        stringBuilder.append(filterDataModel.getAllItemsInSection().get(j).getId() + ", ");
                }

                jsonArray.put(stringBuilder.toString().replaceAll(", $", ""));
                if(stringBuilder.toString().length()>0) {
                    jsonBody.put(filterDataModel.getHeaderTitle(), jsonArray);
                    jsonArray1.put(i, jsonBody);
                }
            }
            //jsonObject.put("Filter", jsonArray1);
        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonArray1;
    }

    /**To get arraylist from JSON response from server*/
    public static ArrayList <StoreListModel> getList(JSONObject response){
        final ArrayList<StoreListModel> storeListModels = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            StoreListModel storeListModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                storeListModel = new StoreListModel();
                storeListModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                storeListModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                storeListModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                storeListModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                storeListModel.setFollowingStatus(jsonArray.getJSONObject(i).getInt("FollowingStatus") == 0 ? false : true);
                storeListModel.setOpenStatus(jsonArray.getJSONObject(i).getInt("OpenStatus") == 1 ? false : true);
                storeListModel.setFollowingCount(jsonArray.getJSONObject(i).getString("FollowingCount"));
                storeListModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));
                storeListModel.setImageSource(jsonArray.getJSONObject(i).getString("ImageSource"));
                String flag = jsonArray.getJSONObject(i).getString("Flags");
                storeListModel.setSaleflag(flag.contains("0")==true ? true : false);
                storeListModel.setOfferflag(flag.contains("1")==true ? true : false);
                storeListModel.setNewflag(flag.contains("2")==true ? true : false);
                storeListModel.setBannerFlag(jsonArray.getJSONObject(i).getInt("BannerFlag") == 0 ? false : true);

                storeListModels.add(storeListModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return  storeListModels;
    }

    /**Get filter array from response*/
    public static ArrayList <TwoLevelDataModel> getFilterList(JSONObject response){
        ArrayList<TwoLevelDataModel> filterDataModels = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            TwoLevelDataModel filterDataModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                filterDataModel = new TwoLevelDataModel();
                filterDataModel.setHeaderTitle(jsonArray.getJSONObject(i).getString("Title"));
                ArrayList<BasicModel> singleItem = new ArrayList<>();
                BasicModel model;
                JSONArray array = jsonArray.getJSONObject(i).getJSONArray("Options");
                for (int j = 0; j < array.length(); j++) {
                    model = new BasicModel();
                    model.setId(array.getJSONObject(j).getString("Code"));
                    model.setCategory(array.getJSONObject(j).getString("Title"));
                    model.setStatus(false);
                    singleItem.add(model);
                }
                filterDataModel.setAllItemsInSection(singleItem);
                filterDataModels.add(filterDataModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return  filterDataModels;
    }
}

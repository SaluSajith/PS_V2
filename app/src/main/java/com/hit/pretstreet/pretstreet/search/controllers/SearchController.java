package com.hit.pretstreet.pretstreet.search.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
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

    public static JSONObject getMultiStoreListJson(String Id, String prePage, String clicktype, int offset) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("PreviousPageTypeId", prePage);
            jsonBody.put("ClickTypeId", clicktype);
            jsonBody.put("Id", Id);
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getAutoSearchListJson(String searchWord, String prePage, String catId, String cattype) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", catId);
            jsonBody.put("Searchfor", cattype);
            jsonBody.put("PreviousPageTypeId", prePage);
            jsonBody.put("ClickTypeId", Constant.AUTOSEARCHLINK);
            jsonBody.put("Searchword", searchWord);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getRecentSearchListJson(String Id, String prePage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", Id);
            jsonBody.put("PreviousPageTypeId", prePage);
            jsonBody.put("ClickTypeId", Constant.AUTOSEARCHLINK);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }


    public static JSONObject getSearchResultJson(String searchWord, String prePage, String catId,
                                                 int offset, String cattype, JSONArray arrayFilter) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", catId);
            jsonBody.put("Searchfor", cattype);
            jsonBody.put("PreviousPageTypeId", prePage);
            jsonBody.put("ClickTypeId", "");
            jsonBody.put("Filter", arrayFilter);
            jsonBody.put("Searchword", searchWord);
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static ArrayList <StoreListModel> getList(JSONObject response){
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


    public static ArrayList <BasicModel> getAutoSearchList(JSONObject response){
        ArrayList<BasicModel> searchModels = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            BasicModel searchModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                searchModel = new BasicModel();
                searchModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                searchModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                searchModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                searchModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                searchModel.setCategory(jsonArray.getJSONObject(i).getString("Category"));
                searchModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));

                searchModels.add(searchModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  searchModels;
    }


    public static ArrayList <BasicModel> getRecentViewList(JSONObject response){
        ArrayList<BasicModel> searchModels = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("RecentViewed");
            BasicModel searchModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                searchModel = new BasicModel();
                searchModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                searchModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                searchModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                searchModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                searchModel.setCategory(jsonArray.getJSONObject(i).getString("Category"));
                searchModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));

                searchModels.add(searchModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  searchModels;
    }

    public static ArrayList <BasicModel> getRecentSearchList(JSONObject response){
        ArrayList<BasicModel> searchModels = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("RecentSearched");
            BasicModel searchModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                searchModel = new BasicModel();
                searchModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                searchModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                searchModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                searchModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                searchModel.setCategory(jsonArray.getJSONObject(i).getString("Category"));
                searchModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));

                searchModels.add(searchModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  searchModels;
    }

    public static ArrayList <BasicModel> getCategoryList(JSONObject response){
        ArrayList<BasicModel> searchModels = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("Category");
            BasicModel searchModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                searchModel = new BasicModel();
                searchModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                searchModel.setCategory(jsonArray.getJSONObject(i).getString("CategoryName"));
                searchModels.add(searchModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return  searchModels;
    }

    public static ArrayList <StoreListModel> getSearchList(JSONObject response){
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
                storeListModel.setFollowingStatus(jsonArray.getJSONObject(i).getInt("FollowingStatus") ==0 ? false : true);
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
}
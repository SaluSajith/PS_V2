package com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.FilterDataModel;
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

    public static JSONObject getShoplistJson(String catId, String offset, String prepage, JSONArray arrayFilter) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", catId);
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");
            jsonBody.put("Filter", arrayFilter);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }


    public static JSONObject getFilterJson(String catId, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", catId);
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

    public static JSONArray createFilterModel(ArrayList<FilterDataModel> filterDataModels){

        JSONArray jsonArray1 = new JSONArray();
        StringBuilder stringBuilder;
        try {
            for(int i=0;i<filterDataModels.size();i++){
                FilterDataModel filterDataModel = filterDataModels.get(i);
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
/*
    public static ArrayList <StoreListModel> getList(JSONObject response){
        final ArrayList<StoreListModel> storeListModels = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(String.valueOf(response));
            ArrayNode dataNode = (ArrayNode)rootNode.get("Data");
            Iterator<JsonNode> dataIterator = dataNode.elements();
            StoreListModel storeListModel;
            while (dataIterator.hasNext()) {
                storeListModel = new StoreListModel();
                JsonNode storeNode = dataIterator.next();
                storeListModel.setPageType(storeNode.get("PageType").textValue());
                storeListModel.setPageTypeId(storeNode.get("PageTypeId").asInt()+"");
                storeListModel.setId(storeNode.get("Id").asInt()+"");
                storeListModel.setTitle(storeNode.get("Title").textValue());
                storeListModel.setFollowingStatus(storeNode.get("FollowingStatus").asInt() == 0 ? false : true);
                storeListModel.setOpenStatus(storeNode.get("OpenStatus").asInt() == 1 ? false : true);
                storeListModel.setFollowingCount(storeNode.get("FollowingCount").asInt()+"");
                storeListModel.setLocation(storeNode.get("Location").textValue());
                storeListModel.setImageSource(storeNode.get("ImageSource").textValue());
                String flag = storeNode.get("Flags").textValue();
                if(flag.contains("0"))
                    storeListModel.setSaleflag(true);
                else
                    storeListModel.setSaleflag(false);
                if(flag.contains("1"))
                    storeListModel.setOfferflag(true);
                else
                    storeListModel.setOfferflag(false);
                if(flag.contains("2"))
                    storeListModel.setNewflag(true);
                else
                    storeListModel.setNewflag(false);
                storeListModel.setBannerFlag(storeNode.get("BannerFlag").asInt() == 0 ? false : true);
                storeListModel.setLoadmoreFlag(dataIterator.hasNext() ? false : true);

                storeListModels.add(storeListModel);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  storeListModels;
    }*/

    public static ArrayList <FilterDataModel> getFilterList(JSONObject response){
        ArrayList<FilterDataModel> filterDataModels = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            FilterDataModel filterDataModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                filterDataModel = new FilterDataModel();
                filterDataModel.setHeaderTitle(jsonArray.getJSONObject(i).getString("Title"));
                ArrayList<SearchModel> singleItem = new ArrayList<>();
                SearchModel model;
                JSONArray array = jsonArray.getJSONObject(i).getJSONArray("Options");
                for (int j = 0; j < array.length(); j++) {
                    model = new SearchModel();
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

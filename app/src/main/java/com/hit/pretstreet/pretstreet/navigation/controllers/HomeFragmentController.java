package com.hit.pretstreet.pretstreet.navigation.controllers;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 03/08/2017.
 */

public class HomeFragmentController {
    private static Context context;

    public HomeFragmentController(Context context) {
        this.context = context;
    }

    public static JSONObject getTrendinglistJson(String offset, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            /*jsonBody.put("CategoryId", catId);
            jsonBody.put("Limit", limit);
            jsonBody.put("Offset", offset);
            jsonBody.put("Filter", filter);*/
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getExhibitionlistJson(String offset, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            /*jsonBody.put("CategoryId", catId);
            jsonBody.put("Limit", limit);
            jsonBody.put("Offset", offset);
            jsonBody.put("Filter", filter);*/
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }
    public static JSONObject getTrendinglikeJson(String id, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", id);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }
    public static ArrayList<TrendingItems> getTrendingList(JSONObject response) {
        ArrayList<TrendingItems> trendingItems = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");

            TrendingItems item;
            if (trendingItems == null)
                trendingItems = new ArrayList<>();
                /*else
                list.clear();*/
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trendingContent = jsonArray.getJSONObject(i);
                item = new TrendingItems();
                item.setId(trendingContent.getString("Id"));
                item.setPagetype(trendingContent.getString("PageType"));
                item.setClicktype(trendingContent.getString("ClickTypeId"));
                item.setPagetypeid(trendingContent.getString("PageTypeId"));
                item.setStoreid(trendingContent.getString("StoreId"));
                item.setLogoImage(trendingContent.getString("LogoImage"));
                item.setTitle(trendingContent.getString("Title"));
                item.setArticle(trendingContent.getString("ArticleShortDescription"));
                item.setLike(trendingContent.getInt("CustomerLikeStatus") == 0 ? false : true);
                item.setBanner(trendingContent.getInt("BannerFlag") == 0 ? false : true);
                item.setStoreName(trendingContent.getString("Storename"));

                JSONArray jsonImagearray = trendingContent.getJSONArray("ImageSource");
                ArrayList imagearray = new ArrayList();
                for(int j=0;j<jsonImagearray.length();j++) {
                    imagearray.add(jsonImagearray.get(j));
                }
                item.setImagearray(imagearray);
                if(!trendingContent.getString("ArticleDate").equalsIgnoreCase("")) {
                    try {
                        DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                        String inputDateStr = trendingContent.getString("ArticleDate");
                        Date date = inputFormat.parse(inputDateStr);
                        String outputDateStr = outputFormat.format(date);
                        item.setArticledate(outputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else item.setArticledate("");

                trendingItems.add(item);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return trendingItems;
    }


    public static ArrayList<TrendingItems> getExhibitionList(JSONObject response) {
        ArrayList<TrendingItems> exHItems = new ArrayList<>();
        try {
            //JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = response.getJSONArray("Data");
            TrendingItems item;
            if (exHItems == null)
                exHItems = new ArrayList<>();
                /*else
                list.clear();*/
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trendingContent = jsonArray.getJSONObject(i);
                item = new TrendingItems();
                item.setId(trendingContent.getString("Id"));
                item.setPagetype(trendingContent.getString("PageType"));
                item.setClicktype(trendingContent.getString("ClickTypeId"));
                item.setPagetypeid(trendingContent.getString("PageTypeId"));
                item.setTitle(trendingContent.getString("Title"));
                item.setArticle(trendingContent.getString("ArticleShortDescription"));
                item.setLike(trendingContent.getInt("CustomerLikeStatus") == 1 ? true : false);
                item.setBanner(trendingContent.getInt("BannerFlag") == 1 ? true : false);

                JSONArray jsonImagearray = trendingContent.getJSONArray("ImageSource");
                ArrayList imagearray = new ArrayList();
                for(int j=0;j<jsonImagearray.length();j++) {
                    imagearray.add(jsonImagearray.get(j));
                }
                item.setImagearray(imagearray);
                if(!trendingContent.getString("ArticleDate").equalsIgnoreCase("")) {
                    try {
                        DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                        String inputDateStr = trendingContent.getString("ArticleDate");
                        Date date = inputFormat.parse(inputDateStr);
                        String outputDateStr = outputFormat.format(date);
                        item.setArticledate(outputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else item.setArticledate("");

                exHItems.add(item);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return exHItems;
    }
}
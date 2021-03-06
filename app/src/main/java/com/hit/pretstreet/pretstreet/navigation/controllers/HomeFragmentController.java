package com.hit.pretstreet.pretstreet.navigation.controllers;

import android.content.Context;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.GIVEAWAYPAGE;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDINGPAGE;

/**
 * Created by User on 03/08/2017.
 */

public class HomeFragmentController {
    private static Context context;

    public HomeFragmentController(Context context) {
        this.context = context;
    }

    public static JSONObject getTrendinglistJson(int offset, String prepage, String clicktypeid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT_S);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktypeid);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }


    public static JSONObject getGiveawaylistJson(int offset, String prepage, String clicktypeid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT_S);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktypeid);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getExhibitionlistJson(int offset, String prepage, String clicktypeid,
                                                   String month, String year) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Month", month);
            jsonBody.put("Year", year);
            jsonBody.put("Limit", Constant.LIMIT_S);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktypeid);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getExhibitionSeach(String str) {

        JSONObject jsonBody = new JSONObject();
        try {
           jsonBody.put("Search", str);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getExhibitionlikeJson(String clicktype, String id, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", id);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktype);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }
        return jsonBody;
    }

    public static JSONObject getOTPVerificationJson(String phone) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserMobile", phone);
            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getExhibitionRegisterJson(String clicktype, String id, String prepage, String phone) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Id", id);
            jsonBody.put("Phonenumber", phone);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", clicktype);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getRegisterJson_UpdatePhone(JSONObject jsonObject, String phone) {

        JSONObject jsonBody = jsonObject;
        try {
            jsonBody.put("Phonenumber", phone);

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

    public static ArrayList<TrendingItems> getTrendingList(JSONObject response){
        ArrayList<TrendingItems> trendingItems = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");

            TrendingItems item;
            if (trendingItems == null)
                trendingItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trendingContent = jsonArray.getJSONObject(i);
                item = new TrendingItems();
                item.setId(trendingContent.getString("Id"));
                //item.setPagetype(trendingContent.getString("PageType"));
                item.setPagetype(TRENDINGPAGE);
                item.setClicktype(trendingContent.getString("ClickTypeId"));
                item.setPagetypeid(trendingContent.getString("PageTypeId"));
                item.setStoreid(trendingContent.getString("StoreId"));
                item.setLogoImage(trendingContent.getString("LogoImage"));
                item.setTitle(trendingContent.getString("Title"));
                item.setArticle(trendingContent.getString("ArticleShortDescription"));

                JSONObject jsonObject = trendingContent.getJSONObject("TitleContent");
                item.setTitleid(jsonObject.getString("RedirectionId"));
                item.setTitlepagetype(jsonObject.getString("PageTypeId"));

                item.setLike(trendingContent.getInt("CustomerLikeStatus") == 0 ? false : true);
                item.setBanner(trendingContent.getInt("BannerFlag") == 0 ? false : true);
                item.setStoreName(trendingContent.getString("Storename"));
                //item.setImgHeight(Integer.parseInt(trendingContent.getString("MaxImageHeight")));
                //item.setImgWidth(Integer.parseInt(trendingContent.getString("MaxImageWidth")));
                JSONArray jsonImagearray = trendingContent.getJSONArray("ImageSource");
                ArrayList imagearray = new ArrayList();
                for(int j=0;j<jsonImagearray.length();j++) {
                    imagearray.add(jsonImagearray.get(j));
                }
                item.setImagearray(imagearray);
                item.setArticledate(trendingContent.getString("ArticleDate"));
                if(trendingContent.has("Share"))
                    item.setShareUrl(trendingContent.getString("Share"));
                trendingItems.add(item);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return trendingItems;
    }

    public static String getHeading(JSONObject response){
        String s = "Giveaway";
        try {
            s = response.getString("Heading");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static ArrayList<TrendingItems> getGiveawayList(JSONObject response){
        ArrayList<TrendingItems> trendingItems = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");

            TrendingItems item;
            if (trendingItems == null)
                trendingItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trendingContent = jsonArray.getJSONObject(i);
                item = new TrendingItems();
                item.setId(trendingContent.getString("Id"));
                //item.setPagetype(trendingContent.getString("PageType"));
                item.setPagetype(GIVEAWAYPAGE);
                item.setClicktype(trendingContent.getString("ClickTypeId"));
                item.setPagetypeid(trendingContent.getString("PageTypeId"));
                item.setStoreid(trendingContent.getString("StoreId"));
                item.setLogoImage(trendingContent.getString("LogoImage"));
                item.setTitle(trendingContent.getString("Title"));
                item.setArticle(trendingContent.getString("ArticleShortDescription"));

                JSONObject jsonObject = trendingContent.getJSONObject("TitleContent");
                item.setTitleid(jsonObject.getString("RedirectionId"));
                item.setTitlepagetype(jsonObject.getString("PageTypeId"));

                item.setLike(trendingContent.getInt("CustomerLikeStatus") == 0 ? false : true);
                item.setBanner(trendingContent.getInt("BannerFlag") == 0 ? false : true);
                item.setStoreName(trendingContent.getString("Storename"));
                //item.setImgHeight(Integer.parseInt(trendingContent.getString("MaxImageHeight")));
                //item.setImgWidth(Integer.parseInt(trendingContent.getString("MaxImageWidth")));
                JSONArray jsonImagearray = trendingContent.getJSONArray("ImageSource");
                ArrayList imagearray = new ArrayList();
                for(int j=0;j<jsonImagearray.length();j++) {
                    imagearray.add(jsonImagearray.get(j));
                }
                item.setImagearray(imagearray);
                item.setArticledate(trendingContent.getString("ArticleDate"));
                if(trendingContent.has("Share"))
                    item.setShareUrl(trendingContent.getString("Share"));
                trendingItems.add(item);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return trendingItems;
    }

    public static ArrayList<BasicModel> getExhibitionSearchList(JSONObject response){
        ArrayList<BasicModel> exHItems = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            BasicModel item;
            if (exHItems == null)
                exHItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject searchContent = jsonArray.getJSONObject(i);
                item = new BasicModel();
                item.setId(searchContent.getString("Id"));
                item.setPageType(searchContent.getString("PageType"));
                item.setPageTypeId(searchContent.getString("PageTypeId"));
                item.setTitle(searchContent.getString("Title"));

                exHItems.add(item);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return exHItems;
    }
    public static ArrayList getExhibitionSearchNameList(JSONObject response){
        ArrayList exHItems = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            if (exHItems == null)
                exHItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject searchContent = jsonArray.getJSONObject(i);
                exHItems.add(searchContent.getString("Title"));
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return exHItems;
    }

    public static ArrayList<TrendingItems> getExhibitionList(JSONObject response){
        ArrayList<TrendingItems> exHItems = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("Data");
            TrendingItems item;
            if (exHItems == null)
                exHItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trendingContent = jsonArray.getJSONObject(i);
                item = new TrendingItems();
                item.setId(trendingContent.getString("Id"));
                //item.setPagetype(trendingContent.getString("PageType"));
                item.setPagetype(EXHIBITIONPAGE);
                item.setClicktype(trendingContent.getString("ClickTypeId"));
                item.setPagetypeid(trendingContent.getString("PageTypeId"));
                item.setTitle(trendingContent.getString("Title"));
                item.setArticle(trendingContent.getString("ArticleShortDescription"));
                item.setShareUrl(trendingContent.getString("Share"));
                item.setArea(trendingContent.getString("AreaCity"));
                item.setLike(trendingContent.getInt("CustomerLikeStatus") == 1 ? true : false);
                item.setBanner(trendingContent.getInt("BannerFlag") == 1 ? true : false);
                item.setRegister(trendingContent.getString("CustomerRegisterStatus"));

                String jsonImage = trendingContent.getString("ImageSource");

                ArrayList imagearray = new ArrayList();
                imagearray.add(jsonImage);
                item.setImagearray(imagearray);
                item.setArticledate(trendingContent.getString("ArticleDate"));

                exHItems.add(item);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return exHItems;
    }
}
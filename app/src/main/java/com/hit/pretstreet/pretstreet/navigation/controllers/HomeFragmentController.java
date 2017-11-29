package com.hit.pretstreet.pretstreet.navigation.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    public static JSONObject getTrendinglistJson(int offset, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT_S);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }


    public static JSONObject getGiveawaylistJson(int offset, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT_S);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getExhibitionlistJson(int offset, String prepage) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT_S);
            jsonBody.put("Offset", offset);
            jsonBody.put("PreviousPageTypeId", prepage);
            jsonBody.put("ClickTypeId", "");

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
                item.setImgHeight(Integer.parseInt(trendingContent.getString("MaxImageHeight")));
                item.setImgWidth(Integer.parseInt(trendingContent.getString("MaxImageWidth")));
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
                item.setImgHeight(Integer.parseInt(trendingContent.getString("MaxImageHeight")));
                item.setImgWidth(Integer.parseInt(trendingContent.getString("MaxImageWidth")));
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
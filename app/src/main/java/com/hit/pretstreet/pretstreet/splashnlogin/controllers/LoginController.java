package com.hit.pretstreet.pretstreet.splashnlogin.controllers;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatItems;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.SHOPBYMOODS;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SHOPBYPRO;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.SLIDER;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRAPE;

/**
 * Created by User on 7/4/2017.
 */

public class LoginController {

    private static final int PROFILE_PIC_SIZE = 400;
    private static LoginCallbackInterface loginCallbackInterface;
    private static Context context;

    private static int SIGNUP = 0;
    private static int LOGIN = 1;

    public LoginController(LoginCallbackInterface loginCallbackInterface, Context context){
        this.loginCallbackInterface = loginCallbackInterface;
        this.context = context;
    }
    public LoginController(Context context){
        this.context = context;
        PreferenceServices.init(context);
    }

    public static JSONObject getFacebookLoginData(JSONObject jsonObject) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserSocialId", URLEncoder.encode(jsonObject.getString("id").toString(), "UTF-8"));
            jsonBody.put("UserSocialType", "facebook");
            jsonBody.put("ProfileImage", URLEncoder.encode("https://graph.facebook.com/" +
                    jsonObject.getString("id").toString() + "/picture?type=large", "UTF-8"));
            if (jsonObject.has("email"))
                jsonBody.put("UserEmail", jsonObject.getString("email").toString());
            else jsonBody.put("UserEmail", "");

            if (jsonObject.has("first_name"))
                jsonBody.put("FirstName", URLEncoder.encode(jsonObject.getString("first_name").toString(), "UTF-8"));
            else jsonBody.put("FirstName", "");

            if (jsonObject.has("last_name"))
                jsonBody.put("LastName", URLEncoder.encode(jsonObject.getString("last_name").toString(), "UTF-8"));
            else jsonBody.put("LastName", "");

            if (jsonObject.has("gender"))
                jsonBody.put("gender", jsonObject.getString("gender").toString());
            else jsonBody.put("gender", "");

            String utmparam = PreferenceServices.getInstance().getUTMQueryparam();
            jsonBody.put("utm_url", utmparam);

            jsonBody = Constant.addConstants(jsonBody, context);
            jsonBody = Constant.addDeviceId(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getGoogleLoginDetails(GoogleSignInAccount account) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserSocialId", URLEncoder.encode(account.getId(), "UTF-8"));
            jsonBody.put("UserSocialType", "google");
            //jsonBody.put("ProfileImage", googleImageUrl.substring(0, googleImageUrl.length() - 2) + PROFILE_PIC_SIZE);
            jsonBody.put("ProfileImage", account.getPhotoUrl());
            jsonBody.put("UserEmail", account.getEmail());
            jsonBody.put("FirstName", account.getGivenName());
            jsonBody.put("LastName", account.getFamilyName());

            String utmparam = PreferenceServices.getInstance().getUTMQueryparam();
            jsonBody.put("utm_url", utmparam);

            jsonBody = Constant.addConstants(jsonBody, context);
            jsonBody = Constant.addDeviceId(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getNormalLoginDetails(LoginSession loginSession) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("FirstName", loginSession.getFname());
            jsonBody.put("LastName", loginSession.getLname());
            jsonBody.put("UserEmail", loginSession.getEmail());
            jsonBody.put("UserMobile", loginSession.getMobile());
            jsonBody.put("UserPassword", loginSession.getPassword());

            String utmparam = PreferenceServices.getInstance().getUTMQueryparam();
            jsonBody.put("utm_url", utmparam);

            jsonBody = Constant.addConstants(jsonBody, context);
            jsonBody = Constant.addDeviceId(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getOTPVerificationJson(String phone, String email) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserMobile", phone);
            jsonBody.put("UserEmail", email);

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static JSONObject getHomePageJson() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("PreviousPageTypeId", "");
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }


    public static JSONObject getSubCatPageJson(String prePageid, String catId) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("PreviousPageTypeId", prePageid);
            jsonBody.put("CategoryId", catId);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {}

        return jsonBody;
    }

    public static void validateLoginFields(EdittextPret et_number) {
        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        String number = et_number.getText().toString();
        if (number.length() < 1) {
            edittextPret = et_number;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, LOGIN);
            return;
        }  else if (!Utility.validCellPhone(number)) {
            edittextPret = et_number;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, LOGIN);
            return;
        } else {
            loginCallbackInterface.validationSuccess(number);
        }
    }

    public static void validateRegisterFields(EdittextPret edt_firstname,
                                              EdittextPret edt_lastname,
                                              EdittextPret edt_mobile,
                                              EdittextPret edt_email,
                                              EdittextPret edt_password) {

        String firstname = edt_firstname.getText().toString().trim();
        String lname = edt_lastname.getText().toString().trim();
        String mobile = edt_mobile.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        if (firstname.length() < 1) {
            edittextPret = edt_firstname;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        } else if (lname.length() < 1) {
            edittextPret = edt_lastname;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (!Utility.validCellPhone(mobile)) {
            edittextPret = edt_mobile;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (email.length() < 1) {
            edittextPret = edt_email;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (!Utility.isValidEmail(email)) {
            edittextPret = edt_email;
            message = "Invalid email";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        }  else if (password.length() < 6) {
            edittextPret = edt_password;
            message = "Password length should be more than 6";
            loginCallbackInterface.validateCallback(edittextPret, message, SIGNUP);
            return;
        } else {
            LoginSession loginSession = new LoginSession();
            loginSession.setFname(firstname);
            loginSession.setLname(lname);
            loginSession.setMobile(mobile);
            loginSession.setEmail(email);
            loginSession.setPassword(password);
            loginCallbackInterface.validationSuccess(loginSession);
        }
    }

    public static ArrayList<HomeCatItems> getHomeContent(String SavedMAinCaTList){
        final ArrayList<HomeCatItems> list = new ArrayList<>();
        final ArrayList<BasicModel> navCatList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(SavedMAinCaTList);
            JSONArray jsonArray = response.getJSONArray("Data");
            HomeCatItems homeCatItems;
            BasicModel navCatModel;
            for (int i = 0; i < jsonArray.length(); i++) {

                homeCatItems = new HomeCatItems();
                homeCatItems.setContentTypeId(jsonArray.getJSONObject(i).getString("ContentTypeId"));
                homeCatItems.setContentType(jsonArray.getJSONObject(i).getString("ContentType"));
                String contentTypeId = jsonArray.getJSONObject(i).getString("ContentTypeId");
                switch (contentTypeId) {
                    case TRAPE:
                        JSONObject object = jsonArray.getJSONObject(i).getJSONObject("ContentData");

                        HomeCatContentData homeContentData = new HomeCatContentData();
                        homeContentData.setCategoryId(object.getString("MainCategoryId"));
                        homeContentData.setCategoryName(object.getString("CategoryName"));
                        homeContentData.setImageSource(object.getString("ImageSource"));
                        homeContentData.setPageTypeId(object.getString("PageTypeId"));
                        homeCatItems.setHomeContentData(homeContentData);


                        /* Saving list to show it in menu */
                        navCatModel = new BasicModel();
                        navCatModel.setId(object.getString("MainCategoryId"));
                        navCatModel.setCategory(object.getString("CategoryName"));
                        navCatModel.setPageTypeId(object.getString("PageTypeId"));
                        navCatList.add(navCatModel);

                        break;
                    case SHOPBYMOODS:
                        JSONArray proContent = jsonArray.getJSONObject(i).getJSONArray("ContentData");
                        homeContentData = new HomeCatContentData();

                        ArrayList<HomeCatContentData> homeSubCategoriesArray = new ArrayList<>();
                        for (int k = 0; k < proContent.length(); k++) {

                            JSONObject data = proContent.getJSONObject(k);
                            HomeCatContentData contentData = new HomeCatContentData();
                            contentData.setCategoryId(data.getString("SubCategoryId"));
                            contentData.setCategoryName(data.getString("CategoryName"));
                            contentData.setTitle(data.getString("Title"));
                            contentData.setImageSource(data.getString("ImageSource"));
                            contentData.setPageType(data.getString("PageType"));
                            contentData.setPageTypeId(data.getString("PageTypeId"));

                            homeSubCategoriesArray.add(contentData);
                        }
                        homeContentData.setHomeCatContentDatas(homeSubCategoriesArray);
                        homeCatItems.setHomeContentData(homeContentData);
                        break;
                    case SHOPBYPRO:
                        proContent = jsonArray.getJSONObject(i).getJSONArray("ContentData");
                        homeContentData = new HomeCatContentData();

                        homeSubCategoriesArray = new ArrayList<>();
                        for (int k = 0; k < proContent.length(); k++) {

                            JSONObject data = proContent.getJSONObject(k);
                            HomeCatContentData contentData = new HomeCatContentData();
                            contentData.setCategoryId(data.getString("SubCategoryId"));
                            contentData.setCategoryName(data.getString("CategoryName"));
                            contentData.setTitle(data.getString("Title"));
                            contentData.setImageSource(data.getString("ImageSource"));
                            contentData.setPageType(data.getString("PageType"));
                            contentData.setPageTypeId(data.getString("PageTypeId"));

                            homeSubCategoriesArray.add(contentData);
                        }
                        homeContentData.setHomeCatContentDatas(homeSubCategoriesArray);
                        homeCatItems.setHomeContentData(homeContentData);
                        break;
                    case SLIDER:
                        JSONArray sliContent = jsonArray.getJSONObject(i).getJSONArray("ContentData");
                        homeContentData = new HomeCatContentData();

                        homeSubCategoriesArray = new ArrayList<>();
                        for (int k = 0; k < sliContent.length(); k++) {

                            JSONObject data = sliContent.getJSONObject(k);
                            HomeCatContentData contentData = new HomeCatContentData();
                            contentData.setCategoryId(data.getString("SubCategoryId"));
                            contentData.setCategoryName("");
                            contentData.setTitle(data.getString("Title"));
                            contentData.setImageSource(data.getString("ImageSource"));
                            contentData.setPageType(data.getString("PageType"));
                            contentData.setPageTypeId(data.getString("PageTypeId"));

                            homeSubCategoriesArray.add(contentData);
                        }
                        homeContentData.setHomeCatContentDatas(homeSubCategoriesArray);
                        homeCatItems.setHomeContentData(homeContentData);
                        break;
                    default:
                        break;
                }
                list.add(homeCatItems);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return list;
    }

    public static ArrayList<HomeCatItems> getSubCatContent(JSONObject mResponse){
        final ArrayList<HomeCatItems> list = new ArrayList<>();

        try {

            JSONObject response = mResponse;
            JSONArray jsonArray = response.getJSONArray("Data");
            HomeCatItems homeCatItems;
            for (int i = 0; i < jsonArray.length(); i++) {

                homeCatItems = new HomeCatItems();
                homeCatItems.setContentTypeId(jsonArray.getJSONObject(i).getString("ContentTypeId"));
                homeCatItems.setContentType(jsonArray.getJSONObject(i).getString("ContentType"));
                JSONObject object = jsonArray.getJSONObject(i).getJSONObject("ContentData");

                HomeCatContentData homeContentData = new HomeCatContentData();
                homeContentData.setCategoryId(object.getString("SubCategoryId"));
                homeContentData.setCategoryName(object.getString("CategoryName"));
                homeContentData.setImageSource(object.getString("ImageSource"));
                homeContentData.setPageTypeId(object.getString("PageTypeId"));
                homeContentData.setTitle(object.getString("Title"));

                homeCatItems.setHomeContentData(homeContentData);
                list.add(homeCatItems);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return list;
    }


    public static ArrayList <BasicModel> getNavList(String SavedCaTList){
        final ArrayList<BasicModel> navCatList = new ArrayList<>();

        try {
            if(SavedCaTList.length()>0) {
                JSONObject response = new JSONObject(SavedCaTList);
                JSONArray jsonArray = response.getJSONArray("Data");
                BasicModel navCatModel;
                for (int i = 0; i < jsonArray.length(); i++) {
                    String contentTypeId = jsonArray.getJSONObject(i).getString("ContentTypeId");
                    switch (contentTypeId) {
                        case TRAPE:
                            JSONObject object = jsonArray.getJSONObject(i).getJSONObject("ContentData");
                            if (object.getString("PageTypeId").equals("2") || object.getString("PageTypeId").equals("3")) {
                                navCatModel = new BasicModel();
                                navCatModel.setId(object.getString("MainCategoryId"));
                                navCatModel.setCategory(object.getString("CategoryName"));
                                navCatModel.setPageTypeId(object.getString("PageTypeId"));
                                navCatList.add(navCatModel);
                            }
                            break;
                    }
                }
            }
            else{

            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return navCatList;
/*




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
        return  filterDataModels;*/
    }
}
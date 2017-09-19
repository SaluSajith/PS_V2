package com.hit.pretstreet.pretstreet.navigationitems.controllers;

import android.content.Context;
import android.location.Location;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.SharedPreferencesHelper;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.navigationitems.NavigationItemsActivity;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LoginCallbackInterface;
import com.hit.pretstreet.pretstreet.splashnlogin.models.LoginSession;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by User on 16/08/2017.
 */

public class NavItemsController {

    private static Context context;
    private static LoginCallbackInterface loginCallbackInterface;

    private static final int ADDSTORE_FRAGMENT = 3;
    private static final int ACCOUNT_FRAGMENT = 0;
    private static final int CHANGEPASSWORD_FRAGMENT = 13;

    public NavItemsController(Context context) {
        this.context = context;
        loginCallbackInterface = (NavigationItemsActivity)context;
    }

    public static void validateAddStoreFields(EdittextPret edt_storename,
                                              EdittextPret edt_name,
                                              EdittextPret edt_email,
                                              EdittextPret edt_landline,
                                              EdittextPret edt_mobile,
                                              EdittextPret edt_about,
                                              EdittextPret edt_location, Location loc) {

        String storename = edt_storename.getText().toString().trim();
        String name = edt_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String landline = edt_landline.getText().toString().trim();
        String mobile = edt_mobile.getText().toString().trim();
        String location = edt_location.getText().toString().trim();
        String about = edt_about.getText().toString().trim();

        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        if (storename.length() < 1) {
            edittextPret = edt_storename;
            message = "Invalid name";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        } else if (name.length() < 1) {
            edittextPret = edt_name;
            message = "Invalid name";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        }  else if (!Utility.validCellPhone(landline)) {
            edittextPret = edt_landline;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        } else if (!Utility.validCellPhone(mobile)) {
            edittextPret = edt_mobile;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        }  else if (email.length() < 1) {
            edittextPret = edt_email;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        }  else if (!Utility.isValidEmail(email)) {
            edittextPret = edt_email;
            message = "Invalid email";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        }  else if (location.length() < 6) {
            edittextPret = edt_location;
            message = "Invalid Location";
            loginCallbackInterface.validateCallback(edittextPret, message, ADDSTORE_FRAGMENT);
            return;
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("StoreName", storename);
                jsonBody.put("ContactPersonName", name);
                jsonBody.put("Email", email);
                jsonBody.put("LandlineNumber", landline);
                jsonBody.put("Latitude", loc.getLatitude());
                jsonBody.put("Longitude", loc.getLongitude());
                jsonBody.put("MobileNumber", mobile);
                jsonBody.put("AboutStore", about);

                jsonBody = Constant.addConstants(jsonBody, context);

            } catch (JSONException e) {
            } catch (Exception e) {
            }
            loginCallbackInterface.validationSuccess(jsonBody, ADDSTORE_FRAGMENT);
        }
    }

    public static void validateRegisterFields(EdittextPret edt_firstname,
                                              EdittextPret edt_lastname,
                                              EdittextPret edt_email,
                                              EdittextPret edt_dob,
                                              EdittextPret edt_mobile) {

        String firstname = edt_firstname.getText().toString().trim();
        String lname = edt_lastname.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String dob = edt_dob.getText().toString().trim();
        String mobile = edt_mobile.getText().toString().trim();

        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        if (firstname.length() < 1) {
            edittextPret = edt_firstname;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, ACCOUNT_FRAGMENT);
            return;
        } else if (lname.length() < 1) {
            edittextPret = edt_lastname;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, ACCOUNT_FRAGMENT);
            return;
        } /* else if (!Utility.validCellPhone(mobile)) {
            edittextPret = edt_mobile;
            message = "Invalid phone number";
            loginCallbackInterface.validateCallback(edittextPret, message, ACCOUNT_FRAGMENT);
            return;
        }  */else if (email.length() < 1) {
            edittextPret = edt_email;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, ACCOUNT_FRAGMENT);
            return;
        }  else if (!Utility.isValidEmail(email)) {
            edittextPret = edt_email;
            message = "Invalid email";
            loginCallbackInterface.validateCallback(edittextPret, message, ACCOUNT_FRAGMENT);
            return;
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("FirstName", firstname);
                jsonBody.put("LastName", lname);
                jsonBody.put("UserEmail", email);
                jsonBody.put("UserMobile", mobile);
                jsonBody.put("DOB", dob);

                jsonBody = Constant.addConstants(jsonBody, context);

            } catch (JSONException e) {
            } catch (Exception e) {
            }

            loginCallbackInterface.validationSuccess(jsonBody, ACCOUNT_FRAGMENT);
        }
    }

    public static void validateUpdatePassFields(EdittextPret et_currPas,
                                      EdittextPret et_newPas,
                                        EdittextPret et_confPas) {
        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        String password = et_currPas.getText().toString();
        String new_password = et_newPas.getText().toString();
        String confirm_password = et_confPas.getText().toString();

        if (password.length() < 1) {
            edittextPret = et_currPas;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, CHANGEPASSWORD_FRAGMENT);
            return;
        }
        else if (new_password.length() < 1) {
            edittextPret = et_newPas;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, CHANGEPASSWORD_FRAGMENT);
            return;
        }
        else if (confirm_password.length() < 1) {
            edittextPret = et_confPas;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, CHANGEPASSWORD_FRAGMENT);
            return;
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("UserOldPassword", password);
                jsonBody.put("UserNewPassword", new_password);
                jsonBody.put("UserConfirmNewPassword", confirm_password);
                jsonBody = Constant.addConstants(jsonBody, context);

            } catch (JSONException e) {
            } catch (Exception e) {
            }
            loginCallbackInterface.validationSuccess(jsonBody, CHANGEPASSWORD_FRAGMENT);
        }
    }

    public static void validateFields(EdittextPret et_data, int id) {
        String message = "Fields cannot be empty";
        EdittextPret edittextPret ;

        String data = et_data.getText().toString();
        if (data.length() < 1) {
            edittextPret = et_data;
            message = "Fields cannot be empty";
            loginCallbackInterface.validateCallback(edittextPret, message, id);
            return;
        } else {
            loginCallbackInterface.validationSuccess(data);
        }
    }

    public static JSONObject getContactUsJson(String message) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Message", message);
            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static JSONObject getNoitficationlistJson(String catId, String pagecount, String pageid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("CategoryId", catId);
            jsonBody.put("Pageid", pagecount);
            jsonBody.put("PreviousPageTypeId", pageid);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }


    public static JSONObject getStaticPageJson() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody = Constant.addConstants(jsonBody, context);
        } catch (Exception e) {
        }

        return jsonBody;
    }
    public static JSONObject getFollowinglistJson(String catId, String pagecount, String pageid) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Limit", Constant.LIMIT);
            jsonBody.put("CategoryId", catId);
            jsonBody.put("Pageid", pagecount);
            jsonBody.put("PreviousPageTypeId", pageid);
            jsonBody.put("ClickTypeId", "");

            jsonBody = Constant.addConstants(jsonBody, context);

        } catch (JSONException e) {
        } catch (Exception e) {
        }

        return jsonBody;
    }

    public static ArrayList<SearchModel> getCategoryListHeader(JSONObject response){
        ArrayList<SearchModel> searchModels = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("Category");
            SearchModel searchModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                searchModel = new SearchModel();
                searchModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                searchModel.setCategory(jsonArray.getJSONObject(i).getString("CategoryName"));
                searchModels.add(searchModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  searchModels;
    }

    public static String getStaticHtmlData(JSONObject response){
        String html = null;
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            html = jsonObject.getString("Content");
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        return html;
    }

    public static ArrayList <StoreListModel> getCategoryList(JSONObject response){
        ArrayList<StoreListModel> storeListModels = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("Following");
            StoreListModel storeListModel;
            for (int i = 0; i < jsonArray.length(); i++) {
                storeListModel = new StoreListModel();
                storeListModel.setPageType(jsonArray.getJSONObject(i).getString("PageType"));
                storeListModel.setPageTypeId(jsonArray.getJSONObject(i).getString("PageTypeId"));
                storeListModel.setId(jsonArray.getJSONObject(i).getString("Id"));
                storeListModel.setTitle(jsonArray.getJSONObject(i).getString("Title"));
                storeListModel.setFollowingStatus(true);
                storeListModel.setOpenStatus(jsonArray.getJSONObject(i).getInt("OpenStatus") == 1 ? false : true);
                storeListModel.setFollowingCount(jsonArray.getJSONObject(i).getString("FollowingCount"));
                storeListModel.setLocation(jsonArray.getJSONObject(i).getString("Location"));
                storeListModel.setImageSource(jsonArray.getJSONObject(i).getString("ImageSource"));
                String flag = jsonArray.getJSONObject(i).getString("Flags");
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
                storeListModel.setBannerFlag(jsonArray.getJSONObject(i).getInt("BannerFlag") == 0 ? false : true);

                storeListModels.add(storeListModel);
            }
        }catch (JSONException e1) {
            e1.printStackTrace();
        }

        return  storeListModels;
    }
}

package com.hit.pretstreet.pretstreet.navigation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.customview.EdittextPret;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.navigation.controllers.HomeFragmentController;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.splashnlogin.DefaultLocationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.EXHIBITIONSEARCH_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.TRENDING_URL;

/**
 * Created by User on 15/02/2018.
 * Foe searching exhibition by name
 *  * @author SVS
 */

public class ExhibitionSearchActivity extends AbstractBaseAppCompatActivity
        implements ApiListenerInterface {

    @BindView(R.id.edt_search) EdittextPret edt_search;
    @BindView(R.id.lv_exhibitions) ListView lv_exhibitions;

    ArrayList<BasicModel> resultModels;

    JsonRequestController jsonRequestController;
    HomeFragmentController homeFragmentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibitionsearch);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        PreferenceServices.init(this);

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strSearch = s.toString();
                if(strSearch.length()>=2)
                    getSearchResults(strSearch);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

        });
    }

    @OnClick(R.id.img_close)
    public void onClose(){
        onBackPressed();
    }

    /** Get Search Results*/
    public void getSearchResults(String str){
        JSONObject resultJson = homeFragmentController.getExhibitionSeach(str);
        jsonRequestController.sendRequest(this, resultJson, EXHIBITIONSEARCH_URL);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        homeFragmentController = new HomeFragmentController(this);
    }

    @Override
    public void onResponse(JSONObject response) {
        hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {

    }

    /**Handling response corresponding to the URL
     * @param response response corresponding to each URL - here I am appending the URL itself
     *                 to the response so that I will be able to handle each response seperately*/
    private void handleResponse(JSONObject response) {
        try {
            String url = response.getString("URL");
            switch (url) {
                case EXHIBITIONSEARCH_URL:
                    ArrayList resultList = homeFragmentController.getExhibitionSearchNameList(response);
                    resultModels = homeFragmentController.getExhibitionSearchList(response);
                    setAdapter(resultList);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
        }
    }

    private void setAdapter(ArrayList resultList){
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(),
                R.layout.row_nav_text, R.id.tv_nav_item, resultList);
        lv_exhibitions.setAdapter(adapter);
        lv_exhibitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = (String) resultModels.get(i).getId();

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putString(ID_KEY, id);
                b.putString(PARCEL_KEY, "1");
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private class doInBackground extends AsyncTask<Void, Void, Boolean> {
        ArrayList resultList = new ArrayList();
        ArrayAdapter adapter;
        @Override
        protected Boolean doInBackground(Void... voids) {
            // Retrieve the autocomplete results.
            //resultList = autocomplete(strSearch);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (resultList==null) {
                displaySnackBar("Please check your internet connection!");
            } else {
                adapter = new ArrayAdapter(getBaseContext(),
                        R.layout.row_nav_text, R.id.tv_nav_item, resultList);
                lv_exhibitions.setAdapter(adapter);
                lv_exhibitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String str = (String) resultList.get(i);
                        lv_exhibitions.setAdapter(null);
                    }
                });
            }
        }
    }
}

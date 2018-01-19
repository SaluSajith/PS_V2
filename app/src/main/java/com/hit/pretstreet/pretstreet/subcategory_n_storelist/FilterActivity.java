package com.hit.pretstreet.pretstreet.subcategory_n_storelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.apis.JsonRequestController;
import com.hit.pretstreet.pretstreet.core.apis.interfaces.ApiListenerInterface;
import com.hit.pretstreet.pretstreet.core.utils.Constant;
import com.hit.pretstreet.pretstreet.core.utils.PreferenceServices;
import com.hit.pretstreet.pretstreet.core.utils.Utility;
import com.hit.pretstreet.pretstreet.core.views.AbstractBaseAppCompatActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters.FilterSectionAdapter;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.controllers.SubCategoryController;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.FilterCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hit.pretstreet.pretstreet.core.utils.Constant.FILTER_URL;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.ID_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PARCEL_KEY;
import static com.hit.pretstreet.pretstreet.core.utils.Constant.PRE_PAGE_KEY;


public class FilterActivity extends AbstractBaseAppCompatActivity implements
        ApiListenerInterface, FilterCallback {

    @BindView(R.id.rv_Search)RecyclerView rv_Filter;
    private ArrayList<TwoLevelDataModel> dataModels;

    JsonRequestController jsonRequestController;
    SubCategoryController subCategoryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        PreferenceServices.init(this);
        getFilterlist();
    }

    private void getFilterlist(){
        String pageid = getIntent().getStringExtra(PRE_PAGE_KEY);
        String mCatid = getIntent().getStringExtra(ID_KEY);
        try {
            Bundle bundle = getIntent().getExtras();
            dataModels = (ArrayList<TwoLevelDataModel>) bundle.getSerializable(PARCEL_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dataModels.size()>0)
            setAdapter();
        else {
            JSONObject resultJson = subCategoryController.getFilterJson(mCatid, pageid);
            this.showProgressDialog(getResources().getString(R.string.loading));
            jsonRequestController.sendRequest(this, resultJson, FILTER_URL);
        }
    }

    private void setAdapter(){
        FilterSectionAdapter adapter = new FilterSectionAdapter(this, dataModels);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        Utility.setListLayoutManager(rv_Filter, FilterActivity.this);
        adapter.setLayoutManager(manager);
        rv_Filter.setAdapter(adapter);
    }

    @Override
    protected void setUpController() {
        jsonRequestController = new JsonRequestController(this);
        subCategoryController = new SubCategoryController(this);
    }

    @Override
    public void onResponse(JSONObject response) {
        this.hideDialog();
        handleResponse(response);
    }

    @Override
    public void onError(String error) {
        this.hideDialog();
        displaySnackBar(error);
    }

    private void handleResponse(JSONObject response) {
        try {
            String url = response.getString("URL");
            switch (url) {
                case Constant.FILTER_URL:
                    dataModels = subCategoryController.getFilterList(response);
                    setAdapter();
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_back)
    public void onBackPress(){
        onBackPressed();
    }

    @OnClick(R.id.btn_apply)
    public void onApplyPress(){
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARCEL_KEY, dataModels);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void updateStatus(ArrayList<TwoLevelDataModel> dataModel) {
        dataModels = dataModel;
    }
}

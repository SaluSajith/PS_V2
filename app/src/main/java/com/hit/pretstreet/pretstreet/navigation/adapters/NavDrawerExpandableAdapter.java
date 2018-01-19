package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.interfaces.NavigationClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;

import java.util.ArrayList;

/**
 * Created by User on 17/01/2018.
 */

public class NavDrawerExpandableAdapter extends BaseExpandableListAdapter {

    private static TwoLevelDataModel[] allData;
    private static Context mContext;
    private static NavigationClick navigationClick;
    private static ExpandableListView expandableListView;

    public NavDrawerExpandableAdapter(ExpandableListView expandableListView, Context context,
                                      TwoLevelDataModel[] data, NavigationClick navigationClick) {
        this.allData = data;
        this.mContext = context;
        this.navigationClick = navigationClick;
        this.expandableListView =  expandableListView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.allData[groupPosition].getAllItemsInSection().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_nav_text, null);
        }

        TextViewPret txtListChild = convertView
                .findViewById(R.id.tv_nav_item);
        final ArrayList<BasicModel> itemsInSection = allData[groupPosition].getAllItemsInSection();
        String itemName = itemsInSection.get(childPosition).getCategory();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity homeActivity = (HomeActivity) mContext;
                HomeCatContentData homeContentData = new HomeCatContentData();
                homeContentData.setPageTypeId(allData[groupPosition].getAllItemsInSection().get(childPosition).getPageTypeId());
                homeContentData.setCategoryId(allData[groupPosition].getAllItemsInSection().get(childPosition).getId());
                homeActivity.onTrapeClick(homeContentData, allData[groupPosition].getAllItemsInSection().get(childPosition).getCategory());
            }
        });

        txtListChild.setText(itemName);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return allData[groupPosition].getAllItemsInSection().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return allData[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return allData.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_nav_header, null);
        }
        TextViewPret lblListHeader = convertView
                .findViewById(R.id.tv_nav_item);
        String headerTitle = allData[groupPosition].getHeaderTitle();
        lblListHeader.setText(headerTitle);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allData[groupPosition].getAllItemsInSection().size()==0) {
                    if (navigationClick != null)
                        navigationClick.menuOnClick(allData[groupPosition].getId());
                }
                else{
                    if(expandableListView.isGroupExpanded(groupPosition))
                    expandableListView.collapseGroup(groupPosition);
                    else expandableListView.expandGroup(groupPosition);
                }
            }
        });

        AppCompatImageView appCompatImageView = convertView.findViewById(R.id.iv_arrow_expand);
        if(allData[groupPosition].getAllItemsInSection().size()>0)
            appCompatImageView.setVisibility(View.VISIBLE);
        else appCompatImageView.setVisibility(View.INVISIBLE);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
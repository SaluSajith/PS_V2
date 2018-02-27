package com.hit.pretstreet.pretstreet.splashnlogin.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.utils.SectionedRecyclerViewAdapter;
import com.hit.pretstreet.pretstreet.search.interfaces.RecentCallback;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.FilterActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.FilterCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 22/02/2018.
 */

public class PopularPlacesSectionedAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static List<TwoLevelDataModel> allData;
    private static RecentCallback recentCallback;

    /**Public constructor
     * @param data two dimensional array object*/
    public PopularPlacesSectionedAdapter(Context context, List<TwoLevelDataModel> data) {
        this.allData = data;
        this.recentCallback = (RecentCallback) context;
    }
    @Override
    public int getSectionCount() {
        return allData.size();
    }

    @Override
    public int getItemCount(int section) {
        return allData.get(section).getAllItemsInSection().size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        String sectionName = allData.get(section).getHeaderTitle();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.sectionTitle.setText(sectionName);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {

        final ArrayList<BasicModel> itemsInSection = allData.get(section).getAllItemsInSection();
        String itemName = itemsInSection.get(relativePosition).getTitle();
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.txt_storename.setText(itemName);
        itemViewHolder.txt_storename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recentCallback.viewClick(allData.get(section).getAllItemsInSection().get(relativePosition));
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, boolean header) {
        View v;
        if (header){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_filter_item_section, parent, false);
            return new SectionViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_popularplaces, parent, false);
            return new ItemViewHolder(v);
        }
    }


    // SectionViewHolder Class for Sections
    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        final TextView sectionTitle;
        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
        }
    }

    /**ItemViewHolder Class for Items in each Section*/
    public static class ItemViewHolder extends RecyclerView.ViewHolder {// implements View.OnClickListener{
        @BindView(R.id.txt_storename)TextViewPret txt_storename;
        @BindView(R.id.txt_address)TextViewPret txt_address;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //itemView.setOnClickListener(this);
            /*txt_storename.setOnClickListener(this);
            txt_address.setOnClickListener(this);*/
        }

        /*@Override
        public void onClick(View view) {
            recentCallback.viewClick(artItems.get(getAdapterPosition()));
        }*/
    }
}
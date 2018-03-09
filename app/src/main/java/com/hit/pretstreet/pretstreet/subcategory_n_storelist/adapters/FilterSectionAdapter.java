package com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.utils.SectionedRecyclerViewAdapter;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.FilterActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.FilterCallback;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.TwoLevelDataModel;

import java.util.ArrayList;
import java.util.List;
/**
 * Adapter section for Filter section
 * @author SVS
 **/
public class FilterSectionAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static List<TwoLevelDataModel> allData;
    FilterCallback filterCallback;

    /**Public constructor
     * @param data two dimensional array object*/
    public FilterSectionAdapter(Context context, List<TwoLevelDataModel> data) {
        this.allData = data;
        filterCallback = (FilterActivity) context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, final int relativePosition, int absolutePosition) {

        final ArrayList<BasicModel> itemsInSection = allData.get(section).getAllItemsInSection();
        String itemName = itemsInSection.get(relativePosition).getCategory();
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.cb_Item.setText(itemName);
        itemViewHolder.cb_Item.setChecked(itemsInSection.get(relativePosition).getStatus()== true ? true : false );
        itemViewHolder.cb_Item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                itemsInSection.get(relativePosition).setStatus(isChecked);
                filterCallback.updateStatus((ArrayList<TwoLevelDataModel>) allData);
            }}
        );
        // Try to put a image . for sample i set background color in xml layout file
        // itemViewHolder.itemImage.setBackgroundColor(Color.parseColor("#01579b"));
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
                    .inflate(R.layout.row_filter_item, parent, false);
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
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final CheckBox cb_Item;

        public ItemViewHolder(View itemView) {
            super(itemView);
            cb_Item = itemView.findViewById(R.id.cb_Item);

            /* itemView.setOnClickListener(new SView.OnClickListener() {
                @Override
                public void onClick(SView v) {
                    Toast.makeText(v.getContext(), cb_Item.getText(), Toast.LENGTH_SHORT).show();

                }
            });*/
        }
    }
}
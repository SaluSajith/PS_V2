package com.hit.pretstreet.pretstreet.search.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.search.models.SearchModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 14/08/2017.
 */

public class AutoSearchAdapter extends RecyclerView.Adapter<AutoSearchAdapter.AutoSearchViewHolder>{
    private Context context;
    ArrayList<SearchModel> artItems;

    public AutoSearchAdapter(Context context, ArrayList<SearchModel> artItems) {
        this.context = context;
        this.artItems = artItems;
    }

    @Override
    public int getItemCount() {
        return this.artItems != null ? artItems.size() : 0;
    }

    @Override
    public AutoSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_recent, parent, false);
        return new AutoSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AutoSearchViewHolder holder, int position) {

        SearchModel searchModel = artItems.get(position);
        holder.txt_storename.setText(searchModel.getTitle());
        holder.txt_address.setText(searchModel.getLocation());
        holder.txt_cat.setText(searchModel.getCategory());

    }

    public class AutoSearchViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_storename)TextViewPret txt_storename;
        @BindView(R.id.txt_address)TextViewPret txt_address;
        @BindView(R.id.txt_cat)TextViewPret txt_cat;

        public AutoSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

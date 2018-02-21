package com.hit.pretstreet.pretstreet.splashnlogin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.search.interfaces.RecentCallback;
import com.hit.pretstreet.pretstreet.search.models.BasicModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 20/02/2018.
 */

public class PopularPlacesAdapter extends RecyclerView.Adapter<PopularPlacesAdapter.AutoSearchViewHolder>{
    ArrayList<BasicModel> artItems;
    RecentCallback recentCallback;

    public PopularPlacesAdapter(Context context, ArrayList<BasicModel> artItems) {
        this.artItems = artItems;
        this.recentCallback = (RecentCallback) context;
    }

    @Override
    public int getItemCount() {
        return this.artItems != null ? artItems.size() : 0;
    }

    @Override
    public AutoSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_popularplaces, parent, false);
        return new AutoSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AutoSearchViewHolder holder, int position) {

        BasicModel searchModel = artItems.get(position);
        setViewText(holder.txt_storename, searchModel.getTitle());
       // setViewText(holder.txt_address, searchModel.getLocation());
    }

    private void setViewText(TextView textView, String text) {
        textView.setText(text);
    }
    public class AutoSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.txt_storename)TextViewPret txt_storename;
        @BindView(R.id.txt_address)TextViewPret txt_address;

        public AutoSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            txt_storename.setOnClickListener(this);
            txt_address.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recentCallback.viewClick(artItems.get(getAdapterPosition()));
        }
    }
}
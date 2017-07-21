package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.navigation.interfaces.NavigationClick;
import com.hit.pretstreet.pretstreet.navigation.models.NavDrawerItem;

/**
 * Created by User on 6/27/2017.
 */

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.MyViewHolder> {

    private final Context context;
    private NavDrawerItem data[] = null;
    private NavigationClick navigationClick;

    public NavDrawerAdapter(Context context, NavDrawerItem [] data, NavigationClick navigationClick) {
        this.context = context;
        this.data = data;
        this.navigationClick = navigationClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_nav_text, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem navDrawerItem = data[position];
        holder.tv_nav_item.setText(navDrawerItem.getName());
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_nav_item;

        public MyViewHolder(View view) {
            super(view);
            tv_nav_item = (TextView) view.findViewById(R.id.tv_nav_item);
            tv_nav_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (navigationClick != null) {
                navigationClick.menuOnClick(data[getAdapterPosition()].getId());
            }
        }
    }

}

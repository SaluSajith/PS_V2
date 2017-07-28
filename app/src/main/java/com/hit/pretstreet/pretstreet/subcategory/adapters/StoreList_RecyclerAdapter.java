package com.hit.pretstreet.pretstreet.subcategory.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.subcategory.models.StoreListModel;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/27/2017.
 */

public class StoreList_RecyclerAdapter extends RecyclerView.Adapter<StoreList_RecyclerAdapter.ShopsHolder >
{
    private Context context;
    int layoutResourceId;
    private ArrayList<StoreListModel> mItems;
    int followCount;

    public StoreList_RecyclerAdapter(Context context, int layoutResourceId,
                                     ArrayList<StoreListModel> storeListModels) {
        this.context = context;
        this.mItems = storeListModels;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public ShopsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mRootView;
        int listViewItemType = getItemViewType(viewType);
        if (listViewItemType % 2 == 0) {
            mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store1, parent, false);
        } else {
            mRootView = LayoutInflater.from(context).inflate(R.layout.row_list_store2, parent, false);
        }
        return new ShopsHolder(mRootView);
    }

    @Override
    public int getItemCount() {
       // return this.mItems != null ? mItems.size() : 0;
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        //return mItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        return position;
    }

    @Override
    public void onBindViewHolder(final ShopsHolder holder, final int position) {

        /*if(holder.matrix==null) {
            holder.matrix = holder.img_store_photo.getImageMatrix();
            holder.matrix.postScale(2,2);
        }
        else;
        if(position == mItems.size()-1) {
            if (pageCount < totalPages) {
                if(loadmore)
                    holder.ll_progress.setVisibility(View.VISIBLE);
                else holder.ll_progress.setVisibility(View.GONE);
            }
            else
                holder.ll_progress.setVisibility(View.GONE);
        }
        else
            holder.ll_progress.setVisibility(View.GONE);

        holder.txt_storename.setText(mItems.get(position).get("name"));
        holder.txt_address.setText(mItems.get(position).get("area"));

        String strFollowCount = mItems.get(position).get("follow_count");
        if (strFollowCount.length() >= 4) {
            String convertedCountK = strFollowCount.substring(0, strFollowCount.length() - 3);
            if (convertedCountK.length() >= 4) {
                String convertedCount = convertedCountK.substring(0, convertedCountK.length() - 3);
                holder.txt_folleowercount.setText(Html.fromHtml(convertedCount + "<sup>M</sup>"));
            } else {
                holder.txt_folleowercount.setText(Html.fromHtml(convertedCountK + "<sup>K</sup>"));
            }
        } else {
            holder.txt_folleowercount.setText(Html.fromHtml(strFollowCount));
        }

        if (position == 0) {
            LinearLayout.LayoutParams relativeParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            relativeParams.setMargins(0, 15, 10, 0);
            holder.tv_margintop.setLayoutParams(relativeParams);
            holder.tv_margintop.requestLayout();
        }

        Log.e("image", mItems.get(position).get("thumb") + "");
        Glide.with(context)
                .load(mItems.get(position).get("thumb"))
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(holder.img_store_photo) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        Bitmap croppedBmp = Bitmap.createBitmap(resource);
                        holder.img_store_photo.setImageMatrix(holder.matrix);
                        holder.img_store_photo.setImageBitmap(croppedBmp);
                    }
                });

        if (mItems.get(position).get("wishlist").equalsIgnoreCase("notin")) {
            holder.img_follow_unfollow.setText("Follow");
        } else {
            holder.img_follow_unfollow.setText("Unfollow");
        }

        if (mItems.get(position).get("sale").equalsIgnoreCase("Yes")) {
            holder.img_sale.setVisibility(View.VISIBLE);
        } else {
            holder.img_sale.setVisibility(View.GONE);
        }

        if (mItems.get(position).get("arrival").equalsIgnoreCase("No")) {
            holder.img_new_arrival.setVisibility(View.GONE);
        } else {
            holder.img_new_arrival.setVisibility(View.VISIBLE);
        }

        holder.img_follow_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followCount = Integer.parseInt(mItems.get(position).get("follow_count"));
                *//*if (mItems.get(position).get("wishlist").equalsIgnoreCase("notin")) {
                    addToFollowers(mItems.get(position).get("id"), position);
                } else {
                    removeFromFollowers(mItems.get(position).get("id"), position);
                }*//*
            }
        });*/

    }

    class ShopsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ll_progress) LinearLayout ll_progress;
        @BindView(R.id.img_follow_unfollow) TextViewPret img_follow_unfollow;
        @BindView(R.id.txt_storename) TextViewPret txt_storename;
        @BindView(R.id.txt_address) TextViewPret txt_address;
        @BindView(R.id.tv_margintop) TextViewPret tv_margintop;
        @BindView(R.id.txt_folleowercount) TextViewPret txt_folleowercount;
        @BindView(R.id.img_store_photo) ImageView img_store_photo;
        @BindView(R.id.img_sale) ImageView img_sale;
        @BindView(R.id.img_new_arrival) ImageView img_new_arrival;

        Matrix matrix;

        public ShopsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
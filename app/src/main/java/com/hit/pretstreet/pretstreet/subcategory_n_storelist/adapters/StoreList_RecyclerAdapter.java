package com.hit.pretstreet.pretstreet.subcategory_n_storelist.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.interfaces.ButtonClickCallbackStoreList;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.models.StoreListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/27/2017.
 */

public class StoreList_RecyclerAdapter extends RecyclerView.Adapter<StoreList_RecyclerAdapter.ShopsHolder >
{
    private Context context;
    private static ArrayList<StoreListModel> mItems;
    private static TextViewPret textViewPret;
    static int mPosition;

    ButtonClickCallbackStoreList buttonClickCallback;

    public StoreList_RecyclerAdapter(Context context,
                                     ArrayList<StoreListModel> storeListModels) {
        this.context = context;
        this.mItems = storeListModels;
        buttonClickCallback = (ButtonClickCallbackStoreList) context;
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
        return this.mItems != null ? mItems.size() : 0;
        //return 10;
    }

    @Override
    public int getItemViewType(int position) {
        //return mItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        return position;
    }

    @Override
    public void onBindViewHolder(final ShopsHolder holder, final int position) {

        if(holder.matrix==null) {
            holder.matrix = holder.img_store_photo.getImageMatrix();
            holder.matrix.postScale(2,2);
        }
        else;
        /*if(position == mItems.size()-1) {
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
        */
        holder.txt_storename.setText(mItems.get(position).getTitle());
        holder.txt_address.setText(mItems.get(position).getLocation());

        String strFollowCount = mItems.get(position).getFollowingCount();
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
/*

        if (position == 0) {
            LinearLayout.LayoutParams relativeParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            relativeParams.setMargins(0, 15, 10, 0);
            holder.tv_margintop.setLayoutParams(relativeParams);
            holder.tv_margintop.requestLayout();
        }
*/

        Glide.with(context)
                .load(mItems.get(position).getImageSource())
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(holder.img_store_photo) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        Bitmap croppedBmp = Bitmap.createBitmap(resource);
                        //holder.img_store_photo.setImageMatrix(holder.matrix);
                        holder.img_store_photo.setImageBitmap(croppedBmp);
                    }
                });

        holder.img_follow_unfollow.setText(mItems.get(position).getFollowingStatus() == true ?"Unfollow" : "Follow");
        holder.tv_closeStatus.setText(mItems.get(position).getOpenStatus() == true ? "Close" : "Open");
        holder.img_sale.setVisibility(mItems.get(position).getSaleflag() == true ? View.GONE : View.VISIBLE);
        holder.img_offer.setVisibility(mItems.get(position).getOfferflag() == true ? View.GONE : View.VISIBLE);
        holder.img_newarrival.setVisibility(mItems.get(position).getNewflag() == true ? View.GONE : View.VISIBLE);
    }

    public class ShopsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ll_progress) LinearLayout ll_progress;
        @BindView(R.id.img_follow_unfollow) TextViewPret img_follow_unfollow;
        @BindView(R.id.txt_storename) TextViewPret txt_storename;
        @BindView(R.id.txt_address) TextViewPret txt_address;
        @BindView(R.id.tv_margintop) TextViewPret tv_margintop;
        @BindView(R.id.txt_folleowercount) TextViewPret txt_folleowercount;
        @BindView(R.id.tv_closeStatus) TextViewPret tv_closeStatus;
        @BindView(R.id.img_store_photo) ImageView img_store_photo;
        @BindView(R.id.img_sale) ImageButton img_sale;
        @BindView(R.id.img_offer) ImageButton img_offer;
        @BindView(R.id.img_newarrival) ImageButton img_newarrival;

        Matrix matrix;

        public ShopsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textViewPret =  new TextViewPret(context);
            img_follow_unfollow.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    StoreListModel storeListModel = mItems.get(position);
                    buttonClickCallback.buttonClick(storeListModel);
                }
            });
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.img_follow_unfollow:
                    mPosition = getAdapterPosition();
                    textViewPret = (TextViewPret) view;
                    buttonClickCallback.updateFollowStatus(mItems.get(getAdapterPosition()).getId());
                    break;
                default: break;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static void updateFollowStatus(int status, String storeid) {
        textViewPret.setText(status == 0 ? "Unfollow" : "Follow");
        if(mItems.get(mPosition).getId().equals(storeid))
        mItems.get(mPosition).setFollowingStatus(status == 0 ? true : false);
    }
}
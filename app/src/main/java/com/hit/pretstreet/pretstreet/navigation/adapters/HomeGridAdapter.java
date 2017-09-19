package com.hit.pretstreet.pretstreet.navigation.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.fragments.HomeFragment;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;
import com.hit.pretstreet.pretstreet.navigationitems.FollowingActivity;
import com.hit.pretstreet.pretstreet.subcategory_n_storelist.StoreListingActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 24/08/2017.
 */

public class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.GridViewHolder>{

    private Context context;
    Fragment fragment;
    Activity activity;
    ArrayList<HomeCatContentData> homeSubCategoriesArray;
    HomeTrapeClick buttonClickCallback;
    private RelativeLayout.LayoutParams lp;

    private int type = 0;
    private int CAT_TYPE = 0;
    private int MOODS_TYPE = 1;

    public HomeGridAdapter(Context context, Fragment fragment, ArrayList<HomeCatContentData> homeSubCategoriesArray, int type) {
        this.context = context;
        this.fragment = fragment;
        this.activity = (HomeActivity)context;
        this.type = type;
        this.homeSubCategoriesArray = homeSubCategoriesArray;
        buttonClickCallback = (HomeActivity)context;
    }

    @Override
    public int getItemCount() {
        return this.homeSubCategoriesArray != null ? homeSubCategoriesArray.size() : 0;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        int listViewItemType = getItemViewType(viewType);
        if (listViewItemType % 2 == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_cat1, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_cat2, parent, false);
        }
        return new GridViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {

        HomeCatContentData contentData = homeSubCategoriesArray.get(position);
        holder.txt_cat_name.setText(contentData.getCategoryName());

        Bitmap mask1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_cat);
        Bitmap mask2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_cat1);
        if(position%2==0) {
            ((HomeFragment)fragment).loadImage(contentData.getImageSource(), holder.img_cat_image, mask1);
        }else{
            ((HomeFragment)fragment).loadImage(contentData.getImageSource(), holder.img_cat_image, mask2);
        }

        if(type == MOODS_TYPE) {
            lp.setMargins(40, (int) context.getResources().getDimension(R.dimen.padding_xlarge) + 3, 5, 0);
            holder.txt_cat_name.setLayoutParams(lp);
        }
        else{
            lp.setMargins(40,(int) context.getResources().getDimension(R.dimen.padding_standard), 5, 0);
            holder.txt_cat_name.setLayoutParams(lp);
        }
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_cat_image)ImageView img_cat_image;
        @BindView(R.id.img_cat_image1)ImageView img_cat_image1;
        @BindView(R.id.txt_cat_name)TextViewPret txt_cat_name;

        public GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            lp = (RelativeLayout.LayoutParams) txt_cat_name.getLayoutParams();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClickCallback.onTrapeClick(homeSubCategoriesArray.get(getAdapterPosition()),
                            homeSubCategoriesArray.get(getAdapterPosition()).getCategoryName());
                }
            });
        }
    }
}

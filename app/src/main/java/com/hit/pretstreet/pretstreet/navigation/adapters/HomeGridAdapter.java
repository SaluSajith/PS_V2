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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.navigation.HomeActivity;
import com.hit.pretstreet.pretstreet.navigation.interfaces.HomeTrapeClick;
import com.hit.pretstreet.pretstreet.navigation.models.HomeCatContentData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 24/08/2017.
 */

public class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.GridViewHolder>{

    private Context context;
    Activity activity;
    ArrayList<HomeCatContentData> homeSubCategoriesArray;
    HomeTrapeClick buttonClickCallback;

    public HomeGridAdapter(Context context, ArrayList<HomeCatContentData> homeSubCategoriesArray) {
        this.context = context;
        this.activity = (HomeActivity)context;
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_cat1, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {

        HomeCatContentData contentData = homeSubCategoriesArray.get(position);
        holder.txt_cat_name.setText(contentData.getCategoryName());

        Bitmap mask1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_cat);
        Bitmap shadow1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_shadow);
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        Bitmap mask2 = Bitmap.createBitmap(mask1, 0, 0, mask1.getWidth(), mask1.getHeight(), matrix, true);
        Bitmap shadow2 = Bitmap.createBitmap(shadow1, 0, 0, shadow1.getWidth(), shadow1.getHeight(), matrix, true);
        if(position % 2 ==0) {
            loadImage(contentData.getImageSource(), holder.img_cat_image, mask1);
            BitmapDrawable ob = new BitmapDrawable(shadow1);
            holder.img_cat_image.setBackgroundDrawable(ob);
        } else {
            loadImage(contentData.getImageSource(), holder.img_cat_image, mask2);
            BitmapDrawable ob = new BitmapDrawable(shadow2);
            holder.img_cat_image.setBackgroundDrawable(ob);
        }
    }

    private void loadImage(String url, final ImageView imageView, final Bitmap mask){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int dwidth = displaymetrics.widthPixels;
        final int dheight = (int) ((displaymetrics.heightPixels) * 0.45);

        Glide.with(context)
                .load(url).asBitmap()
                .placeholder(R.drawable.mask_home)
                .centerCrop()
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        float scaleWidth = ((float) dwidth) / width;
                        float scaleHeight = ((float) dheight) / height;
                        Matrix matrix = new Matrix();
                        if (width > height)
                            if (scaleHeight > scaleWidth)
                                matrix.postScale(scaleWidth, scaleWidth);
                            else
                                matrix.postScale(scaleHeight, scaleHeight);
                        else {
                            if (scaleHeight > scaleWidth)
                                matrix.postScale(scaleHeight, scaleHeight);
                            else
                                matrix.postScale(scaleWidth, scaleWidth);
                        }
                        Bitmap resizedBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false);
                        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas mCanvas = new Canvas(result);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                        //mCanvas.drawBitmap(resource, 0, 0, null);
                        mCanvas.drawBitmap(resizedBitmap, 0, 0, null);
                        mCanvas.drawBitmap(mask, 0, 0, paint);
                        paint.setXfermode(null);
                        imageView.setImageBitmap(result);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                });
    }
    public class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_cat_image)ImageView img_cat_image;
        @BindView(R.id.txt_cat_name)TextViewPret txt_cat_name;
        @BindView(R.id.direction_card_view)CardView direction_card_view;

        public GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

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

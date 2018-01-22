package com.hit.pretstreet.pretstreet.core.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hit.pretstreet.pretstreet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/5/2017.
 */

public class PageState extends FrameLayout {

    private static final int SUCCESS = 0;
    private static final int LOADING = 1;
    private static final int ERROR = 2;
    private static final int NETWORK = 3;
    @BindView(R.id.loading_tv)
    TextView loadingTv;
    private Context context;
    private View mainView;
    private View loadingView;
    private View errorView;
    private View networkErrorView;
    private boolean isErrorState;
    private boolean isLoading;
    private PageStateListener pageStateListener;

    public PageState(Context context) {
        super(context);
        this.context = context;
    }

    public PageState(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void loadView(Activity activity, View view) {
        this.mainView = view;
        this.addView(this.mainView, 0);
        activity.setContentView(this);
        initPages();
    }

    @SuppressLint("InflateParams")
    private void initPages() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        this.loadingView = layoutInflater.inflate(R.layout.loading_view, null, false);
        this.loadingView.setVisibility(GONE);
        addView(this.loadingView);
        ButterKnife.bind(this, this.loadingView);

        this.errorView = layoutInflater.inflate(R.layout.error_view, null, false);
        this.errorView.setVisibility(GONE);
        addView(this.errorView);

        this.networkErrorView = layoutInflater.inflate(R.layout.empty_view, null, false);
        AppCompatImageView networkImage = this.networkErrorView.findViewById(R.id.network_error);
        networkImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageStateListener != null) {
                    pageStateListener.onErrorIconClicked();
                }
            }
        });
        this.networkErrorView.setVisibility(GONE);
        addView(this.networkErrorView);
    }

    private void show(int state) {
        isErrorState = false;
        int success = INVISIBLE, error = GONE, loading = GONE, network = GONE;
        switch (state) {
            case SUCCESS:
                success = VISIBLE;
                isLoading = false;
                break;
            case ERROR:
                isErrorState = true;
                isLoading = false;
                error = VISIBLE;
                break;
            case LOADING:
                loading = VISIBLE;
                isLoading = true;
                break;
            case NETWORK:
                isErrorState = true;
                isLoading = false;
                network = VISIBLE;
                break;
        }
        this.mainView.setVisibility(success);
        this.errorView.setVisibility(error);
        this.loadingView.setVisibility(loading);
        this.networkErrorView.setVisibility(network);
    }

    public void onLoading() {
        if (this.loadingTv != null) {
            this.loadingTv.setText(context.getString(R.string.loading));
        }
        show(LOADING);
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void onLoading(String loadingText) {
        if (this.loadingTv != null) {
            this.loadingTv.setText(loadingText);
        }
        show(LOADING);
    }

    public void onSuccess() {
        show(SUCCESS);
    }

    public void onError() {
        show(ERROR);
    }

    public void onNetworkError() {
        show(NETWORK);
    }

    public boolean isErrorState() {
        return this.isErrorState;
    }

    public void setOnPageStateListner(PageStateListener onPageStateListner) {
        this.pageStateListener = onPageStateListner;
    }

    public interface PageStateListener {
        void onErrorIconClicked();
    }
}

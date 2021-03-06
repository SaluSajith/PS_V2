package com.hit.pretstreet.pretstreet.core.customview.monthPicker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.hit.pretstreet.pretstreet.R;
import com.hit.pretstreet.pretstreet.core.customview.ButtonPret;
import com.hit.pretstreet.pretstreet.core.customview.TextViewPret;
import com.hit.pretstreet.pretstreet.core.customview.monthPicker.listener.DateMonthDialogListener;
import com.hit.pretstreet.pretstreet.core.customview.monthPicker.listener.OnCancelMonthDialogListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 08/02/2018.
 * Class for displaying month and year picker
 */


public class RackMonthPicker {
    private AlertDialog mAlertDialog;
    private RackMonthPicker.Builder builder;
    private Context context;
    private ButtonPret mPositiveButton;
    private ButtonPret mNegativeButton;
    private DateMonthDialogListener dateMonthDialogListener;
    private OnCancelMonthDialogListener onCancelMonthDialogListener;
    private boolean isBuild = false;

    public RackMonthPicker(Context context) {
        this.context = context;
        builder = new Builder();
    }

    public void show() {
        if (isBuild) {
            mAlertDialog.show();
        } else {
            builder.build();
            isBuild = true;
        }
    }

    public RackMonthPicker setPositiveButton(DateMonthDialogListener dateMonthDialogListener) {
        this.dateMonthDialogListener = dateMonthDialogListener;
        mPositiveButton.setOnClickListener(builder.positiveButtonClick());
        return this;
    }

    public RackMonthPicker setNegativeButton(OnCancelMonthDialogListener onCancelMonthDialogListener) {
        this.onCancelMonthDialogListener = onCancelMonthDialogListener;
        mNegativeButton.setOnClickListener(builder.negativeButtonClick());
        return this;
    }

    public RackMonthPicker setPositiveText(String text) {
        mPositiveButton.setText(text);
        return this;
    }

    public RackMonthPicker setNegativeText(String text) {
        mNegativeButton.setText(text);
        return this;
    }

    public RackMonthPicker setLocale(Locale locale) {
        builder.setLocale(locale);
        return this;
    }

    public RackMonthPicker setSelectedMonth(int index) {
        builder.setSelectedMonth(index);
        return this;
    }

    public RackMonthPicker setSelectedYear(int year) {
        builder.setSelectedYear(year);
        return this;
    }

    public RackMonthPicker setColorTheme(int color) {
        builder.setColorTheme(color);
        return this;
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }

    private class Builder implements MonthAdapter.OnSelectedListener {

        private MonthAdapter monthAdapter;
        private TextViewPret mTitleView;
        private TextViewPret mYear;
        private int year = 2017;
        private AlertDialog.Builder alertBuilder;
        private View contentView;
        ButtonPret next, previous;

        private Builder() {
            alertBuilder = new AlertDialog.Builder(context);

            contentView = LayoutInflater.from(context).inflate(R.layout.dialog_moth_picker, null);
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);

            mTitleView = contentView.findViewById(R.id.title);
            mYear = contentView.findViewById(R.id.text_year);

            next = contentView.findViewById(R.id.btn_next);
            next.setOnClickListener(nextButtonClick());

            previous = contentView.findViewById(R.id.btn_previous);
            previous.setOnClickListener(previousButtonClick());

            mPositiveButton = contentView.findViewById(R.id.btn_p);
            mNegativeButton = contentView.findViewById(R.id.btn_n);

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);

            monthAdapter = new MonthAdapter(context, this);
            RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(monthAdapter);

            mTitleView.setText(monthAdapter.getShortMonth() + ", " + year);
            mYear.setText(year + "");
        }

        public void setLocale(Locale locale) {
            monthAdapter.setLocale(locale);
        }

        public void setSelectedMonth(int index) {
            monthAdapter.setSelectedItem(index);
            mTitleView.setText(monthAdapter.getShortMonth() + ", " + year);
        }

        public void setSelectedYear(int year) {
            this.year = year;
            mYear.setText(year + "");
            mTitleView.setText(monthAdapter.getShortMonth() + ", " + year);
        }

        public void setColorTheme(int color) {
            LinearLayout linearToolbar = contentView.findViewById(R.id.linear_toolbar);
            linearToolbar.setBackgroundResource(color);

            monthAdapter.setBackgroundMonth(color);
            mPositiveButton.setTextColor(ContextCompat.getColor(context, color));
            mNegativeButton.setTextColor(ContextCompat.getColor(context, color));
        }

        public void build() {
            mAlertDialog = alertBuilder.create();
            mAlertDialog.show();
            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
            mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mAlertDialog.getWindow().setBackgroundDrawableResource(R.drawable.material_dialog_window);
            mAlertDialog.getWindow().setContentView(contentView);
        }

        public View.OnClickListener nextButtonClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int thisYear = cal.get(Calendar.YEAR);
                    if (year <= thisYear+5) {
                        next.setVisibility(View.VISIBLE);
                        previous.setVisibility(View.VISIBLE);
                        year++;
                        mYear.setText(year + "");
                        mTitleView.setText(monthAdapter.getShortMonth() + ", " + year);
                    }
                    else {
                        previous.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                    }
                }
            };
        }

        public View.OnClickListener previousButtonClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (year >= 2018) {
                        next.setVisibility(View.VISIBLE);
                        previous.setVisibility(View.VISIBLE);
                        year--;
                        mYear.setText(year + "");
                        mTitleView.setText(monthAdapter.getShortMonth() + ", " + year);
                    }
                    else {
                        next.setVisibility(View.VISIBLE);
                        previous.setVisibility(View.GONE);
                    }
                }
            };
        }

        public View.OnClickListener positiveButtonClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dateMonthDialogListener.onDateMonth(
                            monthAdapter.getMonth(),
                            monthAdapter.getStartDate(),
                            monthAdapter.getEndDate(),
                            year, mTitleView.getText().toString());

                    mAlertDialog.dismiss();
                }
            };
        }

        public View.OnClickListener negativeButtonClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCancelMonthDialogListener.onCancel(mAlertDialog);
                }
            };
        }

        @Override
        public void onContentSelected() {
            mTitleView.setText(monthAdapter.getShortMonth() + ", " + year);
        }
    }
}